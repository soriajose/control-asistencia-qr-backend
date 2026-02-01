package com.soriaajose.control.asistencia.qr.backend.workshift.service;

import com.soriaajose.control.asistencia.qr.backend.organization.model.Organization;
import com.soriaajose.control.asistencia.qr.backend.user.model.User;
import com.soriaajose.control.asistencia.qr.backend.user.repository.UserRepository;
import com.soriaajose.control.asistencia.qr.backend.workshift.dto.WorkShiftRequestDTO;
import com.soriaajose.control.asistencia.qr.backend.workshift.dto.WorkShiftResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.workshift.model.WorkShift;
import com.soriaajose.control.asistencia.qr.backend.workshift.repository.WorkShiftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WorkShiftService {

    private final WorkShiftRepository workShiftRepository;
    private final UserRepository userRepository;


    @Transactional
    public Long createWorkShift(WorkShiftRequestDTO request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Organization organization = user.getPerson().getOrganization();

        WorkShift shift = WorkShift.builder()
                .name(request.getName())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .organization(organization)
                .createdAt(LocalDateTime.now())
                .createdBy(user.getUsername())
                .build();

        WorkShift workshift = workShiftRepository.save(shift);
        return workshift.getId();
    }

    @Transactional
    public Long updateWorkShift(Long id, WorkShiftRequestDTO request) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Organization organization = user.getPerson().getOrganization();

        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        if (!workShift.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("Acceso denegado: No puedes editar un turno de otra organización.");
        }

        workShift.setName(request.getName());
        workShift.setStartTime(request.getStartTime());
        workShift.setEndTime(request.getEndTime());
        workShift.setOrganization(organization);
        workShift.setUpdatedAt(LocalDateTime.now());
        workShift.setUpdatedBy(user.getUsername());


        return workShiftRepository.save(workShift).getId();
    }

    @Transactional
    public void deleteWorkShift(Long id) {
        WorkShift workShift = workShiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Organization organization = user.getPerson().getOrganization();

        if (!workShift.getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("Acceso denegado: No puedes eliminar un turno de otra organización.");
        }

        workShift.setDeletedAt(LocalDateTime.now());
        workShift.setDeletedBy(user.getUsername());
        workShiftRepository.save(workShift);
    }

    @Transactional(readOnly = true)
    public List<WorkShiftResponseDTO> findAllWorkShifts() {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Long organizationId = user.getPerson().getOrganization().getId();

        List<WorkShift> workShifts = workShiftRepository.findByOrganizationIdAndDeletedAtIsNull(organizationId);

        List<WorkShiftResponseDTO> workShiftResponseDTO = workShifts.stream().map(s ->
                new WorkShiftResponseDTO(s.getId(), s.getName(), s.getStartTime(), s.getEndTime())).toList();

        return workShiftResponseDTO;

    }

}
