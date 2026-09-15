package org.example.internship_system.service;

import org.example.internship_system.dtos.response.StatisticsResponse;
import org.example.internship_system.entity.InternshipOffer;
import org.example.internship_system.entity.enums.ApplicationStatus;
import org.example.internship_system.entity.enums.OfferStatus;
import org.example.internship_system.repository.ApplicationRepository;
import org.example.internship_system.repository.InternshipOfferRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    private final InternshipOfferRepository internshipOfferRepository;
    private final ApplicationRepository applicationRepository;

    public StatisticsService(InternshipOfferRepository internshipOfferRepository,
                             ApplicationRepository applicationRepository) {
        this.internshipOfferRepository = internshipOfferRepository;
        this.applicationRepository = applicationRepository;
    }

    public StatisticsResponse getStatistics() {
        StatisticsResponse response = new StatisticsResponse();
        response.setTotalActiveOffers(internshipOfferRepository.countByStatus(OfferStatus.ACTIVE));
        response.setTotalApplications(applicationRepository.count());
        response.setApprovedApplications(applicationRepository.countByStatus(ApplicationStatus.APPROVED));
        response.setTopSkills(getTopSkills(5));
        return response;
    }

    // requiredSkills is stored as text like "Java, Spring Boot, SQL",
    // so split it, count each skill and keep the most frequent ones
    private Map<String, Long> getTopSkills(int limit) {
        Map<String, Long> counts = new HashMap<>();
        for (InternshipOffer offer : internshipOfferRepository.findAll()) {
            if (offer.getRequiredSkills() == null) {
                continue;
            }
            for (String skill : offer.getRequiredSkills().split(",")) {
                String name = skill.trim();
                if (!name.isEmpty()) {
                    counts.put(name, counts.getOrDefault(name, 0L) + 1);
                }
            }
        }

        List<Map.Entry<String, Long>> entries = new ArrayList<>(counts.entrySet());
        entries.sort((a, b) -> Long.compare(b.getValue(), a.getValue()));

        Map<String, Long> topSkills = new LinkedHashMap<>();
        for (int i = 0; i < Math.min(limit, entries.size()); i++) {
            topSkills.put(entries.get(i).getKey(), entries.get(i).getValue());
        }
        return topSkills;
    }
}
