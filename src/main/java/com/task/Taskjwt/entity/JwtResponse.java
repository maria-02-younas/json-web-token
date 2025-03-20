package com.task.Taskjwt.entity;

import java.util.Date;

public class JwtResponse {
    private String jwtToken;
    private String username;
    private Date expiryDate;

    public String getJwtToken() {
        return jwtToken;
    }

    public void setJwtToken(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public JwtResponse() {
    }

    public JwtResponse(String jwtToken, String username, Date expiryDate) {
        this.jwtToken = jwtToken;
        this.username = username;
        this.expiryDate = expiryDate;
    }

    @Override
    public String toString() {
        return "JwtResponse{" +
                "jwtToken='" + jwtToken + '\'' +
                ", username='" + username + '\'' +
                ", expiryDate=" + expiryDate +
                '}';
    }
}
