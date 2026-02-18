package com.shop.ecommerce.multivendor.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.List;

public class JwtTokenValidator extends OncePerRequestFilter {

    private final SecretKey key =
            Keys.hmacShaKeyFor(JWT_CONSTANT.SECRET_KEY.getBytes());

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();

        // ✅ Allow auth APIs without token
        if (path.startsWith("/auth")) {
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = request.getHeader(JWT_CONSTANT.JWT_HEADER);

        if (jwt != null && jwt.startsWith("Bearer ")) {
            try {
                jwt = jwt.substring(7);

                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = String.valueOf(claims.get("email"));

                String authorities = String.valueOf(claims.get("authorities"));

                List grantedAuthorities =
                        AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);

                Authentication authentication =
                        new UsernamePasswordAuthenticationToken(
                                email,
                                null,
                                grantedAuthorities
                        );

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                throw new RuntimeException("Invalid JWT Token");
            }
        }

        filterChain.doFilter(request, response);
    }
}