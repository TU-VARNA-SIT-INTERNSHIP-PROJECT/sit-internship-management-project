package org.example.internship_system.repository;

import org.example.internship_system.entity.Application;
import org.example.internship_system.entity.InternshipOffer;
import org.example.internship_system.entity.StudentProfile;
import org.example.internship_system.entity.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {

    List<Application> findByStudent(StudentProfile student);

    List<Application> findByInternshipOffer(InternshipOffer internshipOffer);

    boolean existsByStudentAndInternshipOffer(StudentProfile student, InternshipOffer internshipOffer);

    long countByStatus(ApplicationStatus status);
}
