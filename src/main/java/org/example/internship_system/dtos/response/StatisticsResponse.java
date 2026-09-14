package org.example.internship_system.dtos.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
@NoArgsConstructor
public class StatisticsResponse {

    private long totalActiveOffers;
    private long totalApplications;
    private long approvedApplications;
    private Map<String, Long> topSkills;
}
