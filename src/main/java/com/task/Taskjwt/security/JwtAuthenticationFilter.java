package com.task.Taskjwt.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.Taskjwt.entity.ErrorResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(OncePerRequestFilter.class);

    @Autowired
    private JwtHelper jwtHelper;

    @Autowired
    private UserDetailsService userDetailsService;

    private final RequestMatcher ignoredPaths = new AntPathRequestMatcher("/auth/login");

    @Override
        protected void doFilterInternal(HttpServletRequest request,
                        HttpServletResponse response, FilterChain filterChain)
                                            throws ServletException, IOException {

        if (this.ignoredPaths.matches(request)) {
            filterChain.doFilter(request, response);

            return;
        }

//        Authorization
        String requestHeader = request.getHeader("Authorization");
        logger.info("Header : {}", requestHeader);
        String username = null;
        String token = null;

//        Bearer djnhfiuehiu
        if (requestHeader != null && requestHeader.startsWith("Bearer")) {
            token = requestHeader.substring(7);
            try {
                username = this.jwtHelper.getUsernameFromToken(token);
            } catch (ExpiredJwtException e) {
                logger.info("Given jwt token is expired");

                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                PrintWriter writer = response.getWriter();
                writer.write(objectMapper.writeValueAsString(
                        new ErrorResponse("401", "Given jwt token is expired")));

//                e.printStackTrace();
            } catch (MalformedJwtException e) {
                logger.info("Some changed has done in token! Invalid token");

                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                PrintWriter writer = response.getWriter();
                writer.write(objectMapper.writeValueAsString(
                        new ErrorResponse("401", "Some changed has done in token! Invalid token")));

//                e.printStackTrace();
            } catch (Exception e) {
                logger.info("Jwt Signature exception");

                ObjectMapper objectMapper = new ObjectMapper();

                objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");

                PrintWriter writer = response.getWriter();
                writer.write(objectMapper.writeValueAsString(
                        new ErrorResponse("401", "Jwt Signature exception")));

//                e.printStackTrace();
            }
        } else {
            logger.info("No token found! Unauthorized access");

            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(
                    new ErrorResponse("401", "No token found! Unauthorized access")));

        }

        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);
            Boolean validateToken = this.jwtHelper.validateToken(token, userDetails);

            if (validateToken) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            } else {
                logger.info("Validation fails!");
            }
        }
        filterChain.doFilter(request, response);
    }
}
