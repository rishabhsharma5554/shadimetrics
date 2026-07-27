package com.shaadimetrics.website.web;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Rule-based wedding budget planner. Not a machine-learning model — it maps the
 * couple's answers onto proportional budget allocations we've seen work well
 * across real Shaadi Metrics weddings, so the "AI Planner" gives an instant,
 * explainable estimate rather than a black-box guess.
 */
@Service
public class PlannerService {

    public record BudgetLine(String label, long amount) {}

    public record Recommendation(long totalBudget, java.util.List<BudgetLine> lines, String headline) {}

    private static final Map<String, Long> BUDGET_MIDPOINTS = Map.of(
            "UNDER_10L", 800_000L,
            "10_20L", 1_500_000L,
            "20_40L", 3_000_000L,
            "ABOVE_40L", 5_000_000L
    );

    private static final Map<String, Integer> GUEST_MIDPOINTS = Map.of(
            "UNDER_150", 100,
            "150_300", 225,
            "300_500", 400,
            "ABOVE_500", 600
    );

    // Allocation shares mirror what we typically see across real weddings we've planned.
    private static final Map<String, Double> ALLOCATION = new LinkedHashMap<>();
    static {
        ALLOCATION.put("Venue & Decoration", 0.42);
        ALLOCATION.put("Catering", 0.28);
        ALLOCATION.put("Photography & Films", 0.13);
        ALLOCATION.put("Entertainment", 0.10);
        ALLOCATION.put("Invitations & Stationery", 0.04);
        ALLOCATION.put("Accommodation", 0.03);
    }

    public int guestMidpoint(String guestKey) {
        return GUEST_MIDPOINTS.getOrDefault(guestKey, 225);
    }

    public Recommendation recommend(String budgetKey, String guestKey, String city) {
        long total = BUDGET_MIDPOINTS.getOrDefault(budgetKey, 1_500_000L);
        int guests = GUEST_MIDPOINTS.getOrDefault(guestKey, 225);

        java.util.List<BudgetLine> lines = ALLOCATION.entrySet().stream()
                .map(e -> new BudgetLine(e.getKey(), Math.round(total * e.getValue())))
                .toList();

        String cityPart = (city != null && !city.isBlank()) ? " in " + city : "";
        String headline = "For roughly %,d guests%s, here's how we'd suggest splitting a ₹%,d estimate."
                .formatted(guests, cityPart, total);

        return new Recommendation(total, lines, headline);
    }
}
