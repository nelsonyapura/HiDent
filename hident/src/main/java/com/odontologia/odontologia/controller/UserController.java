package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/update-name")
    public String updateName(@AuthenticationPrincipal User user,
                             @RequestParam("newName") String newName,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {

        if (user == null) return "redirect:/auth/login";

        try {
            userService.updateName(user, newName);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Nombre actualizado a: " + newName.trim());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/home");
    }
}
