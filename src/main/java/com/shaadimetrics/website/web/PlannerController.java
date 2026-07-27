package com.shaadimetrics.website.web;

import com.shaadimetrics.website.domain.ConsultationLead;
import com.shaadimetrics.website.domain.LeadSource;
import com.shaadimetrics.website.repo.ConsultationLeadRepository;
import com.shaadimetrics.website.repo.OfferRepository;
import com.shaadimetrics.website.repo.ServiceItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class PlannerController {

    private final PlannerService plannerService;
    private final ServiceItemRepository serviceItemRepository;
    private final OfferRepository offerRepository;
    private final ConsultationLeadRepository leadRepository;

    public PlannerController(PlannerService plannerService,
                              ServiceItemRepository serviceItemRepository,
                              OfferRepository offerRepository,
                              ConsultationLeadRepository leadRepository) {
        this.plannerService = plannerService;
        this.serviceItemRepository = serviceItemRepository;
        this.offerRepository = offerRepository;
        this.leadRepository = leadRepository;
    }

    @GetMapping("/planner")
    public String plannerForm() {
        return "planner";
    }

    @PostMapping("/planner")
    public String plannerSubmit(@RequestParam String guestCount,
                                 @RequestParam String budgetRange,
                                 @RequestParam(required = false) String city,
                                 @RequestParam(required = false) String weddingDate,
                                 @RequestParam String name,
                                 @RequestParam String phone,
                                 @RequestParam(required = false) String preferredCallTime,
                                 Model model) {
        PlannerService.Recommendation recommendation = plannerService.recommend(budgetRange, guestCount, city);

        ConsultationLead lead = new ConsultationLead();
        lead.setName(name);
        lead.setPhone(phone);
        lead.setWeddingDate(weddingDate);
        lead.setPreferredCity(city);
        lead.setBudgetRange(budgetRange);
        lead.setPreferredCallTime(preferredCallTime);
        lead.setSource(LeadSource.AI_PLANNER);
        lead.setGuestCount(plannerService.guestMidpoint(guestCount));
        leadRepository.save(lead);

        model.addAttribute("recommendation", recommendation);
        model.addAttribute("services", serviceItemRepository.findByPublishedTrueOrderBySortOrderAsc());
        model.addAttribute("offers", offerRepository.findByActiveTrueOrderByIdDesc());
        model.addAttribute("name", name);
        return "planner-result";
    }
}
