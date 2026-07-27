package com.shaadimetrics.website.admin;

import com.shaadimetrics.website.config.FileStorageService;
import com.shaadimetrics.website.domain.GalleryCategory;
import com.shaadimetrics.website.domain.GalleryImage;
import com.shaadimetrics.website.repo.GalleryImageRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/gallery")
public class AdminGalleryController {

    private final GalleryImageRepository galleryImageRepository;
    private final FileStorageService fileStorageService;

    public AdminGalleryController(GalleryImageRepository galleryImageRepository, FileStorageService fileStorageService) {
        this.galleryImageRepository = galleryImageRepository;
        this.fileStorageService = fileStorageService;
    }

    @GetMapping
    public String list(@RequestParam(required = false) String category, Model model) {
        List<GalleryImage> images = (category == null || category.isBlank())
                ? galleryImageRepository.findAllByOrderByCategoryAscSortOrderAsc()
                : galleryImageRepository.findByCategoryOrderBySortOrderAsc(GalleryCategory.valueOf(category.toUpperCase()));
        model.addAttribute("images", images);
        model.addAttribute("categories", GalleryCategory.values());
        model.addAttribute("selectedCategory", category);
        return "admin/gallery/list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("categories", GalleryCategory.values());
        return "admin/gallery/form";
    }

    @PostMapping
    public String create(@RequestParam GalleryCategory category,
                          @RequestParam(required = false) String caption,
                          @RequestParam(defaultValue = "0") int sortOrder,
                          @RequestParam(defaultValue = "false") boolean featured,
                          @RequestParam MultipartFile file,
                          RedirectAttributes redirectAttributes) {
        String path = fileStorageService.store(file);
        if (path == null) {
            redirectAttributes.addFlashAttribute("error", "Please choose a photo to upload.");
            return "redirect:/admin/gallery/new";
        }
        GalleryImage image = new GalleryImage();
        image.setCategory(category);
        image.setCaption(caption);
        image.setSortOrder(sortOrder);
        image.setFeatured(featured);
        image.setImagePath(path);
        galleryImageRepository.save(image);
        redirectAttributes.addFlashAttribute("success", "Photo added to the " + category.getLabel() + " gallery.");
        return "redirect:/admin/gallery";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        GalleryImage image = galleryImageRepository.findById(id).orElseThrow();
        model.addAttribute("image", image);
        model.addAttribute("categories", GalleryCategory.values());
        return "admin/gallery/edit";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @RequestParam GalleryCategory category,
                          @RequestParam(required = false) String caption,
                          @RequestParam(defaultValue = "0") int sortOrder,
                          @RequestParam(defaultValue = "false") boolean featured,
                          RedirectAttributes redirectAttributes) {
        GalleryImage image = galleryImageRepository.findById(id).orElseThrow();
        image.setCategory(category);
        image.setCaption(caption);
        image.setSortOrder(sortOrder);
        image.setFeatured(featured);
        galleryImageRepository.save(image);
        redirectAttributes.addFlashAttribute("success", "Photo details updated.");
        return "redirect:/admin/gallery";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        galleryImageRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Photo removed from the gallery.");
        return "redirect:/admin/gallery";
    }
}
