package org.example.internship_system.service;

import org.example.internship_system.dtos.request.ApplicationRequest;
import org.example.internship_system.dtos.request.ApplicationStatusRequest;
import org.example.internship_system.dtos.response.ApplicationResponse;
import org.example.internship_system.entity.Application;
import org.example.internship_system.entity.Company;
import org.example.internship_system.entity.InternshipOffer;
import org.example.internship_system.entity.StudentProfile;
import org.example.internship_system.entity.enums.ApplicationStatus;
import org.example.internship_system.entity.enums.OfferStatus;
import org.example.internship_system.exception.ResourceNotFoundException;
import org.example.internship_system.mapper.ApplicationMapper;
import org.example.internship_system.repository.ApplicationRepository;
import org.example.internship_system.repository.CompanyRepository;
import org.example.internship_system.repository.InternshipOfferRepository;
import org.example.internship_system.repository.StudentProfileRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final StudentProfileRepository studentProfileRepository;
    private final InternshipOfferRepository internshipOfferRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationMapper applicationMapper;

    public ApplicationService(ApplicationRepository applicationRepository,
                              StudentProfileRepository studentProfileRepository,
                              InternshipOfferRepository internshipOfferRepository,
                              CompanyRepository companyRepository,
                              ApplicationMapper applicationMapper) {
        this.applicationRepository = applicationRepository;
        this.studentProfileRepository = studentProfileRepository;
        this.internshipOfferRepository = internshipOfferRepository;
        this.companyRepository = companyRepository;
        this.applicationMapper = applicationMapper;
    }


    private StudentProfile currentStudent() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return studentProfileRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "No student profile for user: " + email));
    }


    private Company currentCompany() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return companyRepository.findByUserEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("No company for user: " + email));
    }

    private void checkOfferOwnership(InternshipOffer offer) {
        Company company = currentCompany();
        if (!offer.getCompany().getId().equals(company.getId())) {
            throw new AccessDeniedException("You can only manage applications for your own offers");
        }
    }

    public ApplicationResponse create(ApplicationRequest request) {
        StudentProfile student = currentStudent();

        InternshipOffer offer = internshipOfferRepository.findById(request.getInternshipOfferId())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Internship offer not found: " + request.getInternshipOfferId()));

        if (offer.getStatus() != OfferStatus.ACTIVE) {
            throw new IllegalStateException("Cannot apply to a closed offer: " + offer.getId());
        }

        if (applicationRepository.existsByStudentAndInternshipOffer(student, offer)) {
            throw new IllegalStateException(
                    "Student " + student.getId() + " has already applied to offer " + offer.getId());
        }

        Application application = new Application();
        application.setStudent(student);
        application.setInternshipOffer(offer);
        application.setMotivationLetter(request.getMotivationLetter());
        application.setApplicationDate(LocalDateTime.now());
        application.setStatus(ApplicationStatus.PENDING);

        Application saved = applicationRepository.save(application);
        return toResponse(saved);
    }

    public List<ApplicationResponse> getAll() {
        List<ApplicationResponse> result = new ArrayList<>();
        for (Application application : applicationRepository.findAll()) {
            result.add(toResponse(application));
        }
        return result;
    }

    public ApplicationResponse getById(Long id) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
        return toResponse(application);
    }

    public List<ApplicationResponse> getMyApplications() {
        StudentProfile student = currentStudent();
        List<ApplicationResponse> result = new ArrayList<>();
        for (Application application : applicationRepository.findByStudent(student)) {
            result.add(toResponse(application));
        }
        return result;
    }

    public List<ApplicationResponse> getByOffer(Long offerId) {
        InternshipOffer offer = internshipOfferRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Internship offer not found: " + offerId));
        checkOfferOwnership(offer);
        List<ApplicationResponse> result = new ArrayList<>();
        for (Application application : applicationRepository.findByInternshipOffer(offer)) {
            result.add(toResponse(application));
        }
        return result;
    }

    public ApplicationResponse updateStatus(Long id, ApplicationStatusRequest request) {
        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
        checkOfferOwnership(application.getInternshipOffer());

        application.setStatus(request.getStatus());
        if (request.getComment() != null) {
            application.setComment(request.getComment());
        }

        Application saved = applicationRepository.save(application);
        return toResponse(saved);
    }

    private ApplicationResponse toResponse(Application application) {
        return applicationMapper.toDto(application);
    }
}
