package dev.simoncodes.ledger.auth.jwt;

import dev.simoncodes.ledger.user.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtService jwtSvc;
    private final UserDetailsServiceImpl userDetailsSvc;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        try {
            UUID userId = jwtSvc.validateAccessToken(token);


            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails user = userDetailsSvc.loadUserByUsername(userId.toString());

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("JwtFilter: set auth, principal type = "
                        + authToken.getPrincipal().getClass().getName());
            }
        } catch (Exception e) {
            // Log the issue and move on.
        }
        filterChain.doFilter(request, response);
    }
}
