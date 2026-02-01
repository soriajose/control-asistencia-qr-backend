package com.soriaajose.control.asistencia.qr.backend.organization.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@Builder
public class QrDataResponseDTO {

    private String token;
    private String name;

}
