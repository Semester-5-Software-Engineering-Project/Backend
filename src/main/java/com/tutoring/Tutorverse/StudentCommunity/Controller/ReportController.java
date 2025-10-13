package com.tutoring.Tutorverse.StudentCommunity.Controller;

import com.tutoring.Tutorverse.StudentCommunity.Dto.ReportDto;
import com.tutoring.Tutorverse.StudentCommunity.Model.Post;
import com.tutoring.Tutorverse.StudentCommunity.Model.Report;
import com.tutoring.Tutorverse.StudentCommunity.Services.PostService;
import com.tutoring.Tutorverse.StudentCommunity.Services.ReportService;
import com.tutoring.Tutorverse.Services.UserService;
import com.tutoring.Tutorverse.Model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community/reports")
public class ReportController {

    @Autowired private ReportService reportService;
    @Autowired private PostService postService;
    @Autowired private UserService userService;

    // 🔹 Report a post
    @PostMapping
    public ResponseEntity<?> createReport(@RequestBody Map<String, String> body, HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        User reporter = userService.getUserFromRequest(req);
        if (reporter == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        String postIdStr = body.get("postId");
        String reason = body.get("reason");

        if (postIdStr == null || postIdStr.isEmpty())
            return ResponseEntity.badRequest().body("postId is required");
        if (reason == null || reason.trim().isEmpty())
            return ResponseEntity.badRequest().body("reason is required");

        UUID postId;
        try {
            postId = UUID.fromString(postIdStr);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid postId format");
        }

        Optional<Post> postOpt = postService.getPostById(postId);
        if (postOpt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");

        Report report = new Report();
        report.setPost(postOpt.get());
        report.setReporter(reporter);
        report.setReason(reason.trim());
        report.setCreatedAt(LocalDateTime.now());

        return ResponseEntity.ok(toDto(reportService.createReport(report)));
    }

    // 🔹 Get reports for a specific post (for admin/moderation views)
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getReportsByPost(@PathVariable UUID postId, HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        List<ReportDto> reports = reportService.getReportsByPost(postId)
                .stream().map(this::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(reports);
    }

    // 🔹 Convert entity to DTO
    private ReportDto toDto(Report report) {
        ReportDto dto = new ReportDto();
        dto.setId(report.getId());
        dto.setPostId(report.getPost().getId());
        dto.setReason(report.getReason());
        dto.setReporterName(report.getReporter() != null ? report.getReporter().getName() : "Unknown");
        return dto;
    }
}
