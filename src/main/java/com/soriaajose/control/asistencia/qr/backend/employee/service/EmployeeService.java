package com.soriaajose.control.asistencia.qr.backend.employee.service;

import com.soriaajose.control.asistencia.qr.backend.employee.dto.EmployeeRequestDTO;
import com.soriaajose.control.asistencia.qr.backend.employee.dto.EmployeeResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.employee.dto.PageDTO;
import com.soriaajose.control.asistencia.qr.backend.organization.model.Organization;
import com.soriaajose.control.asistencia.qr.backend.person.model.Person;
import com.soriaajose.control.asistencia.qr.backend.role.model.Role;
import com.soriaajose.control.asistencia.qr.backend.role.repository.RoleRepository;
import com.soriaajose.control.asistencia.qr.backend.user.model.User;
import com.soriaajose.control.asistencia.qr.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    @Transactional
    public Long createEmployee(EmployeeRequestDTO request) {
        // 1. Obtener Admin Logueado y su Organización
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        Organization organization = user.getPerson().getOrganization();

        // 2. Validar que el username o email no existan ya (Globalmente o por Org)
        if (userRepository.findByUsernameAndDeletedAtIsNull(request.getUsername()).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe.");
        }

        // 3. Crear Persona
        Person person = new Person();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getEmail());
        person.setPhone(request.getPhone());
        person.setOrganization(organization); // <--- ASIGNACIÓN AUTOMÁTICA
        person.setCreatedBy(user.getUsername());
        person.setCreatedAt(LocalDateTime.now());

        // 4. Crear Usuario
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword())); // Encriptar
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setCreatedBy(user.getUsername());
        newUser.setPerson(person);

        // 5. Asignar Rol EMPLOYEE
        Role employeeRole = roleRepository.findByName("EMPLOYEE")
                .orElseThrow(() -> new RuntimeException("Error: Rol no encontrado."));
        newUser.setRoles(Collections.singleton(employeeRole));

        // 6. Guardar y Retornar
        return userRepository.save(newUser).getId();
    }

    @Transactional
    public Long updateEmployee(Long id, EmployeeRequestDTO request) {
        // 1. Obtener Admin Logueado y su Organización
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        Organization organization = admin.getPerson().getOrganization();

        // 2. Buscar empleado y VERIFICAR QUE SEA DE MI ORGANIZACIÓN
        User employee = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (!employee.getPerson().getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("Acceso denegado: No puedes editar este empleado.");
        }

        // 3. Actualizar datos
        Person person = employee.getPerson();
        person.setFirstName(request.getFirstName());
        person.setLastName(request.getLastName());
        person.setEmail(request.getEmail());
        person.setPhone(request.getPhone());
        person.setUpdatedAt(LocalDateTime.now());
        person.setUpdatedBy(admin.getUsername());

        // Solo actualizamos password si viene en el request (no es nula ni vacía)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            employee.setPassword(passwordEncoder.encode(request.getPassword()));
            employee.setUpdateddAt(LocalDateTime.now());
            employee.setUpdatedBy(admin.getUsername());
        }

        // Nota: Generalmente no dejamos cambiar el username, pero si quieres, hazlo aquí.
        return userRepository.save(employee).getId();
    }

    @Transactional
    public void deleteEmployee(Long id) {
        // 1. Obtener Admin Logueado y su Organización
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Admin no encontrado"));

        Organization organization = admin.getPerson().getOrganization();

        User employee = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        if (!employee.getPerson().getOrganization().getId().equals(organization.getId())) {
            throw new RuntimeException("Acceso denegado.");
        }

        // Evitar que el admin se borre a sí mismo
        if (employee.getId().equals(admin.getId())) {
            throw new RuntimeException("No puedes eliminar tu propia cuenta.");
        }

        Person person = employee.getPerson();
        person.setUpdatedAt(LocalDateTime.now());
        person.setUpdatedBy(admin.getUsername());
        employee.setPerson(person);
        employee.setDeletedAt(LocalDateTime.now());
        employee.setDeletedBy(admin.getUsername());

        userRepository.save(employee);
    }

    @Transactional(readOnly = true)
    public PageDTO<EmployeeResponseDTO> getMyEmployeesPagination(String search, int page, int size) {

        // 1. Obtenemos solo el nombre de usuario (String) del contexto. Esto es seguro.
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Buscamos al usuario "Fresco" en la BD.
        // Como estamos dentro de @Transactional, este usuario estará "Vivo" (Attached).
        User currentUser = userRepository.findByUsername(currentUsername)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 3. Extraer su ID de Organización (Blindaje SaaS)
        Long organizationId = currentUser.getPerson().getOrganization().getId();

        // 4. Configurar la Paginación
        // Ordenamos por ID descendente para ver los nuevos primero, o por apellido.
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        // 5. Ejecutar la búsqueda en base de datos
        Page<User> userPage = userRepository.searchByOrganization(organizationId, search, pageable);

        // 6. Mapeo de Entidad a DTO (Devuelve Page<EmployeeResponseDTO>)
        Page<EmployeeResponseDTO> employeePage = userPage.map(this::mapToResponseDTO);

        // 7. CAMBIO AQUÍ: Envolvemos el resultado en tu clase custom
        return new PageDTO<>(employeePage);
    }

    // Método auxiliar de mapeo
    private EmployeeResponseDTO mapToResponseDTO(User user) {
        return EmployeeResponseDTO.builder()
                .id(user.getId())
                .firstName(user.getPerson().getFirstName())
                .lastName(user.getPerson().getLastName())
                .email(user.getPerson().getEmail())
                .phone(user.getPerson().getPhone())
                .username(user.getUsername())
                .build();
    }
}
