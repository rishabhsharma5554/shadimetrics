package com.shaadimetrics.website.admin;

import com.shaadimetrics.website.domain.AppUser;
import com.shaadimetrics.website.domain.Role;
import com.shaadimetrics.website.repo.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.EnumSet;
import java.util.Set;

@Controller
@RequestMapping("/admin/users")
public class AdminUserController {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminUserController(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("allRoles", Role.values());
        return "admin/users/list";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("editing", userRepository.findById(id).orElseThrow());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("allRoles", Role.values());
        return "admin/users/list";
    }

    @PostMapping
    public String create(@RequestParam String username,
                          @RequestParam String fullName,
                          @RequestParam(required = false) String email,
                          @RequestParam String password,
                          @RequestParam(required = false) Set<Role> roles,
                          RedirectAttributes redirectAttributes) {
        if (userRepository.existsByUsernameIgnoreCase(username)) {
            redirectAttributes.addFlashAttribute("error", "That username is already taken.");
            return "redirect:/admin/users";
        }
        AppUser user = new AppUser();
        user.setUsername(username);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRoles(roles == null ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles));
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "User \"" + username + "\" created.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                          @RequestParam String fullName,
                          @RequestParam(required = false) String email,
                          @RequestParam(required = false) String password,
                          @RequestParam(required = false) Set<Role> roles,
                          @RequestParam(defaultValue = "false") boolean enabled,
                          RedirectAttributes redirectAttributes) {
        AppUser user = userRepository.findById(id).orElseThrow();
        user.setFullName(fullName);
        user.setEmail(email);
        user.setEnabled(enabled);
        user.setRoles(roles == null ? EnumSet.noneOf(Role.class) : EnumSet.copyOf(roles));
        if (password != null && !password.isBlank()) {
            user.setPassword(passwordEncoder.encode(password));
        }
        userRepository.save(user);
        redirectAttributes.addFlashAttribute("success", "User updated.");
        return "redirect:/admin/users";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, Authentication authentication, RedirectAttributes redirectAttributes) {
        AppUser user = userRepository.findById(id).orElseThrow();
        if (user.getUsername().equalsIgnoreCase(authentication.getName())) {
            redirectAttributes.addFlashAttribute("error", "You can't delete your own account while logged in.");
            return "redirect:/admin/users";
        }
        userRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "User removed.");
        return "redirect:/admin/users";
    }
}
