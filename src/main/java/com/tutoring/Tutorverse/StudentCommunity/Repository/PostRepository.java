package com.tutoring.Tutorverse.StudentCommunity.Repository;


import com.tutoring.Tutorverse.StudentCommunity.Model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    List<Post> findByCreatorId(UUID creatorId);
    Page<Post> findByCreatorId(UUID creatorId, Pageable pageable);
    Page<Post> findAll(Pageable pageable);
}
