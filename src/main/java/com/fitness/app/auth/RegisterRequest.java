package com.fitness.app.auth;


import com.fitness.app.persistence.entities.Gender;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {

     Long id;

     String name;

     String lastname;

     String username;

     String email;

     String password;

     Date age;

     Boolean coach;

     String gender;

     String country;
}
