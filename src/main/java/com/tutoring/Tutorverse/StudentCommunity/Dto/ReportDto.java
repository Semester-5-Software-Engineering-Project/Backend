package com.tutoring.Tutorverse.StudentCommunity.Dto;

import java.util.UUID;

public class ReportDto {
    private UUID id;
    private UUID postId;
    private String reason;
    private String reporterName;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getPostId() { return postId; }
    public void setPostId(UUID postId) { this.postId = postId; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public String getReporterName() { return reporterName; }
    public void setReporterName(String reporterName) { this.reporterName = reporterName; }
}
