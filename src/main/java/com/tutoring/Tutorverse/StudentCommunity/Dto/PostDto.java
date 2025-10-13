package com.tutoring.Tutorverse.StudentCommunity.Dto;

import java.util.List;
import java.util.UUID;

public class PostDto {
    private UUID id;
    private String title;
    private String description;
    private List<String> imageUrls;
    private String creatorName;
    private String creatorProfileImage;
    private int commentCount;
    private int reportCount;
    private int engagement;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    public String getCreatorName() { return creatorName; }
    public void setCreatorName(String creatorName) { this.creatorName = creatorName; }
    public String getCreatorProfileImage() { return creatorProfileImage; }
    public void setCreatorProfileImage(String creatorProfileImage) { this.creatorProfileImage = creatorProfileImage; }
    public int getCommentCount() { return commentCount; }
    public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    public int getReportCount() { return reportCount; }
    public void setReportCount(int reportCount) { this.reportCount = reportCount; }
    public int getEngagement() { return engagement; }
    public void setEngagement(int engagement) { this.engagement = engagement; }
}
