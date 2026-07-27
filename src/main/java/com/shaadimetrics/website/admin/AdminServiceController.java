package com.shaadimetrics.website.admin;

import com.shaadimetrics.website.config.FileStorageService;
import com.shaadimetrics.website.domain.ServiceItem;
import com.shaadimetrics.website.repo.ServiceItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/services")
public class AdminServiceController {

    private final ServiceItemRepository serviceItemRepository;
    private final FileStorageService fileStorageService;

    public AdminServiceController(ServiceItemRepository serviceItemRepository, FileStorageService fileStorageService) {
        this.serviceItemRepository = serviceItemRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("services", serviceItemRepository.findAllByOrderBySortOrderAsc());
        return "admin/services/list";
    }

    @PostMapping
    public String create(@ModelAttribute ServiceItem service,
                          @RequestParam(required = false) MultipartFile photo,
                          RedirectAttributes redirectAttributes) {
        service.setId(null);
        applyPhoto(service, photo);
        serviceItemRepository.save(service);
        redirectAttributes.addFlashAttribute("success", "Service added.");
        return "redirect:/admin/services";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("editing", serviceItemRepository.findById(id).orElseThrow());
        model.addAttribute("services", serviceItemRepository.findAllByOrderBySortOrderAsc());
        return "admin/services/list";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @ModelAttribute ServiceItem service,
                          @RequestParam(required = false) MultipartFile photo,
                          RedirectAttributes redirectAttributes) {
        ServiceItem existing = serviceItemRepository.findById(id).orElseThrow();
        existing.setTitle(service.getTitle());
        existing.setDescription(service.getDescription());
        existing.setSortOrder(service.getSortOrder());
        existing.setPublished(service.isPublished());
        applyPhoto(existing, photo);
        serviceItemRepository.save(existing);
        redirectAttributes.addFlashAttribute("success", "Service updated.");
        return "redirect:/admin/services";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        serviceItemRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Service removed.");
        return "redirect:/admin/services";
    }

    private void applyPhoto(ServiceItem service, MultipartFile photo) {
        if (photo != null && !photo.isEmpty()) {
            service.setImagePath(fileStorageService.store(photo));
        }
    }
}
