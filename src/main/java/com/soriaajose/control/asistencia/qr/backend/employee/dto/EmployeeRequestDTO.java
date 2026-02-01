package com.soriaajose.control.asistencia.qr.backend.employee.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class EmployeeRequestDTO {

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String password;
    private Long workShiftId;

}
