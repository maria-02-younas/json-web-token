package com.task.Taskjwt.controller;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.task.Taskjwt.entity.ErrorResponse;
import com.task.Taskjwt.entity.JwtRequest;
import com.task.Taskjwt.entity.JwtResponse;
import com.task.Taskjwt.security.JwtHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {
    //    To fetch user details
    @Autowired
    private UserDetailsService userDetailsService;

    //    To authenticate
    @Autowired
    private AuthenticationManager authenticationManager;

    //    To create Jwt
    @Autowired
    private JwtHelper jwtHelper;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        this.doAuthentication(request.getUsername(), request.getPassword());

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(request.getUsername());
        String token = this.jwtHelper.generateToken(userDetails);

        JwtResponse response = new JwtResponse(token, userDetails.getUsername(), jwtHelper.getExpirationDateFromToken(token));
//        response.setJwtToken(token);
//        response.setUsername(userDetails.getUsername());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private void doAuthentication(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(username, password);

        try {
            authenticationManager.authenticate(authenticationToken);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Username or Password!");
        }
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> exceptionHandler() throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();

        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        ErrorResponse errorResponse = ErrorResponse.builder().statusCode("403").message("Invalid Credentials!").build();

        return new ResponseEntity<>(
                objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(errorResponse), HttpStatus.FORBIDDEN);
    }
}
