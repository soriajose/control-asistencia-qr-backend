package com.soriaajose.control.asistencia.qr.backend.security.config;

import com.soriaajose.control.asistencia.qr.backend.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity // habilitamos la seguridad a nivel de toda la app
@EnableMethodSecurity // habilitamos la seguridad a nivel de metodos
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthenticationProvider authenticationProvider;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // esto es para la prueba local
                .authorizeHttpRequests(auth -> auth.requestMatchers("/auth/**").permitAll() // endpoint que no necesitan autenticacion
                        .anyRequest().authenticated()) // al resto, solicitar autenticacion
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // DEFINIMOS EL BEAN QUE DICE "QUIÉN PUEDE ENTRAR" - ESTO ES PARA PROBAR LOCALMENTE
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Permitir el origen de tu Frontend (Angular)
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));

        // --- CAMBIO CLAVE AQUÍ ---
        // Usamos patrones para permitir subdominios dinámicos
       // configuration.setAllowedOriginPatterns(List.of("http://localhost:4200", "http://*.localhost:4200"));

        // Permitir los métodos HTTP comunes
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

        // Permitir todos los headers (Authorization, Content-Type, etc.)
        configuration.setAllowedHeaders(List.of("*"));

        // Permitir credenciales (cookies, auth headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Aplicar esta configuración a TODAS las rutas (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
