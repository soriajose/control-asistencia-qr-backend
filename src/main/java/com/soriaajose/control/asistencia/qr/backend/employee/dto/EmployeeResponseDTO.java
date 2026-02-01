package com.soriaajose.control.asistencia.qr.backend.employee.dto;

import lombok.*;

import java.time.LocalTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeeResponseDTO {

    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    // --- DATOS DEL TURNO ---
    private Long workShiftId;
    private String workShiftName;
    private LocalTime workShiftStartTime;
    private LocalTime workShiftEndTime;

}
