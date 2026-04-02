package com.odontologia.odontologia.controller;

import com.odontologia.odontologia.model.*;
import com.odontologia.odontologia.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/patient")
@RequiredArgsConstructor
public class PatientController {

    private final PatientService patientService;
    private final AnamnesisService anamnesisService;
    private final EvolutionNoteService evolutionNoteService;
    private final DocumentService documentService;
    private final ConsentService consentService;
    private final OralRevisionService oralRevisionService;

    @GetMapping("/{id}")
    public String viewPatient(@AuthenticationPrincipal User user,
                              @PathVariable("id") Long id, Model model) {
        if (user == null) return "redirect:/auth/login";

        Optional<Patient> opt = patientService.findById(id);
        if (opt.isEmpty()) return "redirect:/home";

        Patient patient = opt.get();
        Anamnesis anamnesis = anamnesisService.findByPatientId(id).orElse(new Anamnesis());
        List<EvolutionNote> notes = evolutionNoteService.findByPatientId(id);
        List<Document> documents = documentService.findByPatientId(id);
        List<Consent> consents = consentService.findByPatientId(id);
        List<OralRevision> oralRevisions = oralRevisionService.findByPatientId(id);

        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("activePage", "patients");
        model.addAttribute("patient", patient);
        model.addAttribute("anamnesis", anamnesis);
        model.addAttribute("notes", notes);
        model.addAttribute("notesCount", notes.size());
        model.addAttribute("documents", documents);
        model.addAttribute("documentsCount", documents.size());
        model.addAttribute("consents", consents);
        model.addAttribute("consentsCount", consents.size());
        model.addAttribute("consentTypes", consentService.getConsentTypes());
        model.addAttribute("oralRevisions", oralRevisions);
        model.addAttribute("oralRevisionsCount", oralRevisions.size());

        return "patient/profile";
    }

    @PostMapping("/{id}/edit")
    public String editPatient(@AuthenticationPrincipal User user,
                              @PathVariable("id") Long id,
                              @RequestParam("firstName") String firstName,
                              @RequestParam("lastName") String lastName,
                              @RequestParam(value = "gender", required = false) String gender,
                              @RequestParam(value = "birthDate", required = false) String birthDate,
                              @RequestParam(value = "phone", required = false) String phone,
                              @RequestParam(value = "email", required = false) String email,
                              @RequestParam(value = "address", required = false) String address,
                              @RequestParam(value = "district", required = false) String district,
                              @RequestParam(value = "allergies", required = false) String allergies,
                              @RequestParam(value = "medicalNotes", required = false) String medicalNotes,
                              @RequestParam(value = "dentalHistory", required = false) String dentalHistory,
                              @RequestParam(value = "guardianName", required = false) String guardianName,
                              @RequestParam(value = "guardianPhone", required = false) String guardianPhone,
                              @RequestParam(value = "emergencyPhone", required = false) String emergencyPhone,
                              @RequestParam(value = "age", required = false) String age,
                              RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        try {
            String updatedBy = user.getName() != null ? user.getName() : user.getUsername();
            patientService.update(id, firstName, lastName, gender, birthDate,
                    phone, email, address, district, allergies, medicalNotes,
                    dentalHistory, guardianName, guardianPhone, emergencyPhone,
                    age, updatedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Datos del paciente actualizados correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage());
        }
        return "redirect:/patient/" + id + "?tab=tab-datos";
    }

    @PostMapping("/{id}/anamnesis")
    public String saveAnamnesis(@AuthenticationPrincipal User user,
                                @PathVariable("id") Long id,
                                @ModelAttribute Anamnesis anamnesisData,
                                RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        Optional<Patient> opt = patientService.findById(id);
        if (opt.isEmpty()) return "redirect:/home";
        try {
            anamnesisService.saveOrUpdate(opt.get(), anamnesisData);
            redirectAttributes.addFlashAttribute("successMessage", "Anamnesis guardada correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar anamnesis: " + e.getMessage());
        }
        return "redirect:/patient/" + id + "?tab=tab-anamnesis";
    }

    @PostMapping("/{id}/note")
    public String addNote(@AuthenticationPrincipal User user,
                          @PathVariable("id") Long id,
                          @RequestParam("reason") String reason,
                          @RequestParam(value = "diagnosis", required = false) String diagnosis,
                          @RequestParam(value = "treatmentDone", required = false) String treatmentDone,
                          @RequestParam(value = "toothNumber", required = false) String toothNumber,
                          @RequestParam(value = "observations", required = false) String observations,
                          @RequestParam(value = "indications", required = false) String indications,
                          @RequestParam(value = "nextAppointment", required = false) String nextAppointment,
                          @RequestParam(value = "scheduleNextAppt", required = false) String scheduleNextAppt,
                          @RequestParam(value = "nextApptDate", required = false) String nextApptDate,
                          @RequestParam(value = "nextApptStart", required = false) String nextApptStart,
                          @RequestParam(value = "nextApptEnd", required = false) String nextApptEnd,
                          @RequestParam(value = "nextApptReason", required = false) String nextApptReason,
                          @RequestParam(value = "nextApptDoctor", required = false) String nextApptDoctor,
                          RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        Optional<Patient> opt = patientService.findById(id);
        if (opt.isEmpty()) return "redirect:/home";
        try {
            String createdBy = user.getName() != null ? user.getName() : user.getUsername();
            boolean doSchedule = "true".equals(scheduleNextAppt);

            var note = evolutionNoteService.addNote(opt.get(), reason, diagnosis,
                    treatmentDone, toothNumber, observations, indications,
                    nextAppointment, doSchedule, nextApptDate, nextApptStart,
                    nextApptEnd, nextApptReason, nextApptDoctor, createdBy);

            String msg = "Nota de evolución registrada";
            if (note.getAppointmentScheduled() != null && note.getAppointmentScheduled()) {
                msg += " y cita agendada exitosamente";
            }
            redirectAttributes.addFlashAttribute("successMessage", msg);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/patient/" + id + "?tab=tab-notas";
    }

    @PostMapping("/{id}/note/{noteId}/edit")
    public String editNote(@AuthenticationPrincipal User user,
                           @PathVariable("id") Long id,
                           @PathVariable("noteId") Long noteId,
                           @RequestParam("reason") String reason,
                           @RequestParam(value = "diagnosis", required = false) String diagnosis,
                           @RequestParam(value = "treatmentDone", required = false) String treatmentDone,
                           @RequestParam(value = "toothNumber", required = false) String toothNumber,
                           @RequestParam(value = "observations", required = false) String observations,
                           @RequestParam(value = "indications", required = false) String indications,
                           @RequestParam(value = "nextAppointment", required = false) String nextAppointment,
                           RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        try {
            evolutionNoteService.updateNote(noteId, id, reason, diagnosis,
                    treatmentDone, toothNumber, observations, indications, nextAppointment);
            redirectAttributes.addFlashAttribute("successMessage", "Nota de evolución actualizada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al editar nota: " + e.getMessage());
        }
        return "redirect:/patient/" + id + "?tab=tab-notas";
    }

    @PostMapping("/{id}/note/{noteId}/delete")
    public String deleteNote(@AuthenticationPrincipal User user,
                             @PathVariable("id") Long id,
                             @PathVariable("noteId") Long noteId,
                             RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        try {
            evolutionNoteService.deleteNote(noteId, id);
            redirectAttributes.addFlashAttribute("successMessage", "Nota de evolución eliminada");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar nota: " + e.getMessage());
        }
        return "redirect:/patient/" + id + "?tab=tab-notas";
    }

    @PostMapping("/{id}/document/upload")
    public String uploadDocument(@AuthenticationPrincipal User user,
                                 @PathVariable("id") Long id,
                                 @RequestParam("file") MultipartFile file,
                                 @RequestParam(value = "description", required = false) String description,
                                 @RequestParam(value = "category", required = false) String category,
                                 RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        Optional<Patient> opt = patientService.findById(id);
        if (opt.isEmpty()) return "redirect:/home";
        try {
            String uploadedBy = user.getName() != null ? user.getName() : user.getUsername();
            documentService.upload(opt.get(), file, description, category, uploadedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Documento subido correctamente");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/patient/" + id + "?tab=tab-docs";
    }

    @GetMapping("/{id}/document/{docId}/download")
    public ResponseEntity<Resource> downloadDocument(@PathVariable("id") Long id,
                                                      @PathVariable("docId") Long docId) {
        try {
            Optional<Document> docOpt = documentService.findByIdAndPatient(docId, id);
            Optional<Path> pathOpt = documentService.getFilePath(docId, id);
            if (docOpt.isEmpty() || pathOpt.isEmpty()) return ResponseEntity.notFound().build();

            Document doc = docOpt.get();
            Resource resource = new UrlResource(pathOpt.get().toUri());
            MediaType mediaType = switch (doc.getFileType() != null ? doc.getFileType().toUpperCase() : "PDF") {
                case "JPG", "JPEG" -> MediaType.IMAGE_JPEG;
                case "PNG" -> MediaType.IMAGE_PNG;
                default -> MediaType.APPLICATION_PDF;
            };
            return ResponseEntity.ok().contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + doc.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) { return ResponseEntity.internalServerError().build(); }
    }

    @PostMapping("/{id}/document/{docId}/delete")
    public String deleteDocument(@AuthenticationPrincipal User user,
                                 @PathVariable("id") Long id,
                                 @PathVariable("docId") Long docId,
                                 RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        try { documentService.delete(docId, id); redirectAttributes.addFlashAttribute("successMessage", "Documento eliminado"); }
        catch (Exception e) { redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/patient/" + id + "?tab=tab-docs";
    }

    @PostMapping("/{id}/consent")
    public String uploadConsent(@AuthenticationPrincipal User user,
                                @PathVariable("id") Long id,
                                @RequestParam("file") MultipartFile file,
                                @RequestParam("consentType") String consentType,
                                @RequestParam(value = "notes", required = false) String notes,
                                RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        Optional<Patient> opt = patientService.findById(id);
        if (opt.isEmpty()) return "redirect:/home";
        try {
            String createdBy = user.getName() != null ? user.getName() : user.getUsername();
            consentService.upload(opt.get(), file, consentType, notes, createdBy);
            redirectAttributes.addFlashAttribute("successMessage", "Consentimiento subido correctamente");
        } catch (Exception e) { redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/patient/" + id + "?tab=tab-consents";
    }

    @GetMapping("/{id}/consent/{consentId}/download")
    public ResponseEntity<Resource> downloadConsent(@PathVariable("id") Long id,
                                                      @PathVariable("consentId") Long consentId) {
        try {
            Optional<Consent> consentOpt = consentService.findById(consentId);
            Optional<Path> pathOpt = consentService.getFilePath(consentId);
            if (consentOpt.isEmpty() || pathOpt.isEmpty()) return ResponseEntity.notFound().build();

            Consent consent = consentOpt.get();
            Resource resource = new UrlResource(pathOpt.get().toUri());
            MediaType mediaType = MediaType.APPLICATION_PDF;
            if (consent.isImage()) {
                String lower = consent.getFileName().toLowerCase();
                mediaType = lower.endsWith(".png") ? MediaType.IMAGE_PNG : MediaType.IMAGE_JPEG;
            }
            return ResponseEntity.ok().contentType(mediaType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + consent.getFileName() + "\"")
                    .body(resource);
        } catch (Exception e) { return ResponseEntity.internalServerError().build(); }
    }

    @PostMapping("/{id}/consent/{consentId}/sign")
    public String signConsent(@AuthenticationPrincipal User user,
                              @PathVariable("id") Long id,
                              @PathVariable("consentId") Long consentId,
                              RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        try { consentService.markAsSigned(consentId); redirectAttributes.addFlashAttribute("successMessage", "Consentimiento marcado como firmado"); }
        catch (Exception e) { redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/patient/" + id + "?tab=tab-consents";
    }

    @PostMapping("/{id}/consent/{consentId}/delete")
    public String deleteConsent(@AuthenticationPrincipal User user,
                                @PathVariable("id") Long id,
                                @PathVariable("consentId") Long consentId,
                                RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        try { consentService.delete(consentId, id); redirectAttributes.addFlashAttribute("successMessage", "Consentimiento eliminado"); }
        catch (Exception e) { redirectAttributes.addFlashAttribute("errorMessage", e.getMessage()); }
        return "redirect:/patient/" + id + "?tab=tab-consents";
    }

    @GetMapping("/{id}/odontogram")
    public String odontogram(@AuthenticationPrincipal User user,
                            @PathVariable Long id, Model model) {
        if (user == null) return "redirect:/auth/login";
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("activePage", "patients");
        model.addAttribute("patient", patientService.findById(id).orElseThrow());
        return "patient/odontogram";
    }

    @PostMapping("/{id}/oral-revision")
    public String saveOralRevision(@AuthenticationPrincipal User user,
                                   @PathVariable("id") Long id,
                                   @ModelAttribute OralRevision revisionData,
                                   RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        Optional<Patient> opt = patientService.findById(id);
        if (opt.isEmpty()) return "redirect:/home";
        try {
            String createdBy = user.getName() != null ? user.getName() : user.getUsername();
            oralRevisionService.create(opt.get(), revisionData, createdBy);
            redirectAttributes.addFlashAttribute("successMessage", "Revisión local guardada correctamente");
        } catch (Exception e) { redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar revisión: " + e.getMessage()); }
        return "redirect:/patient/" + id + "?tab=tab-revision";
    }

    @PostMapping("/{id}/oral-revision/{revisionId}/edit")
    public String editOralRevision(@AuthenticationPrincipal User user,
                                   @PathVariable("id") Long id,
                                   @PathVariable("revisionId") Long revisionId,
                                   @ModelAttribute OralRevision revisionData,
                                   RedirectAttributes redirectAttributes) {
        if (user == null) return "redirect:/auth/login";
        try {
            String updatedBy = user.getName() != null ? user.getName() : user.getUsername();
            oralRevisionService.update(revisionId, revisionData, updatedBy);
            redirectAttributes.addFlashAttribute("successMessage", "Revisión local actualizada");
        } catch (Exception e) { redirectAttributes.addFlashAttribute("errorMessage", "Error al actualizar: " + e.getMessage()); }
        return "redirect:/patient/" + id + "?tab=tab-revision";
    }
}
