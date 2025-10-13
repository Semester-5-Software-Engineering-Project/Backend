package com.tutoring.Tutorverse.StudentCommunity.Services;

import com.tutoring.Tutorverse.StudentCommunity.Model.Report;
import com.tutoring.Tutorverse.StudentCommunity.Repository.ReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportService {

    @Autowired
    private ReportRepository reportRepository;

    public Report createReport(Report report) { return reportRepository.save(report); }

    public List<Report> getReportsByPost(UUID postId) { return reportRepository.findByPostId(postId); }

    public List<Report> getReportsByReporter(UUID reporterId) { return reportRepository.findByReporterId(reporterId); }
}
