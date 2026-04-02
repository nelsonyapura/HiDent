package com.odontologia.odontologia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class AppointmentRequest {
    private Long patientId;
    private String appointmentDate;
    private String startTime;
    private String endTime;
    private String reason;
    private String notes;
    private String assignedTo;
    private String status;
}
