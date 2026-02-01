package com.soriaajose.control.asistencia.qr.backend.user.repository;

import com.soriaajose.control.asistencia.qr.backend.attendancehistory.proyection.EmployeeComboProjection;
import com.soriaajose.control.asistencia.qr.backend.user.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByUsernameAndDeletedAtIsNull(String username);

    @Query("SELECT u.id AS id, p.firstName AS firstName, p.lastName AS lastName " +
            "FROM User u " +
            "JOIN u.person p " +
            "WHERE p.organization.id = :orgId " +
            "AND u.deletedAt IS NULL " +
            "ORDER BY p.lastName ASC")
    List<EmployeeComboProjection> findEmployeeComboAttendanceHistory(@Param("orgId") Long orgId);

    @Query("SELECT u FROM User u " +
            "JOIN u.person p " + // <--- AQUÍ ESTÁ EL JOIN EXPLÍCITO
            "WHERE p.organization.id = :orgId " +
            "AND u.deletedAt IS NULL " +
            "AND (" +
            "  :search IS NULL OR :search = '' OR " +
            "  LOWER(p.firstName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "  LOWER(p.lastName) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "  LOWER(p.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " + // Busca en email de Person
            "  LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))" +  // Busca en username de User
            ")")
    Page<User> searchByOrganization(@Param("orgId") Long orgId,
                                    @Param("search") String search,
                                    Pageable pageable);

}
