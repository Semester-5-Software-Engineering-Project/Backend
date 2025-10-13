package com.tutoring.Tutorverse.StudentCommunity.Services;

import com.tutoring.Tutorverse.StudentCommunity.Model.Comment;
import com.tutoring.Tutorverse.StudentCommunity.Repository.CommentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    public Comment createComment(Comment comment) { return commentRepository.save(comment); }


    public List<Comment> getCommentsByPost(UUID postId) {
        return commentRepository.findByPostId(postId);
    }

    public Page<Comment> getCommentsByPost(UUID postId, Pageable pageable) {
        return commentRepository.findByPostId(postId, pageable);
    }

    public Optional<Comment> getCommentById(UUID id) { return commentRepository.findById(id); }

    public Comment updateComment(Comment comment) { return commentRepository.save(comment); }

    public void deleteComment(UUID id) { commentRepository.deleteById(id); }
}
