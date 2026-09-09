package org.example.internship_system.repository;

import org.example.internship_system.entity.Company;
import org.example.internship_system.entity.InternshipOffer;
import org.example.internship_system.entity.enums.OfferStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InternshipOfferRepository extends JpaRepository<InternshipOffer, Long> {
    List<InternshipOffer> findByCompany(Company company);

    long countByStatus(OfferStatus status);

    /**
     * Only the requiredSkills text of the matching offers, so the statistics
     * query does not have to load whole offer entities just to read one column.
     */
    @Query("SELECT o.requiredSkills FROM InternshipOffer o WHERE o.status = :status")
    List<String> findRequiredSkillsByStatus(@Param("status") OfferStatus status);
}