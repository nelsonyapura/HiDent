package com.odontologia.odontologia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ToothUpdateRequest {
    private String toothCode;
    private String nomenclature;
    private String condition;
    private String surfaces;
    private String color;
    private String note;
    private String toothCodeEnd;
}
