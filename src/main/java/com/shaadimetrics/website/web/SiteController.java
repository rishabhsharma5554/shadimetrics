package com.shaadimetrics.website.web;

import com.shaadimetrics.website.domain.ConsultationLead;
import com.shaadimetrics.website.domain.GalleryCategory;
import com.shaadimetrics.website.domain.GalleryImage;
import com.shaadimetrics.website.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class SiteController {

    private static final Logger log = LoggerFactory.getLogger(SiteController.class);

    private final ServiceItemRepository serviceItemRepository;
    private final TestimonialRepository testimonialRepository;
    private final OfferRepository offerRepository;
    private final GalleryImageRepository galleryImageRepository;
    private final ConsultationLeadRepository leadRepository;
    private final StatsService statsService;

    public SiteController(ServiceItemRepository serviceItemRepository,
                           TestimonialRepository testimonialRepository,
                           OfferRepository offerRepository,
                           GalleryImageRepository galleryImageRepository,
                           ConsultationLeadRepository leadRepository,
                           StatsService statsService) {
        this.serviceItemRepository = serviceItemRepository;
        this.testimonialRepository = testimonialRepository;
        this.offerRepository = offerRepository;
        this.galleryImageRepository = galleryImageRepository;
        this.leadRepository = leadRepository;
        this.statsService = statsService;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("services", serviceItemRepository.findByPublishedTrueOrderBySortOrderAsc());
        model.addAttribute("testimonials", testimonialRepository.findByStatusAndPublishedTrueOrderBySortOrderAsc(com.shaadimetrics.website.domain.ReviewStatus.APPROVED));
        model.addAttribute("offers", offerRepository.findByActiveTrueOrderByIdDesc());

        List<GalleryImage> featured = galleryImageRepository.findAllByOrderByCategoryAscSortOrderAsc().stream()
                .filter(GalleryImage::isFeatured)
                .limit(12)
                .toList();
        model.addAttribute("featuredGallery", featured);

        // Add dynamic statistics
        Map<String, Object> heroStats = statsService.getHeroStats();
        model.addAttribute("heroStats", heroStats);

        Map<String, Object> trustStats = statsService.getTrustStats();
        model.addAttribute("trustStats", trustStats);

        Map<String, Object> allStats = statsService.getWebsiteStats();
        model.addAttribute("stats", allStats);

        return "index";
    }

    @GetMapping("/gallery")
    public String gallery(Model model) {
        List<GalleryImage> all = galleryImageRepository.findAllByOrderByCategoryAscSortOrderAsc();
        Map<GalleryCategory, List<GalleryImage>> byCategory = all.stream()
                .collect(Collectors.groupingBy(GalleryImage::getCategory, java.util.LinkedHashMap::new, Collectors.toList()));
        model.addAttribute("byCategory", byCategory);
        model.addAttribute("categories", GalleryCategory.values());
        model.addAttribute("totalCount", all.size());
        return "gallery";
    }

    @GetMapping("/offers")
    public String offers(Model model) {
        model.addAttribute("offers", offerRepository.findByActiveTrueOrderByIdDesc());
        return "offers";
    }

    @PostMapping("/consultation")
    public String submitConsultation(@ModelAttribute ConsultationRequest consultation, Model model) {
        ConsultationLead lead = new ConsultationLead();
        lead.setName(consultation.name());
        lead.setPhone(consultation.phone());
        lead.setWeddingDate(consultation.weddingDate());
        lead.setMessage(consultation.message());
        lead.setPreferredCallTime(consultation.preferredCallTime());
        leadRepository.save(lead);
        log.info("New consultation lead: {}", consultation.summarize());

        model.addAttribute("name", consultation.name());
        return "thank-you";
    }

    @GetMapping("/thank-you")
    public String thankYou() {
        return "thank-you";
    }
}
