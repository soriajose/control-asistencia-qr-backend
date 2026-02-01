package com.soriaajose.control.asistencia.qr.backend.security.jwt;

import com.soriaajose.control.asistencia.qr.backend.role.model.Role;
import com.soriaajose.control.asistencia.qr.backend.user.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class JwtService {

    // IMPORTANTE: Esta es la llave maestra. En producción, esto DEBE estar en application.properties.
    // Es una clave aleatoria de 256 bits (hexadecimal) para firmar los tokens.
    // Si alguien consigue esto, puede falsificar cualquier identidad en tu sistema.
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";

    // Extrae el nombre de usuario (subject) del token.
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // Extraer un Claim genérico
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token); // devuelve todos los claims del token
        return claimsResolver.apply(claims);
    }

    // Lee y verifica la firma del token para obtener todos los datos (Claims)
    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey()) // seteo la clave de la firma
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Decodifica nuestra clave secreta para usarla en el algoritmo de firma
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Validar Token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);

        // Acá devuelvo la comparacion entre el username del detalle y el username del token
        // y consulto si el token NO expiro
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // Verificar si el token expiró
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Extraer la fecha de expiración del token
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // Generar Token (Simplificado - Este es el que llama el AuthController)
    // Esto tambien es un ejemplo de sobrecarga de métodos
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }

    // Generar Token (Completo con Claims extra)
    public String generateToken(Map<String, Object> extraClaims, User user) {

        extraClaims.put("userId", user.getId());

        String roleNames = user.getRoles().isEmpty()
                ? "EMPLOYEE"
                : user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.joining(","));

        extraClaims.put("role", roleNames);

        return Jwts
                .builder()
                .setClaims(extraClaims) // seteo los Claims enviando los extraClaims
                .setSubject(user.getUsername()) // seteo el username
                .setIssuedAt(new Date(System.currentTimeMillis())) // seteo la fecha de creacion del token
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 horas - seteo fecha de expiracion del token
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // seteo la key y el algoritmo de la firma/key
                .compact();
    }

}
