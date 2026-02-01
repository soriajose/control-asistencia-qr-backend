package com.soriaajose.control.asistencia.qr.backend.security.filter;

import com.soriaajose.control.asistencia.qr.backend.security.jwt.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization"); //es el header de autorizacion que se envia en cada peticion
        final String jwt;
        final String username;

        //
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // si no viene el authHeader, va al filtro y arroja error
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7); // Serían 7 posiciones + 1 espacio - Ej: "Bearer "
        username = jwtService.extractUsername(jwt); // obtengo el usuario


        // valido si el usuario es nulo y el contexto de seguridad es nulo
        // esto es para saber si el usuario ya esta autenticado
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username); // Consulta en la BD si el usuario existe

            if (jwtService.isTokenValid(jwt, userDetails)) { // valido si el token de ese usuario es valido/autentico y no ha expirado

                // si es valido, creo un objeto UsernamePasswordAuthenticationToken y le paso:
                // el detalle del usuario, las credenciales en null y las Authorities (los persmisos que el usuario tiene)
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                // Creamos los detalles de la peticion. Se especifica que los detalles vienen desde el request
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Actualizamos el contexto de seguridad (contextHolder)
                // Le pasamos el objeto que tiene el token, los detalles del usuario y sus permisos
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }

        // sigue el procesamiento del request
        filterChain.doFilter(request, response);


    }
}
