package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/proformas")
@RequiredArgsConstructor
public class ProformaController {

    @GetMapping
    public String list(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("activePage", "proformas");

        return "proforma/list";
    }

    @GetMapping("/create")
    public String create(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/auth/login";

        String doctorName = user.getName() != null ? user.getName() : user.getUsername();
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("doctorName", doctorName);
        model.addAttribute("activePage", "proformas");

        return "proforma/create";
    }

    @GetMapping("/{id}")
    public String view(@AuthenticationPrincipal User user,
                       @PathVariable Long id, Model model) {
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("proformaId", id);
        model.addAttribute("activePage", "proformas");

        return "proforma/view";
    }
}
