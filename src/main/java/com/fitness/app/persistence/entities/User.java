package com.fitness.app.persistence.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

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
    private String username;

    @Email
    @NotNull
    @Column(name = "email")
    private String email;

    @Column(name = "password")
    private String password;

    // @NotNull
    @Column(name = "age")
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
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
