package com.soriaajose.control.asistencia.qr.backend.attendancehistory.service;

import com.soriaajose.control.asistencia.qr.backend.attendancehistory.dto.AttendanceSessionDTO;
import com.soriaajose.control.asistencia.qr.backend.attendancehistory.dto.EmployeeComboResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.attendancehistory.enums.AttendanceType;
import com.soriaajose.control.asistencia.qr.backend.attendancehistory.model.AttendanceHistory;
import com.soriaajose.control.asistencia.qr.backend.attendancehistory.repository.AttendanceHistoryRepository;
import com.soriaajose.control.asistencia.qr.backend.organization.model.Organization;
import com.soriaajose.control.asistencia.qr.backend.user.model.User;
import com.soriaajose.control.asistencia.qr.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceHistoryService {

    private final AttendanceHistoryRepository historyRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<EmployeeComboResponseDTO> getAllEmployeeCombo() {

        try {
            // 1. Obtener Admin Logueado y su Organización
            String username = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

            Organization organization = user.getPerson().getOrganization();

            List<EmployeeComboResponseDTO> userList =
                    userRepository.findEmployeeComboAttendanceHistory(organization.getId())
                            .stream()
                            .map(p -> new EmployeeComboResponseDTO(
                                    p.getId(),
                                    p.getFirstName(),
                                    p.getLastName()
                            ))
                            .toList();

            if (userList.isEmpty()) {
                return Collections.emptyList();
            }

            return userList;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


    public List<AttendanceSessionDTO> getEmployeeHistory(Long employeeId) {
        // 1. Obtenemos todos los eventos crudos (Entradas y Salidas mezcladas)
        List<AttendanceHistory> rawEvents = historyRepository.findByUserIdOrderByAttendanceDateAsc(employeeId);

        List<AttendanceSessionDTO> sessions = new ArrayList<>();
        LocalDateTime tempIn = null; // Variable temporal para guardar la entrada pendiente

        // 2. Algoritmo de emparejamiento
        for (AttendanceHistory event : rawEvents) {

            // Si es ENTRADA, guardamos la hora y esperamos
            if (event.getAttendanceType() == AttendanceType.ENTRADA) {
                // Si ya había una entrada pendiente sin cerrar (ej: se olvidó marcar salida),
                // cerramos la anterior forzosamente o la ignoramos (depende de tu regla de negocio).
                // Aquí simplemente sobreescribimos para tomar la más reciente.
                tempIn = event.getAttendanceDate();
            }
            // Si es SALIDA y tenemos una entrada guardada -> ¡Tenemos pareja!
            else if (event.getAttendanceType() == AttendanceType.SALIDA && tempIn != null) {
                sessions.add(AttendanceSessionDTO.builder()
                        .clockIn(tempIn)
                        .clockOut(event.getAttendanceDate())
                        .duration(calculateDuration(tempIn, event.getAttendanceDate()))
                        .build());
                tempIn = null; // Reset para la siguiente sesión
            }
        }

        // 3. Caso Borde: El empleado está trabajando AHORA (tiene entrada pero no salida)
        if (tempIn != null) {
            sessions.add(AttendanceSessionDTO.builder()
                    .clockIn(tempIn)
                    .clockOut(null) // Aún no salió
                    .duration(null)
                    .build());
        }

        // 4. Invertimos la lista para que el frontend muestre lo más reciente arriba
        Collections.reverse(sessions);

        return sessions;
    }

    private String calculateDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) return "-";
        long minutes = java.time.Duration.between(start, end).toMinutes();
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;

        // Si son 0 horas, mostramos solo los minutos
        if (hours == 0) {
            return String.format("%dm", remainingMinutes);
        }
        return String.format("%dh %dm", hours, remainingMinutes);
    }

}
