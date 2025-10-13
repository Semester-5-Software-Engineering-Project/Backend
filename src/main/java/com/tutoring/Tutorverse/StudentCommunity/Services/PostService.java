package com.tutoring.Tutorverse.StudentCommunity.Services;

import com.tutoring.Tutorverse.StudentCommunity.Model.Post;
import com.tutoring.Tutorverse.StudentCommunity.Repository.PostRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    public Post createPost(Post post) {
        if (post.getTitle() == null || post.getDescription() == null)
            throw new IllegalArgumentException("Post must have title and description");
        return postRepository.save(post);
    }

    public Page<Post> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    public Optional<Post> getPostById(UUID id) { return postRepository.findById(id); }

    public Page<Post> getPostsByCreator(UUID creatorId, Pageable pageable) {
        return postRepository.findByCreatorId(creatorId, pageable);
    }

    public void deletePost(UUID id) { postRepository.deleteById(id); }
}
