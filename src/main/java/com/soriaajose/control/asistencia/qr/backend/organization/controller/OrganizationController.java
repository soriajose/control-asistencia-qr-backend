package com.soriaajose.control.asistencia.qr.backend.organization.controller;

import com.soriaajose.control.asistencia.qr.backend.organization.service.OrganizationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/organization")
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/current-qr")
    public ResponseEntity<?> getCurrentQr() {
        try {
            return ResponseEntity.ok(organizationService.getCurrentQrData());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PatchMapping("/update-qr-name")
    public ResponseEntity<?> updateQrName(@RequestBody String name) {
        try {
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("El nombre no puede estar vacío");
            }

            organizationService.updateQrName(name);

            return ResponseEntity.ok().build();

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error actualizando nombre: " + e.getMessage());
        }
    }

    @PatchMapping("/regenerate-qr")
    public ResponseEntity<?> regenerate() {
        try {
            return ResponseEntity.ok(organizationService.regenerateQrToken());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ocurrió un error inesperado: " + e.getMessage());
        }
    }

    @GetMapping("/tolerance")
    public ResponseEntity<Integer> getTolerance() {
        return ResponseEntity.ok(organizationService.getTolerance());
    }

    @PutMapping("/update-tolerance")
    public ResponseEntity<Void> updateTolerance(@RequestBody Integer tolerance) {
        organizationService.updateTolerance(tolerance);
        return ResponseEntity.ok().build();
    }

}
