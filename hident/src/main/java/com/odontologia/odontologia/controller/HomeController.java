package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final PatientService patientService;

    @GetMapping("/")
    public String root() {
        return "redirect:/home";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal User user, Model model) {
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("activePage", "home");
        return "home";
    }

    @GetMapping("/api/patient/search-dni")
    @ResponseBody
    public ResponseEntity<?> searchByDni(
            @AuthenticationPrincipal User user,
            @RequestParam("dni") String dni) {

        if (user == null) return ResponseEntity.status(401).build();
        if (dni == null || !dni.matches("\\d{8}"))
            return ResponseEntity.badRequest().body(Map.of("error", "DNI inválido"));

        Optional<Patient> opt = patientService.findByDni(dni);
        if (opt.isEmpty()) return ResponseEntity.notFound().build();

        Patient p = opt.get();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("id",       p.getIdPatient());
        result.put("fullName", p.getFullName());
        result.put("dni",      p.getDni());
        result.put("phone",    p.getPhone());
        result.put("district", p.getDistrict());
        result.put("age",      p.getEffectiveAge());
        result.put("minor",    p.isMinor());
        result.put("initials",
            p.getFirstName().substring(0, 1).toUpperCase() +
            p.getLastName().substring(0, 1).toUpperCase());

        return ResponseEntity.ok(result);
    }

    @PostMapping("/home/register")
    public String registerPatient(
            @AuthenticationPrincipal User user,
            @RequestParam("dni") String dni,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam(value = "gender",         required = false) String gender,
            @RequestParam(value = "birthDate",       required = false) String birthDate,
            @RequestParam(value = "phone",           required = false) String phone,
            @RequestParam(value = "email",           required = false) String email,
            @RequestParam(value = "address",         required = false) String address,
            @RequestParam(value = "district",        required = false) String district,
            @RequestParam(value = "allergies",       required = false) String allergies,
            @RequestParam(value = "medicalNotes",    required = false) String medicalNotes,
            @RequestParam(value = "dentalHistory",   required = false) String dentalHistory,
            @RequestParam(value = "guardianName",    required = false) String guardianName,
            @RequestParam(value = "guardianPhone",   required = false) String guardianPhone,
            @RequestParam(value = "emergencyPhone",  required = false) String emergencyPhone,
            @RequestParam(value = "age",             required = false) String age,
            RedirectAttributes redirectAttributes) {

        if (user == null) return "redirect:/auth/login";
        try {
            String createdBy = user.getName() != null ? user.getName() : user.getUsername();
            Patient patient = patientService.register(
                    dni, firstName, lastName, gender, birthDate,
                    phone, email, address, district, allergies, medicalNotes,
                    dentalHistory, guardianName, guardianPhone, emergencyPhone,
                    age, createdBy);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Paciente " + patient.getFullName() +
                    " registrado exitosamente (DNI: " + patient.getDni() + ")");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/home";
    }
}
