package com.soriaajose.control.asistencia.qr.backend.organization.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "organizations")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "subdomain", unique = true, nullable = false)
    private String subdomain;

    @Column(name = "qr_name")
    private String qrName;

    @Column(name = "qr_token", unique = true, nullable = false)
    private String qrToken;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDate updateddAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDate deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

}
