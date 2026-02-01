package com.soriaajose.control.asistencia.qr.backend.workshift.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WorkShiftRequestDTO {

    private String name;
    private LocalTime startTime;
    private LocalTime endTime;

}
