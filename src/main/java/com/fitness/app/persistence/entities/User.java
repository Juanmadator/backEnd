package com.fitness.app.persistence.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "last_name")
    // @NotNull
    private String lastname;

    @Column(name = "user_name")
    @NotNull
    @Size(min = 4, max = 20)
    //  @Pattern(regexp = "^[A-Z][a-zA-Z0-9._!@#$%^&*()+=-]*$", message = "El nickname debe comenzar con mayúscula y contener solo letras, números y los siguientes caracteres especiales: . _ ! @ # $ % ^ & * ( ) + = -")
    private String username;

    @Email
    @NotNull
    @Column(name = "email")
    private String email;

    @Column(name = "password")
   // @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!_.-])(?=\\S+$).{8,20}$", message = "La contraseña debe contener al menos una letra minúscula, una letra mayúscula, un número, un carácter especial y no debe contener espacios en blanco")
    private String password;

    // @NotNull
    @Column(name = "age")
    @NotNull
    private Date age;

    @Column(name = "gender")
    // @NotNull
    private String gender;

    @Column(name = "country")
    private String country;

    @Column(name = "coach")
    private Boolean coach;


    @Enumerated(EnumType.STRING)
    private UserRole role;

    private Boolean verified;

    @Column(name = "profile_picture")
    private String profilepicture;

    // Agregar método para cargar explícitamente los roles


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
