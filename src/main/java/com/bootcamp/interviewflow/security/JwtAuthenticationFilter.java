package com.bootcamp.interviewflow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String path = request.getRequestURI();
        String method = request.getMethod();

        log.info("JWT Filter - Processing request: {} {}", method, path);

        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            log.info("JWT Filter - Token found in header");
        } else {
            log.info("JWT Filter - No token found in Authorization header");
        }

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                if (jwtUtil.validateToken(token)) {
                    // Extract user information from token
                    String email = jwtUtil.getEmailFromToken(token);
                    Long userId = jwtUtil.getUserIdFromToken(token);

                    log.info("JWT Filter - Valid token for user: {} (ID: {})", email, userId);

                    request.setAttribute("email", email);
                    request.setAttribute("userId", userId);

                    UserPrincipal userPrincipal = new UserPrincipal(
                            userId,
                            email,
                            null, // password not needed for JWT authentication
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
                    );

                    // Create authentication token for Spring Security with UserPrincipal
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userPrincipal, // <- Now using UserPrincipal object
                            null,
                            userPrincipal.getAuthorities()
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);

                    log.info("JWT Filter - Authentication set in security context for user: {}", email);
                } else {
                    log.warn("JWT Filter - Invalid token for endpoint: {}", path);
                }
            } catch (Exception e) {
                log.error("JWT Filter - Error validating token", e);
            }
        }

        filterChain.doFilter(request, response);
    }
}