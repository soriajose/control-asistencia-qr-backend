package com.soriaajose.control.asistencia.qr.backend.attendancehistory.dto;

import lombok.*;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AttendanceSessionDTO {
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private String duration;
}
