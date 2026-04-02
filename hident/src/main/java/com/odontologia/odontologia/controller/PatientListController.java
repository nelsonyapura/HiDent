package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.Appointment;
import com.odontologia.odontologia.model.Patient;
import com.odontologia.odontologia.model.User;
import com.odontologia.odontologia.service.AppointmentService;
import com.odontologia.odontologia.service.PatientService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientListController {

    private final PatientService patientService;
    private final AppointmentService appointmentService;

    @GetMapping
    public String list(@AuthenticationPrincipal User user,
                       @RequestParam(value = "q", required = false) String query,
                       Model model) {

        if (user == null) return "redirect:/auth/login";

        var patients = patientService.search(query);

        Map<Long, String> lastVisitMap = new HashMap<>();
        Map<Long, String> nextApptMap  = new HashMap<>();

        for (Patient p : patients) {
            Long pid = p.getIdPatient();

            appointmentService.findLastVisit(pid).ifPresent(a ->
                lastVisitMap.put(pid, a.getAppointmentDateFormatted())
            );

            appointmentService.findNextAppointment(pid).ifPresent(a ->
                nextApptMap.put(pid, a.getAppointmentDateFormatted())
            );
        }

        model.addAttribute("username",   user.getUsername());
        model.addAttribute("name",       user.getName());
        model.addAttribute("activePage", "patients");
        model.addAttribute("patients",   patients);
        model.addAttribute("totalCount", patients.size());
        model.addAttribute("query",      query != null ? query : "");
        model.addAttribute("lastVisitMap", lastVisitMap);
        model.addAttribute("nextApptMap",  nextApptMap);

        return "patient/list";
    }

    @PostMapping("/{id}/delete")
    public String delete(@AuthenticationPrincipal User user,
                         @PathVariable Long id,
                         @RequestParam(value = "q", required = false) String query,
                         RedirectAttributes ra) {

        if (user == null) return "redirect:/auth/login";

        try {
            String actor = user.getName() != null ? user.getName() : user.getUsername();
            patientService.softDelete(id, actor);
            ra.addFlashAttribute("successMessage", "Paciente eliminado correctamente.");
        } catch (Exception e) {
            ra.addFlashAttribute("errorMessage", "Error al eliminar: " + e.getMessage());
        }

        String redirect = "redirect:/patients";
        if (query != null && !query.isBlank()) redirect += "?q=" + query;
        return redirect;
    }
}
