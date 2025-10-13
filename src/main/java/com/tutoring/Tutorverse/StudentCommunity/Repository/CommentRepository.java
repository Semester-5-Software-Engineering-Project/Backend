package com.tutoring.Tutorverse.StudentCommunity.Repository;

import com.tutoring.Tutorverse.StudentCommunity.Model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByPostId(UUID postId);
    List<Comment> findByUserId(UUID userId);

    Page<Comment> findByPostId(UUID postId, Pageable pageable);
}
