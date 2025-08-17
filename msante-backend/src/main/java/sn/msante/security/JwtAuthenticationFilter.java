package sn.msante.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * JWT authentication filter based on reference project patterns
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Value("${msante.security.jwt.secret}")
    private String jwtSecret;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                   FilterChain filterChain) throws ServletException, IOException {
        
        String jwt = getJwtFromRequest(request);
        
        if (jwt != null && validateToken(jwt)) {
            try {
                Claims claims = getClaims(jwt);
                String userId = claims.get("user_id", String.class);
                String role = claims.get("role", String.class);
                String tenantId = claims.get("tenant_id", String.class);
                
                List<SimpleGrantedAuthority> authorities = Arrays.asList(
                    new SimpleGrantedAuthority("ROLE_" + role)
                );
                
                MSantePrincipal principal = new MSantePrincipal(userId, role, tenantId);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(principal, null, authorities);
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                
            } catch (Exception e) {
                logger.warn("Invalid JWT token: " + e.getMessage());
                SecurityContextHolder.clearContext();
            }
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    
    private boolean validateToken(String token) {
        try {
            getClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    private Claims getClaims(String token) {
        SecretKey key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/api/auth/") || 
               path.startsWith("/health") ||
               path.startsWith("/actuator/") ||
               path.startsWith("/api-docs") ||
               path.startsWith("/swagger-ui") ||
               (path.startsWith("/api/practitioners/search") && "GET".equals(request.getMethod())) ||
               (path.startsWith("/api/availability/search") && "GET".equals(request.getMethod())) ||
               (path.equals("/api/bookings") && "POST".equals(request.getMethod()));
    }
}