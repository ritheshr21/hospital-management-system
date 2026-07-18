package com.hms.api_gateway.security;

import java.io.IOException;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.hms.api_gateway.util.HeaderMapRequestWrapper;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Validates the JWT on every request that is not a public auth endpoint.
 * On success it forwards the caller identity to downstream services via
 * X-User-Email / X-User-Role headers so they never have to re-parse the token.
 */
@Component
@Order(-1)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    private boolean isPublic(String path) {
        return path.startsWith("/auth/login")
                || path.startsWith("/auth/register")
                || path.startsWith("/actuator");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Let CORS preflight and public endpoints through untouched.
        if ("OPTIONS".equalsIgnoreCase(request.getMethod()) || isPublic(request.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            unauthorized(response, "Missing or malformed Authorization header");
            return;
        }

        try {
            Claims claims = jwtUtil.parse(header.substring(7));

            HeaderMapRequestWrapper wrapped = new HeaderMapRequestWrapper(request);
            wrapped.putHeader("X-User-Email", claims.getSubject());
            wrapped.putHeader("X-User-Role", String.valueOf(claims.get("role")));

            chain.doFilter(wrapped, response);
        } catch (Exception e) {
            unauthorized(response, "Invalid or expired token");
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write("{\"status\":401,\"error\":\"Unauthorized\",\"message\":\"" + message + "\"}");
    }
}
