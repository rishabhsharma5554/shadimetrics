package com.shaadimetrics.website.web;

import com.shaadimetrics.website.domain.LeadStatus;
import com.shaadimetrics.website.repo.ConsultationLeadRepository;
import com.shaadimetrics.website.repo.TestimonialRepository;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class StatsService {

    private final ConsultationLeadRepository leadRepository;
    private final TestimonialRepository testimonialRepository;

    public StatsService(ConsultationLeadRepository leadRepository,
                       TestimonialRepository testimonialRepository) {
        this.leadRepository = leadRepository;
        this.testimonialRepository = testimonialRepository;
    }

    /**
     * Get all statistics for display on the website
     */
    public Map<String, Object> getWebsiteStats() {
        Map<String, Object> stats = new HashMap<>();

        // Count of converted leads (completed weddings)
        long weddingsCompleted = leadRepository.countByStatus(LeadStatus.CONVERTED);
        stats.put("weddingsPlanned", weddingsCompleted > 0 ? weddingsCompleted : 0);

        // Calculate on-time execution percentage
        long totalLeads = leadRepository.count();
        int onTimePercentage = totalLeads > 0 ? (int) ((weddingsCompleted * 100) / totalLeads) : 0;
        stats.put("onTimeExecution", Math.min(onTimePercentage, 100)); // Cap at 100%

        // Always 100% for transparent budgeting (company promise)
        stats.put("transparentBudgeting", 100);

        // Count distinct cities served
        long citiesServed = leadRepository.countDistinctPreferredCities();
        stats.put("citiesServed", citiesServed > 0 ? citiesServed : 0);

        // Count of approved testimonials
        long testimonials = testimonialRepository.countByStatus(
                com.shaadimetrics.website.domain.ReviewStatus.APPROVED);
        stats.put("testimonials", testimonials);

        // Total leads/inquiries
        stats.put("totalLeads", totalLeads);

        return stats;
    }

    /**
     * Get statistics for hero section (top stats)
     */
    public Map<String, Object> getHeroStats() {
        Map<String, Object> stats = new HashMap<>();

        long weddingsCompleted = leadRepository.countByStatus(LeadStatus.CONVERTED);
        long totalLeads = leadRepository.count();
        int onTimePercentage = totalLeads > 0 ? (int) ((weddingsCompleted * 100) / totalLeads) : 0;

        stats.put("weddingsPlanned", weddingsCompleted > 0 ? weddingsCompleted : 0);
        stats.put("onTimeExecution", Math.min(onTimePercentage, 100));
        stats.put("transparentBudgeting", 100);

        return stats;
    }

    /**
     * Get statistics for trust section
     */
    public Map<String, Object> getTrustStats() {
        Map<String, Object> stats = new HashMap<>();

        long weddingsCompleted = leadRepository.countByStatus(LeadStatus.CONVERTED);
        long totalLeads = leadRepository.count();
        int onTimePercentage = totalLeads > 0 ? (int) ((weddingsCompleted * 100) / totalLeads) : 0;
        long citiesServed = leadRepository.countDistinctPreferredCities();

        stats.put("weddingsPlanned", weddingsCompleted > 0 ? weddingsCompleted : 0);
        stats.put("onTimeExecution", Math.min(onTimePercentage, 100));
        stats.put("citiesServed", citiesServed > 0 ? citiesServed : 0);

        return stats;
    }
}
