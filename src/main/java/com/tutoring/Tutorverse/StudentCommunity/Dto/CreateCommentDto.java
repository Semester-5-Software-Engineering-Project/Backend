package com.tutoring.Tutorverse.StudentCommunity.Dto;

import java.util.UUID;

public class CreateCommentDto {
    private UUID postId;
    private String content;

    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
}
