package com.tutoring.Tutorverse.StudentCommunity.Services;

import com.tutoring.Tutorverse.Model.StudentEntity;
import com.tutoring.Tutorverse.Model.User;
import com.tutoring.Tutorverse.Repository.StudentProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommunityStudentProfileService {
    @Autowired
    private StudentProfileRepository studentProfileRepository;

    public Optional<StudentEntity> getStudentProfileByUser(User user) {
        return studentProfileRepository.findByUser(user);
    }
}
