package com.soriaajose.control.asistencia.qr.backend.attendancehistory.model;

import com.soriaajose.control.asistencia.qr.backend.attendancehistory.enums.AttendanceType;
import com.soriaajose.control.asistencia.qr.backend.user.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "attendance_history")
@Entity
public class AttendanceHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "attendance_type")
    private AttendanceType attendanceType;

    @Column(name = "attendance_date")
    private LocalDateTime attendanceDate;

}
