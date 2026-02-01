package com.soriaajose.control.asistencia.qr.backend.security.auth.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResponseDTO {
    private String token;
}
