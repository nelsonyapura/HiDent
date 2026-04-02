package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.dto.LoginRequest;
import com.odontologia.odontologia.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
        return "auth/login";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute LoginRequest request,
            Model model,
            HttpServletRequest httpRequest,
            HttpServletResponse response) {
        try {
            String token = authService.login(request);
            addJwtCookie(response, token, isSecureRequest(httpRequest));
            return "redirect:/home";
        } catch (Exception e) {
            model.addAttribute("loginRequest", request);
            model.addAttribute("error", e.getMessage());
            return "auth/login";
        }
    }

    private void addJwtCookie(HttpServletResponse response, String token, boolean secure) {
        StringBuilder cookie = new StringBuilder();
        cookie.append("JWT=").append(token).append(";");
        cookie.append(" Path=/;");
        cookie.append(" Max-Age=").append(60 * 60 * 24).append(";");
        cookie.append(" HttpOnly;");
        cookie.append(" SameSite=Strict;");
        if (secure) {
            cookie.append(" Secure;");
        }
        response.addHeader("Set-Cookie", cookie.toString());
    }

    private boolean isSecureRequest(HttpServletRequest request) {
        String proto = request.getHeader("X-Forwarded-Proto");
        if (proto != null) {
            return "https".equalsIgnoreCase(proto);
        }
        return request.isSecure();
    }
}
