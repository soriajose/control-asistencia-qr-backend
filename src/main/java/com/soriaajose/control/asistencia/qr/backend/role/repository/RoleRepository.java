package com.soriaajose.control.asistencia.qr.backend.role.repository;

import com.soriaajose.control.asistencia.qr.backend.role.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}
