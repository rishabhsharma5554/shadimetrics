package com.shaadimetrics.website.admin;

import com.shaadimetrics.website.domain.LeadStatus;
import com.shaadimetrics.website.repo.ConsultationLeadRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/leads")
public class AdminLeadController {

    private final ConsultationLeadRepository leadRepository;

    public AdminLeadController(ConsultationLeadRepository leadRepository) {
        this.leadRepository = leadRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("leads", leadRepository.findAllByOrderByCreatedAtDesc());
        model.addAttribute("statuses", LeadStatus.values());
        return "admin/leads/list";
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id, @RequestParam LeadStatus status, RedirectAttributes redirectAttributes) {
        var lead = leadRepository.findById(id).orElseThrow();
        lead.setStatus(status);
        leadRepository.save(lead);
        redirectAttributes.addFlashAttribute("success", "Lead status updated.");
        return "redirect:/admin/leads";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        leadRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Lead deleted.");
        return "redirect:/admin/leads";
    }
}
