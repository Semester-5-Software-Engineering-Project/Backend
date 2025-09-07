package com.tutoring.Tutorverse.Services;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.tutoring.Tutorverse.Dto.EnrollDto;
import com.tutoring.Tutorverse.Model.StudentEntity;
import com.tutoring.Tutorverse.Model.EnrollmentEntity;
import com.tutoring.Tutorverse.Model.ModuelsEntity;
import com.tutoring.Tutorverse.Repository.EnrollRepository;
import com.tutoring.Tutorverse.Repository.ModulesRepository;
import com.tutoring.Tutorverse.Repository.StudentProfileRepository;

@Service
public class EnrollmentService {
    @Autowired
    private EnrollRepository enrollRepository;
    @Autowired
    private StudentProfileRepository studentProfileRepository;
    @Autowired
    private ModulesRepository moduleRepository; 

    public String EnrollTOModule(EnrollDto enrollDto) {
        StudentEntity student = findStudentById(enrollDto.getStudentId());
        ModuelsEntity module = findModuleById(enrollDto.getModuleId());
        EnrollmentEntity enrollment = new EnrollmentEntity();
        enrollment.setStudent(student);
        enrollment.setModule(module);
        enrollRepository.save(enrollment);
        return "Enrolled Successfully " + enrollment.getEnrolmentId();
    }

    private ModuelsEntity findModuleById(UUID moduleId) {
       return moduleRepository.findById(moduleId).orElseThrow(() -> new RuntimeException("Module not found"));
    }
 
    private StudentEntity findStudentById(UUID studentId) {
        return studentProfileRepository.findById(studentId).orElseThrow(() -> new RuntimeException("Student not found"));
        
    }

    public void unenrollFromModule(UUID enrollmentId) {
        enrollRepository.deleteById(enrollmentId);
    }


}
