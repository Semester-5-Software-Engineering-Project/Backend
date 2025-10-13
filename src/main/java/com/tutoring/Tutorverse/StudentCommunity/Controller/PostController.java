package com.tutoring.Tutorverse.StudentCommunity.Controller;

import com.tutoring.Tutorverse.StudentCommunity.Dto.PostDto;
import com.tutoring.Tutorverse.StudentCommunity.Model.Post;
import com.tutoring.Tutorverse.StudentCommunity.Services.PostService;
import com.tutoring.Tutorverse.Services.UserService;
import com.tutoring.Tutorverse.Model.User;
import com.tutoring.Tutorverse.Model.StudentEntity;
import com.tutoring.Tutorverse.StudentCommunity.Services.CommunityStudentProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/community/posts")
public class PostController {

    @Autowired private PostService postService;
    @Autowired private UserService userService;
    @Autowired private CommunityStudentProfileService studentProfileService;

    @PostMapping
    public ResponseEntity<?> createPost(@RequestBody PostDto postDto, HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        User user = userService.getUserFromRequest(req);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        if (postDto.getTitle() == null || postDto.getDescription() == null)
            return ResponseEntity.badRequest().body("Title and description are required");

        Post post = new Post();
        post.setTitle(postDto.getTitle());
        post.setDescription(postDto.getDescription());
        post.setImageUrls(postDto.getImageUrls() != null ? postDto.getImageUrls() : new ArrayList<>());
        post.setCreator(user);

        return ResponseEntity.ok(toDto(postService.createPost(post)));
    }

    @GetMapping
    public ResponseEntity<?> getAllPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.getAllPosts(pageable);
        List<PostDto> posts = postPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("currentPage", postPage.getNumber());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/mine")
    public ResponseEntity<?> getMyPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        User user = userService.getUserFromRequest(req);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        Pageable pageable = PageRequest.of(page, size);
        Page<Post> postPage = postService.getPostsByCreator(user.getId(), pageable);
        List<PostDto> posts = postPage.getContent().stream().map(this::toDto).collect(Collectors.toList());
        Map<String, Object> response = new HashMap<>();
        response.put("posts", posts);
        response.put("currentPage", postPage.getNumber());
        response.put("totalItems", postPage.getTotalElements());
        response.put("totalPages", postPage.getTotalPages());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<?> deletePost(@PathVariable UUID postId, HttpServletRequest req) {
        if (!userService.hasRole(req, "STUDENT"))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Access denied: students only");

        User user = userService.getUserFromRequest(req);
        if (user == null) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");

        Optional<Post> opt = postService.getPostById(postId);
        if (opt.isEmpty()) return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Post not found");

        Post post = opt.get();
        if (!post.getCreator().getId().equals(user.getId()))
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You can delete only your own posts");

        postService.deletePost(postId);
        return ResponseEntity.ok(Map.of("message", "Post deleted successfully"));
    }

    private PostDto toDto(Post post) {
        PostDto dto = new PostDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setDescription(post.getDescription());
        dto.setImageUrls(post.getImageUrls());
        User creator = post.getCreator();
        if (creator != null) {
            dto.setCreatorName(creator.getName());
            StudentEntity student = studentProfileService.getStudentProfileByUser(creator).orElse(null);
            dto.setCreatorProfileImage(student != null ? student.getImageUrl() : null);
        }
        dto.setCommentCount(post.getComments() != null ? post.getComments().size() : 0);
        dto.setReportCount(post.getReports() != null ? post.getReports().size() : 0);
        dto.setEngagement(dto.getCommentCount() + dto.getReportCount());
        return dto;
    }
}
