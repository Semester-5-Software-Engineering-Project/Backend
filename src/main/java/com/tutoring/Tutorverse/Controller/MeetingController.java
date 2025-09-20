package com.tutoring.Tutorverse.Controller;

import com.tutoring.Tutorverse.Dto.MeetingRequestDto;
import com.tutoring.Tutorverse.Services.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/meeting")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @PostMapping("/join")
    public ResponseEntity<?> createMeeting(@RequestBody MeetingRequestDto meetingRequest,
                                         @RequestHeader("Authorization") String authHeader) {
        try {
            // Extract JWT token from Authorization header
            String token = authHeader.substring(7); // Remove "Bearer " prefix

            // Call the meeting service to create meeting with all functionality
            Map<String, Object> response = meetingService.createMeeting(meetingRequest, token);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.status(400).body(Map.of(
                "success", false,
                "error", "Error creating meeting: " + e.getMessage()
            ));
        }
    }
}
