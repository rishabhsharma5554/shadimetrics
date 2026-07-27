package com.shaadimetrics.website.admin;

import com.shaadimetrics.website.config.FileStorageService;
import com.shaadimetrics.website.domain.ReviewStatus;
import com.shaadimetrics.website.domain.Testimonial;
import com.shaadimetrics.website.repo.TestimonialRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/testimonials")
public class AdminTestimonialController {

    private final TestimonialRepository testimonialRepository;
    private final FileStorageService fileStorageService;

    public AdminTestimonialController(TestimonialRepository testimonialRepository, FileStorageService fileStorageService) {
        this.testimonialRepository = testimonialRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("testimonials", testimonialRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("testimonial", new Testimonial());
        model.addAttribute("pendingCount", testimonialRepository.countByStatus(ReviewStatus.PENDING));
        return "admin/testimonials/list";
    }

    @PostMapping
    public String create(@ModelAttribute Testimonial testimonial,
                          @RequestParam(required = false) MultipartFile photo,
                          RedirectAttributes redirectAttributes) {
        applyPhoto(testimonial, photo);
        testimonial.setId(null);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isSuperAdmin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_SUPER_ADMIN"));
        testimonial.setSubmittedBy(auth.getName());
        testimonial.setStatus(isSuperAdmin ? ReviewStatus.APPROVED : ReviewStatus.PENDING);

        testimonialRepository.save(testimonial);
        redirectAttributes.addFlashAttribute("success", isSuperAdmin
                ? "Testimonial added and published."
                : "Testimonial submitted — it will go live once a Super Admin approves it.");
        return "redirect:/admin/testimonials";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("editing", testimonialRepository.findById(id).orElseThrow());
        model.addAttribute("testimonials", testimonialRepository.findAllByOrderBySortOrderAsc());
        model.addAttribute("testimonial", new Testimonial());
        model.addAttribute("pendingCount", testimonialRepository.countByStatus(ReviewStatus.PENDING));
        return "admin/testimonials/list";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @ModelAttribute Testimonial testimonial,
                          @RequestParam(required = false) MultipartFile photo,
                          RedirectAttributes redirectAttributes) {
        Testimonial existing = testimonialRepository.findById(id).orElseThrow();
        existing.setCoupleNames(testimonial.getCoupleNames());
        existing.setLocation(testimonial.getLocation());
        existing.setQuote(testimonial.getQuote());
        existing.setSortOrder(testimonial.getSortOrder());
        existing.setPublished(testimonial.isPublished());
        applyPhoto(existing, photo);
        testimonialRepository.save(existing);
        redirectAttributes.addFlashAttribute("success", "Testimonial updated.");
        return "redirect:/admin/testimonials";
    }

    @PostMapping("/{id}/approve")
    public String approve(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Testimonial existing = testimonialRepository.findById(id).orElseThrow();
        existing.setStatus(ReviewStatus.APPROVED);
        existing.setPublished(true);
        testimonialRepository.save(existing);
        redirectAttributes.addFlashAttribute("success", "Review approved and now live on the site.");
        return "redirect:/admin/testimonials";
    }

    @PostMapping("/{id}/reject")
    public String reject(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Testimonial existing = testimonialRepository.findById(id).orElseThrow();
        existing.setStatus(ReviewStatus.REJECTED);
        testimonialRepository.save(existing);
        redirectAttributes.addFlashAttribute("success", "Review rejected.");
        return "redirect:/admin/testimonials";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        testimonialRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Testimonial removed.");
        return "redirect:/admin/testimonials";
    }

    private void applyPhoto(Testimonial testimonial, MultipartFile photo) {
        if (photo != null && !photo.isEmpty()) {
            testimonial.setPhotoPath(fileStorageService.store(photo));
        }
    }
}
