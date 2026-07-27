package com.shaadimetrics.website.admin;

import com.shaadimetrics.website.config.FileStorageService;
import com.shaadimetrics.website.domain.Offer;
import com.shaadimetrics.website.repo.OfferRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/offers")
public class AdminOfferController {

    private final OfferRepository offerRepository;
    private final FileStorageService fileStorageService;

    public AdminOfferController(OfferRepository offerRepository, FileStorageService fileStorageService) {
        this.offerRepository = offerRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("offers", offerRepository.findAllByOrderByIdDesc());
        return "admin/offers/list";
    }

    @PostMapping
    public String create(@RequestParam String title,
                          @RequestParam String description,
                          @RequestParam String couponCode,
                          @RequestParam String discountText,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validFrom,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validTo,
                          @RequestParam(defaultValue = "true") boolean active,
                          @RequestParam(required = false) MultipartFile photo,
                          RedirectAttributes redirectAttributes) {
        Offer offer = new Offer();
        offer.setTitle(title);
        offer.setDescription(description);
        offer.setCouponCode(couponCode.toUpperCase());
        offer.setDiscountText(discountText);
        offer.setValidFrom(validFrom);
        offer.setValidTo(validTo);
        offer.setActive(active);
        applyPhoto(offer, photo);
        offerRepository.save(offer);
        redirectAttributes.addFlashAttribute("success", "Offer \"" + title + "\" created.");
        return "redirect:/admin/offers";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("editing", offerRepository.findById(id).orElseThrow());
        model.addAttribute("offers", offerRepository.findAllByOrderByIdDesc());
        return "admin/offers/list";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @RequestParam String title,
                          @RequestParam String description,
                          @RequestParam String couponCode,
                          @RequestParam String discountText,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validFrom,
                          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate validTo,
                          @RequestParam(defaultValue = "true") boolean active,
                          @RequestParam(required = false) MultipartFile photo,
                          RedirectAttributes redirectAttributes) {
        Offer offer = offerRepository.findById(id).orElseThrow();
        offer.setTitle(title);
        offer.setDescription(description);
        offer.setCouponCode(couponCode.toUpperCase());
        offer.setDiscountText(discountText);
        offer.setValidFrom(validFrom);
        offer.setValidTo(validTo);
        offer.setActive(active);
        applyPhoto(offer, photo);
        offerRepository.save(offer);
        redirectAttributes.addFlashAttribute("success", "Offer updated.");
        return "redirect:/admin/offers";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        offerRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Offer removed.");
        return "redirect:/admin/offers";
    }

    private void applyPhoto(Offer offer, MultipartFile photo) {
        if (photo != null && !photo.isEmpty()) {
            offer.setImagePath(fileStorageService.store(photo));
        }
    }
}
