package com.shaadimetrics.website.admin;

import com.shaadimetrics.website.domain.LeadStatus;
import com.shaadimetrics.website.domain.ReviewStatus;
import com.shaadimetrics.website.repo.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminDashboardController {

    private final GalleryImageRepository galleryImageRepository;
    private final TestimonialRepository testimonialRepository;
    private final ServiceItemRepository serviceItemRepository;
    private final OfferRepository offerRepository;
    private final ConsultationLeadRepository leadRepository;

    public AdminDashboardController(GalleryImageRepository galleryImageRepository,
                                     TestimonialRepository testimonialRepository,
                                     ServiceItemRepository serviceItemRepository,
                                     OfferRepository offerRepository,
                                     ConsultationLeadRepository leadRepository) {
        this.galleryImageRepository = galleryImageRepository;
        this.testimonialRepository = testimonialRepository;
        this.serviceItemRepository = serviceItemRepository;
        this.offerRepository = offerRepository;
        this.leadRepository = leadRepository;
    }

    @GetMapping("/admin")
    public String dashboard(Model model) {
        model.addAttribute("galleryCount", galleryImageRepository.count());
        model.addAttribute("testimonialCount", testimonialRepository.count());
        model.addAttribute("serviceCount", serviceItemRepository.count());
        model.addAttribute("activeOfferCount", offerRepository.findByActiveTrueOrderByIdDesc().size());
        model.addAttribute("newLeadCount", leadRepository.findAllByOrderByCreatedAtDesc().stream()
                .filter(l -> l.getStatus() == LeadStatus.NEW).count());
        model.addAttribute("pendingReviewCount", testimonialRepository.countByStatus(ReviewStatus.PENDING));
        model.addAttribute("recentLeads", leadRepository.findAllByOrderByCreatedAtDesc().stream().limit(6).toList());
        return "admin/dashboard";
    }
}
