package org.example.internship_system.repository;

import org.example.internship_system.entity.Company;
import org.example.internship_system.entity.InternshipOffer;
import org.example.internship_system.entity.enums.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InternshipOfferRepository extends JpaRepository<InternshipOffer, Long> {
    List<InternshipOffer> findByCompany(Company company);

    long countByStatus(OfferStatus status);
}