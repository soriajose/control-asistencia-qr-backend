package com.soriaajose.control.asistencia.qr.backend.organization.service;

import com.soriaajose.control.asistencia.qr.backend.organization.dto.QrDataResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.organization.model.Organization;
import com.soriaajose.control.asistencia.qr.backend.organization.repository.OrganizationRepository;
import com.soriaajose.control.asistencia.qr.backend.user.model.User;
import com.soriaajose.control.asistencia.qr.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final UserRepository userRepository;


    @Transactional
    public QrDataResponseDTO getCurrentQrData() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Organization org = user.getPerson().getOrganization();

        return QrDataResponseDTO.builder()
                .name(org.getQrName())
                .token(org.getQrToken())
                .build();
    }


    @Transactional
    public void updateQrName(String newName) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Organization org = user.getPerson().getOrganization();

        org.setQrName(newName);
        organizationRepository.save(org);
    }

    @Transactional
    public String regenerateQrToken() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Organization org = user.getPerson().getOrganization();

        String newToken = UUID.randomUUID().toString();

        org.setQrToken(newToken);

        organizationRepository.save(org);

        return newToken;
    }


}
