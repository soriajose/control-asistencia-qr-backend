package com.soriaajose.control.asistencia.qr.backend.employee.controller;

import com.soriaajose.control.asistencia.qr.backend.employee.dto.EmployeeRequestDTO;
import com.soriaajose.control.asistencia.qr.backend.employee.dto.EmployeeResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.employee.dto.PageDTO;
import com.soriaajose.control.asistencia.qr.backend.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<?> create(@RequestBody EmployeeRequestDTO request) {
        employeeService.createEmployee(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody EmployeeRequestDTO request) {
        employeeService.updateEmployee(id, request);
        return ResponseEntity.ok().build();
    }

    // LISTADO CON PAGINACIÓN
    @GetMapping
    public ResponseEntity<PageDTO<EmployeeResponseDTO>> getAllEmployeePagination(
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(employeeService.getMyEmployeesPagination(search, page, size));
    }

    @PatchMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok(HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
