package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/agenda")
@RequiredArgsConstructor
public class AgendaController {

    @GetMapping
    public String agenda(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/auth/login";

        model.addAttribute("username", user.getUsername());
        model.addAttribute("name",     user.getName());
        model.addAttribute("activePage", "agenda");

        return "appointment/agenda";
    }
}
