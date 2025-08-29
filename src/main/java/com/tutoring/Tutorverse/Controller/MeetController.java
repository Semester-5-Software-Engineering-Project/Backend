package com.tutoring.Tutorverse.Controller;


import com.tutoring.Tutorverse.Model.User;
import com.tutoring.Tutorverse.Repository.userRepository;
import com.tutoring.Tutorverse.Services.JwtServices;
import com.tutoring.Tutorverse.Services.MeetingService;
import com.tutoring.Tutorverse.Services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class MeetController {

    private static final Logger logger = LoggerFactory.getLogger(MeetController.class);

    private final JwtServices jwtServices;
    @Autowired
    private userRepository userRepo;

    @Autowired
    private MeetingService meetingService;
    @Autowired
    private UserService userService;

    public MeetController(JwtServices jwtServices) {
        this.jwtServices = jwtServices;
    }

    @PostMapping("/create/meet")
    public ResponseEntity<?> meet(HttpServletRequest req){
        logger.info("=== CREATE MEET API STARTED ===");
        logger.info("Request URL: {}", req.getRequestURL());
        logger.info("Request Method: {}", req.getMethod());
        logger.info("Remote Address: {}", req.getRemoteAddr());

        try{
            String token = null;
            User user = null;

            logger.info("Step 1: Checking for cookies in request");
            if(req.getCookies() != null) {
                logger.info("Found {} cookies in request", req.getCookies().length);

                for (Cookie cookie : req.getCookies()) {
                    logger.info("Processing cookie: {} = {}", cookie.getName(),
                               cookie.getValue().substring(0, Math.min(20, cookie.getValue().length())) + "...");

                    token = cookie.getValue();
                    logger.info("Step 2: Extracting user ID from JWT token");

                    try {
                        Long id = jwtServices.getIdFromJwtToken(token);
                        logger.info("Extracted user ID from token: {}", id);

                        logger.info("Step 3: Looking up user in database with ID: {}", id);
                        Optional<User> userOpt = userRepo.findById(id);

                        if(userOpt.isPresent()){
                            user = userOpt.get();
                            logger.info("Step 4: User found successfully - ID: {}, Name: {}, Email: {}, Role: {}",
                                       user.getId(), user.getName(), user.getEmail(),
                                       user.getRole() != null ? user.getRole().getName() : "No Role");
                            break;
                        } else {
                            logger.warn("User not found in database for ID: {}", id);
                        }
                    } catch (Exception e) {
                        logger.error("Error extracting ID from token: {}", e.getMessage());
                        continue; // Try next cookie
                    }
                }
            } else {
                logger.warn("No cookies found in request");
            }

            if(user == null){
                logger.error("Step 5: Authentication failed - User is null");
                logger.error("=== CREATE MEET API FAILED - UNAUTHORIZED ===");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or invalid token");
            }

            logger.info("Step 5: Authentication successful, generating meeting token");
            logger.info("User details for meeting: ID={}, Name={}, Email={}, Role={}",
                       user.getId(), user.getName(), user.getEmail(),
                       user.getRole() != null ? user.getRole().getName() : "No Role");

            String meetToken = meetingService.generateToken(user);
            logger.info("Step 6: Meeting token generated successfully (length: {} chars)", meetToken.length());

            String jitsiUrl = "https://jit.shancloudservice.com/?jwt=" + meetToken;
            logger.info("Step 7: Jitsi URL constructed: {}", jitsiUrl);

            logger.info("Step 8: Returning meeting token to client");
            logger.info("=== CREATE MEET API COMPLETED SUCCESSFULLY ===");
            return ResponseEntity.ok().body(meetToken);

        }catch (Exception e) {
            logger.error("=== CREATE MEET API FAILED - EXCEPTION ===");
            logger.error("Exception type: {}", e.getClass().getSimpleName());
            logger.error("Exception message: {}", e.getMessage());
            logger.error("Exception stack trace: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token or authentication failed");
        }
    }
}
