package com.soriaajose.control.asistencia.qr.backend.workshift.repository;

import com.soriaajose.control.asistencia.qr.backend.workshift.model.WorkShift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkShiftRepository extends JpaRepository<WorkShift, Long> {
    List<WorkShift> findByOrganizationIdAndDeletedAtIsNull(Long organizationId);
}
