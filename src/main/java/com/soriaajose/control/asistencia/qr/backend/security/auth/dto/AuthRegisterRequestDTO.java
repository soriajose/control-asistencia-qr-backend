package com.soriaajose.control.asistencia.qr.backend.security.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthRegisterRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private String phone;
    private String subdomain;

}
