package com.soriaajose.control.asistencia.qr.backend.security.auth.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AuthLoginRequestDTO {
    private String username;
    private String password;
    private String subdomain;
}
