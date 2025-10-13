package com.tutoring.Tutorverse.StudentCommunity.Controller;

import com.tutoring.Tutorverse.StudentCommunity.Dto.CommentDto;
import com.tutoring.Tutorverse.StudentCommunity.Dto.CreateCommentDto;
import com.tutoring.Tutorverse.StudentCommunity.Model.Comment;
import com.tutoring.Tutorverse.StudentCommunity.Model.Post;
import com.tutoring.Tutorverse.StudentCommunity.Services.CommentService;
import com.tutoring.Tutorverse.StudentCommunity.Services.PostService;
import com.tutoring.Tutorverse.StudentCommunity.Services.CommunityStudentProfileService;
import com.tutoring.Tutorverse.Services.UserService;
import com.tutoring.Tutorverse.Model.User;
import com.tutoring.Tutorverse.Model.StudentEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/community/comments")
public class CommentController {

    @Autowired private CommentService commentService;
    @Autowired private PostService postService;
    @Autowired private UserService userService;
    @Autowired private CommunityStudentProfileService studentProfileService;

    // 🔹 Create a new comment
    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody CreateCommentDto dto, HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        User user = userService.getUserFromRequest(req);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        Optional<Post> postOpt = postService.getPostById(dto.getPostId());
        if (postOpt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");

        if (dto.getContent() == null || dto.getContent().trim().isEmpty())
            return ResponseEntity.badRequest().body("Comment content cannot be empty");

        Comment comment = new Comment();
        comment.setPost(postOpt.get());
        comment.setUser(user);
        comment.setContent(dto.getContent().trim());
        comment.setCreatedAt(LocalDateTime.now());
        comment.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(toDto(commentService.createComment(comment)));
    }

    // 🔹 Get all comments for a specific post
    @GetMapping("/post/{postId}")
    public ResponseEntity<?> getCommentsByPost(
            @PathVariable UUID postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, org.springframework.data.domain.Sort.by("createdAt").descending());
        org.springframework.data.domain.Page<Comment> commentPage = commentService.getCommentsByPost(postId, pageable);
        List<CommentDto> pagedComments = commentPage.getContent().stream().map(this::toDto).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("comments", pagedComments);
        response.put("page", commentPage.getNumber());
        response.put("size", commentPage.getSize());
        response.put("totalElements", commentPage.getTotalElements());
        response.put("totalPages", commentPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // 🔹 Update (only owner)
    @PutMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable UUID commentId, @RequestBody CreateCommentDto dto, HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        User user = userService.getUserFromRequest(req);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        Optional<Comment> opt = commentService.getCommentById(commentId);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");

        Comment comment = opt.get();
        if (!comment.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can edit only your own comments");

        comment.setContent(dto.getContent());
        comment.setUpdatedAt(LocalDateTime.now());
        return ResponseEntity.ok(toDto(commentService.updateComment(comment)));
    }

    // 🔹 Delete (only owner)
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId, HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        User user = userService.getUserFromRequest(req);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        Optional<Comment> opt = commentService.getCommentById(commentId);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Comment not found");

        Comment comment = opt.get();
        if (!comment.getUser().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can delete only your own comments");

        commentService.deleteComment(commentId);
        return ResponseEntity.ok(Map.of("message", "Comment deleted successfully"));
    }

    // 🔹 Convert entity → DTO
    private CommentDto toDto(Comment comment) {
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setPostId(comment.getPost().getId());
        dto.setContent(comment.getContent());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        User user = comment.getUser();
        dto.setUserName(user != null ? user.getName() : "Unknown");

        if (user != null) {
            StudentEntity student = studentProfileService.getStudentProfileByUser(user).orElse(null);
            dto.setUserProfileImage(student != null ? student.getImageUrl() : null);
        }
        return dto;
    }
}
