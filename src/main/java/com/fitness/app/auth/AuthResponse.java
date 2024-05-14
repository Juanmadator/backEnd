package com.fitness.app.auth;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
     String token;
     String error;


    public AuthResponse(String error) {
        this.error = error;
    }


}
