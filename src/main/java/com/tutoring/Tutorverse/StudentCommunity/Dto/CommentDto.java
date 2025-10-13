package com.tutoring.Tutorverse.StudentCommunity.Dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class CommentDto {
    private UUID id;
    private UUID postId;
    private String content;
    private String userName;
    private String userProfileImage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public String getUserProfileImage() { return userProfileImage; }
    public void setUserProfileImage(String userProfileImage) { this.userProfileImage = userProfileImage; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
