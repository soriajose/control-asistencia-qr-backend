package com.soriaajose.control.asistencia.qr.backend.user.model;

import com.soriaajose.control.asistencia.qr.backend.person.model.Person;
import com.soriaajose.control.asistencia.qr.backend.role.model.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updateddAt;

    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private String deletedBy;

    // El CascadeTyype.ALL es muy importante ya que a la hora de guardar
    // hace lo mismo con la clase Person
    // analiza el objeto User, ve lo que contiene el objeto Person y si no tiene un ID, hace un insert
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "person_id", referencedColumnName = "id")
    private Person person;

    @ManyToMany(fetch = FetchType.EAGER) // Cuando carga la tabla User, trae inmediantamente los roles tambien
    @JoinTable(
            name = "users_roles", // NOMBRE DE LA TABLA INTERMEDIA
            joinColumns = @JoinColumn(name = "user_id"), // TU LLAVE - GUARDA EL ID DEL USER
            inverseJoinColumns = @JoinColumn(name = "role_id") // LA OTRA LLAVE - GUARDA EL ID DEL ROL
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    @NonNull
    public String getPassword() {
        return this.password;
    }

    @Override
    @NonNull
    public String getUsername() {
        return this.username;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (roles == null) {
            return List.of();
        }

        // Acá convierto la entidad "Role" a "SimpleGranteAuthority
        return roles.stream().
                map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    // Si el usuario tiene fecha de eliminacion, está desactivado
    @Override
    public boolean isEnabled() {
        return deletedAt == null;
    }
}
