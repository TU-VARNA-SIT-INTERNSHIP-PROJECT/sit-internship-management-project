package org.example.internship_system.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Platform overview shown on the administrator dashboard.
 * See the project brief 2.1.D: total active offers, total applications,
 * approved applications and the most frequently requested skills.
 */
@Setter
@Getter
@NoArgsConstructor
public class AdminStatisticsResponse {

    private long totalActiveOffers;
    private long totalApplications;
    private long approvedApplications;

    /** Most requested skills across active offers, highest count first. */
    private List<SkillCount> topSkills;

    /** One skill and how many active offers ask for it. */
    public record SkillCount(String skill, long count) {}
}
