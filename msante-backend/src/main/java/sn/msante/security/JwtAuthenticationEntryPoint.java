package sn.msante.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom JWT authentication entry point
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                        AuthenticationException authException) throws IOException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        String jsonBody = """
            {
                "error": "Unauthorized",
                "message": "Authentication required",
                "path": "%s",
                "timestamp": "%s"
            }
            """.formatted(request.getRequestURI(), java.time.Instant.now());
            
        response.getWriter().write(jsonBody);
    }
}