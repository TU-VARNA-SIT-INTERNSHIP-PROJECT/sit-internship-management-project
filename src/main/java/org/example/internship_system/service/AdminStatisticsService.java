package org.example.internship_system.service;

import org.example.internship_system.dtos.response.AdminStatisticsResponse;
import org.example.internship_system.entity.enums.ApplicationStatus;
import org.example.internship_system.entity.enums.OfferStatus;
import org.example.internship_system.repository.ApplicationRepository;
import org.example.internship_system.repository.InternshipOfferRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminStatisticsService {

    /** How many skills the dashboard shows. */
    private static final int TOP_SKILLS_LIMIT = 5;

    private final InternshipOfferRepository internshipOfferRepository;
    private final ApplicationRepository applicationRepository;

    public AdminStatisticsService(InternshipOfferRepository internshipOfferRepository,
                                  ApplicationRepository applicationRepository) {
        this.internshipOfferRepository = internshipOfferRepository;
        this.applicationRepository = applicationRepository;
    }

    public AdminStatisticsResponse getStatistics() {
        AdminStatisticsResponse response = new AdminStatisticsResponse();

        response.setTotalActiveOffers(internshipOfferRepository.countByStatus(OfferStatus.ACTIVE));
        response.setTotalApplications(applicationRepository.count());
        response.setApprovedApplications(
                applicationRepository.countByStatus(ApplicationStatus.APPROVED));
        response.setTopSkills(topSkillsOfActiveOffers());

        return response;
    }

    /**
     * requiredSkills is one free-text column, filled in by companies as a
     * comma separated list ("Java, Spring Boot, SQL"). There is no Skill table
     * to group by, so the offers' skill text is split and counted here.
     *
     * Only ACTIVE offers are counted, so the result answers "what are companies
     * hiring for right now" and stays consistent with totalActiveOffers.
     */
    private List<AdminStatisticsResponse.SkillCount> topSkillsOfActiveOffers() {
        // Skills are grouped case-insensitively, but the first spelling a
        // company used is what gets displayed back.
        Map<String, String> displayNames = new LinkedHashMap<>();
        Map<String, Long> counts = new LinkedHashMap<>();

        for (String skillsText : internshipOfferRepository.findRequiredSkillsByStatus(OfferStatus.ACTIVE)) {
            if (skillsText == null || skillsText.isBlank()) {
                continue;
            }
            for (String rawSkill : skillsText.split(",")) {
                String skill = rawSkill.trim();
                if (skill.isEmpty()) {
                    continue;
                }
                String key = skill.toLowerCase();
                displayNames.putIfAbsent(key, skill);
                counts.merge(key, 1L, Long::sum);
            }
        }

        List<AdminStatisticsResponse.SkillCount> topSkills = new ArrayList<>();
        for (Map.Entry<String, Long> entry : counts.entrySet()) {
            topSkills.add(new AdminStatisticsResponse.SkillCount(
                    displayNames.get(entry.getKey()), entry.getValue()));
        }

        // Highest count first; ties fall back to alphabetical so the order is stable.
        topSkills.sort(Comparator
                .comparingLong(AdminStatisticsResponse.SkillCount::count).reversed()
                .thenComparing(AdminStatisticsResponse.SkillCount::skill, String.CASE_INSENSITIVE_ORDER));

        if (topSkills.size() > TOP_SKILLS_LIMIT) {
            return new ArrayList<>(topSkills.subList(0, TOP_SKILLS_LIMIT));
        }
        return topSkills;
    }
}
