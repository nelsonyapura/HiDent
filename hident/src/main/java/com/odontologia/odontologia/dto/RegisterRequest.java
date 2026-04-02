package com.odontologia.odontologia.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {
    private String name;
    private String dni;
    private String phone;
    private String email;
    private String username;
    private String password;
    private String confirmPassword;
}
