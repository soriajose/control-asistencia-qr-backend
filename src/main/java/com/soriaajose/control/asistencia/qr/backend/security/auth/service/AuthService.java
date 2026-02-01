package com.soriaajose.control.asistencia.qr.backend.security.auth.service;

import com.soriaajose.control.asistencia.qr.backend.organization.model.Organization;
import com.soriaajose.control.asistencia.qr.backend.organization.repository.OrganizationRepository;
import com.soriaajose.control.asistencia.qr.backend.person.model.Person;
import com.soriaajose.control.asistencia.qr.backend.role.model.Role;
import com.soriaajose.control.asistencia.qr.backend.role.repository.RoleRepository;
import com.soriaajose.control.asistencia.qr.backend.security.auth.dto.AuthLoginRequestDTO;
import com.soriaajose.control.asistencia.qr.backend.security.auth.dto.AuthRegisterRequestDTO;
import com.soriaajose.control.asistencia.qr.backend.security.auth.dto.AuthResponseDTO;
import com.soriaajose.control.asistencia.qr.backend.security.jwt.JwtService;
import com.soriaajose.control.asistencia.qr.backend.user.model.User;
import com.soriaajose.control.asistencia.qr.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final OrganizationRepository organizationRepository;
    private final RoleRepository roleRepository;

    @Transactional(readOnly = true)
    public AuthResponseDTO login(AuthLoginRequestDTO loginRequest) {

        try {
            // 1. Autenticar usando el Manager de Spring (esto verifica usuario y password)
            // Si falla, lanza una excepción automáticamente.
            // Si las credenciales son incorrectas, lanza una excepción (BadCredentialsException)
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 2. Si llegamos acá, es que el usuario y password son correctos.
            // Buscamos al usuario en la DB para generar el token.
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(); // Aquí podrías lanzar una excepción personalizada


            // Busco el subdominio de la organizacion donde está registrado el usuario
            String userSubdomain = user.getPerson().getOrganization().getSubdomain();

            // Si son distintos, lanzo una excepción
            if (!userSubdomain.equalsIgnoreCase(loginRequest.getSubdomain())) {
                throw new BadCredentialsException("Acceso denegado: El usuario no perteneces a esta organización.");
            }

            // 3. Generamos el token
            String jwtToken = jwtService.generateToken(user);

            // 4. Retornamos el token
            return AuthResponseDTO.builder()
                    .token(jwtToken)
                    .build();
        } catch (Exception e) {
            System.out.println("CATCH: ------------> " + e.getMessage());
            throw new BadCredentialsException(e.getMessage());
        }
    }

    public AuthResponseDTO register(AuthRegisterRequestDTO registerRequest) {

        // Verificar si el usuario ya existe
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("El usuario ya existe");
        }

        // 2. Buscar la Organización por Subdominio (Lógica SaaS)
        Organization organization = organizationRepository.findBySubdomain(registerRequest.getSubdomain())
                .orElseThrow(() -> new RuntimeException("Organización no válida o no encontrada"));

        // 3. Buscar el Rol por defecto en la BD
        Role role = roleRepository.findByName("ADMIN")
                .orElseThrow(() -> new RuntimeException("Error de configuración: Rol 'ADMIN' no existe en la BD"));

        // 4. Crear la Persona (Datos del mundo real)

        // Creo la persona
        Person person = new Person();
        person.setFirstName(registerRequest.getFirstName());
        person.setLastName(registerRequest.getLastName());
        person.setEmail(registerRequest.getEmail());
        person.setPhone(registerRequest.getPhone());
        person.setOrganization(organization);

        // Crear el usuario
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setPerson(person);
        user.setRoles(Set.of(role));


        // Guardar en DB
        userRepository.save(user);

        // Generar token (auto-login)
        String token = jwtService.generateToken(user);

        // Respuesta
        return AuthResponseDTO.builder()
                .token(token)
                .build();
    }


}
