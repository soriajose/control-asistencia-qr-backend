package com.soriaajose.control.asistencia.qr.backend.security.auth.controller;

import com.soriaajose.control.asistencia.qr.backend.security.auth.dto.AuthLoginRequestDTO;
import com.soriaajose.control.asistencia.qr.backend.security.auth.dto.AuthRegisterRequestDTO;
import com.soriaajose.control.asistencia.qr.backend.security.auth.dto.AuthResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.security.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody AuthLoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@RequestBody AuthRegisterRequestDTO registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

}
