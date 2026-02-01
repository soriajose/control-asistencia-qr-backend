package com.soriaajose.control.asistencia.qr.backend.workshift.controller;

import com.soriaajose.control.asistencia.qr.backend.workshift.dto.WorkShiftRequestDTO;
import com.soriaajose.control.asistencia.qr.backend.workshift.dto.WorkShiftResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.workshift.model.WorkShift;
import com.soriaajose.control.asistencia.qr.backend.workshift.service.WorkShiftService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/work-shifts")
@RequiredArgsConstructor
public class WorkShiftController {

    private final WorkShiftService workShiftService;

    @GetMapping("/list")
    public ResponseEntity<List<WorkShiftResponseDTO>> findAllWorkShifts() {
        List<WorkShiftResponseDTO> workShifts = workShiftService.findAllWorkShifts();
        return ResponseEntity.ok(workShifts);
    }

    @PostMapping
    public ResponseEntity<Long> create(@RequestBody WorkShiftRequestDTO request) {
        return ResponseEntity.ok(workShiftService.createWorkShift(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Long> update(@PathVariable Long id, @RequestBody WorkShiftRequestDTO request) {
        return ResponseEntity.ok(workShiftService.updateWorkShift(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        workShiftService.deleteWorkShift(id);
        return ResponseEntity.noContent().build();
    }

}
