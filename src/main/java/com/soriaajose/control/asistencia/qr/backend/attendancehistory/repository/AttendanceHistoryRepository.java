package com.soriaajose.control.asistencia.qr.backend.attendancehistory.repository;

import com.soriaajose.control.asistencia.qr.backend.attendancehistory.model.AttendanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceHistoryRepository extends JpaRepository<AttendanceHistory, Long> {

    // Traemos los registros ordenados por fecha ascendente para poder
    // unir la entrada de la mañana con la salida de la tarde fácilmente.
    List<AttendanceHistory> findByUserIdOrderByAttendanceDateAsc(Long userId);

}
