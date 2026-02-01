package com.soriaajose.control.asistencia.qr.backend.attendancehistory.controller;

import com.soriaajose.control.asistencia.qr.backend.attendancehistory.dto.AttendanceSessionDTO;
import com.soriaajose.control.asistencia.qr.backend.attendancehistory.dto.EmployeeComboResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.attendancehistory.service.AttendanceHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/attendance-history")
public class AttendanceHistoryController {


    @Autowired
    private AttendanceHistoryService attendanceHistoryService;

    @GetMapping("/employees-combo")
    public ResponseEntity<List<EmployeeComboResponseDTO>> getAllEmployee() {
        return ResponseEntity.ok(attendanceHistoryService.getAllEmployeeCombo());
    }

    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<List<AttendanceSessionDTO>> getEmployeeHistory(@PathVariable Long employeeId) {
        return ResponseEntity.ok(attendanceHistoryService.getEmployeeHistory(employeeId));
    }

}
