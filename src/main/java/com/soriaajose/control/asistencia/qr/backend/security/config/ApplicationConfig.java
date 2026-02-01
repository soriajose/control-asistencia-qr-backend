package com.soriaajose.control.asistencia.qr.backend.security.config;

import com.soriaajose.control.asistencia.qr.backend.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserRepository userRepository;


    // Esto se llama desde el JwtAuthenticationFilter
    // Se agrega el @Bean para que se puede usar en toda la app
    // Gracias a este metodo el this.userDetailsService.loadUserByUsername(username) utiliza esto para buscar el username;
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario no existe"));
    }

    // 2. BEAN: AuthenticationProvider
    // Es el encargado de verificar las credenciales
    @Bean
    public AuthenticationProvider authenticationProvider() {
        // Usamos DaoAuthenticationProvider, que es la implementación estándar
        // Le paso el userDetailService en el constructor
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        // Le decimos: "Usa este encoder para verificar contraseñas"
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    // 3. BEAN: AuthenticationManager
    // Este lo usaremos en el AuthController para el login
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // 4. BEAN: PasswordEncoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}
