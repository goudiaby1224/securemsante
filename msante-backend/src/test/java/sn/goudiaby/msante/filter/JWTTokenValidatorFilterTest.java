package sn.goudiaby.msante.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import sn.goudiaby.msante.constants.ApplicationConstants;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JWTTokenValidatorFilterTest {

    @Mock
    private Environment environment;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    @InjectMocks
    private JWTTokenValidatorFilter jwtTokenValidatorFilter;

    private String validJwt;
    private String secret;
    private SecretKey secretKey;

    @BeforeEach
    void setUp() {
        secret = ApplicationConstants.JWT_SECRET_DEFAULT_VALUE;
        secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        
        // Create a valid JWT for testing
        validJwt = Jwts.builder()
                .subject("test@example.com")
                .claim("username", "test@example.com")
                .claim("authorities", "ROLE_PATIENT")
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour
                .signWith(secretKey)
                .compact();

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void testDoFilterInternalWithValidJWT() throws ServletException, IOException {
        when(request.getHeader(ApplicationConstants.JWT_HEADER)).thenReturn(validJwt);
        when(environment.getProperty(ApplicationConstants.JWT_SECRET_KEY, 
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE)).thenReturn(secret);

        jwtTokenValidatorFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(securityContext).setAuthentication(any());
    }

    @Test
    void testDoFilterInternalWithNoJWT() throws ServletException, IOException {
        when(request.getHeader(ApplicationConstants.JWT_HEADER)).thenReturn(null);

        jwtTokenValidatorFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testDoFilterInternalWithInvalidJWT() throws ServletException, IOException {
        String invalidJwt = "invalid.jwt.token";
        when(request.getHeader(ApplicationConstants.JWT_HEADER)).thenReturn(invalidJwt);
        when(environment.getProperty(ApplicationConstants.JWT_SECRET_KEY, 
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE)).thenReturn(secret);

        // Should not throw exception, just filter the request without setting authentication
        assertDoesNotThrow(() -> jwtTokenValidatorFilter.doFilterInternal(request, response, filterChain));

        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testDoFilterInternalWithExpiredJWT() throws ServletException, IOException {
        // Create an expired JWT
        String expiredJwt = Jwts.builder()
                .subject("test@example.com")
                .claim("username", "test@example.com")
                .claim("authorities", "ROLE_PATIENT")
                .issuedAt(new Date(System.currentTimeMillis() - 7200000)) // 2 hours ago
                .expiration(new Date(System.currentTimeMillis() - 3600000)) // expired 1 hour ago
                .signWith(secretKey)
                .compact();

        when(request.getHeader(ApplicationConstants.JWT_HEADER)).thenReturn(expiredJwt);
        when(environment.getProperty(ApplicationConstants.JWT_SECRET_KEY, 
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE)).thenReturn(secret);

        // Should not throw exception, just filter the request without setting authentication
        assertDoesNotThrow(() -> jwtTokenValidatorFilter.doFilterInternal(request, response, filterChain));

        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testDoFilterInternalWithMalformedJWT() throws ServletException, IOException {
        String malformedJwt = "this.is.not.a.valid.jwt";
        when(request.getHeader(ApplicationConstants.JWT_HEADER)).thenReturn(malformedJwt);
        when(environment.getProperty(ApplicationConstants.JWT_SECRET_KEY, 
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE)).thenReturn(secret);

        // Should not throw exception, just filter the request without setting authentication
        assertDoesNotThrow(() -> jwtTokenValidatorFilter.doFilterInternal(request, response, filterChain));

        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }

    @Test
    void testShouldNotFilterAuthEndpoints() throws ServletException {
        when(request.getServletPath()).thenReturn("/auth/login");
        
        boolean shouldNotFilter = jwtTokenValidatorFilter.shouldNotFilter(request);
        
        assertTrue(shouldNotFilter);
    }

    @Test
    void testShouldFilterProtectedEndpoints() throws ServletException {
        when(request.getServletPath()).thenReturn("/api/appointments");
        
        boolean shouldNotFilter = jwtTokenValidatorFilter.shouldNotFilter(request);
        
        assertFalse(shouldNotFilter);
    }

    @Test
    void testDoFilterInternalWithDifferentSecret() throws ServletException, IOException {
        String differentSecret = "differentSecretKeyForTesting123456";
        when(request.getHeader(ApplicationConstants.JWT_HEADER)).thenReturn(validJwt);
        when(environment.getProperty(ApplicationConstants.JWT_SECRET_KEY, 
                ApplicationConstants.JWT_SECRET_DEFAULT_VALUE)).thenReturn(differentSecret);

        // Should not throw exception, just filter the request without setting authentication
        assertDoesNotThrow(() -> jwtTokenValidatorFilter.doFilterInternal(request, response, filterChain));

        verify(filterChain).doFilter(request, response);
        verify(securityContext, never()).setAuthentication(any());
    }
}