package com.task.Taskjwt.security;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.Taskjwt.entity.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

// Entry point
@Component("jwtAuthentication")
public class JwtAuthentication implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        if (response.getStatus() == HttpServletResponse.SC_FORBIDDEN) {

            ObjectMapper objectMapper = new ObjectMapper();

            objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");

            ErrorResponse errorResponse = ErrorResponse.builder().statusCode("403").message("Access Denied! You are not authorized to access it.").build();

            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(errorResponse));
        }
    }
}
