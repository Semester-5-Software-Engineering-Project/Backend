package com.tutoring.Tutorverse.Controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tutoring.Tutorverse.Dto.EnrollDto;
import com.tutoring.Tutorverse.Services.EnrollmentService;
import com.tutoring.Tutorverse.Services.JwtServices;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@RestController
@RequestMapping("/api/enrollment")
public class EnrollmentController {

    @Autowired
    private EnrollmentService enrollmentService;

    @Autowired
    private JwtServices jwtServices;

    @PostMapping("/enroll")
    public ResponseEntity<String> enrollStudent(@RequestBody EnrollDto enrollDto, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if(authHeader == null || jwtServices.validateJwtToken(authHeader.substring(7))==false) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            enrollDto.setStudentId(getUserId(authHeader));
            String message = enrollmentService.EnrollTOModule(enrollDto);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error enrolling student: " + e.getMessage());
        }
    }

    @DeleteMapping("/unenroll/{enrollmentId}")
    public ResponseEntity<String> unenrollStudent(@PathVariable UUID enrollmentId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            if(authHeader == null || jwtServices.validateJwtToken(authHeader.substring(7))==false) {
                return ResponseEntity.status(401).body("Unauthorized");
            }
            enrollmentService.unenrollFromModule(enrollmentId);
            return ResponseEntity.ok("Unenrolled Successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error unenrolling student: " + e.getMessage());
        }
    }

    private UUID getUserId (String authHeader) {
        try{
            String token = authHeader.substring(7);
            return jwtServices.getUserIdFromJwtToken(token);
        } catch (Exception e) {
            return null;
        }
    }


}
