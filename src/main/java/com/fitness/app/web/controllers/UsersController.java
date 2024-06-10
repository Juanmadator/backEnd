package com.fitness.app.web.controllers;


import com.fitness.app.persistence.entities.User;
import com.fitness.app.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/users")
public class UsersController {
    private final UserService userService;

    @GetMapping(value = "{id}")
    public ResponseEntity<Optional<User>> getUser(@PathVariable Long id) {
        Optional<User> user = userService.getUser(id);
        return ResponseEntity.ok(user);
    }


    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<User> updateUser(
            @RequestParam("username") String username,
            @RequestParam("name") String name,
            @RequestParam("lastname") String lastname,
            @RequestParam("age") @DateTimeFormat(pattern = "yyyy-MM-dd") Date age,
            @RequestParam("gender") String gender,
            @RequestParam("country") String country,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage,
            @PathVariable Long id
    ) {
        Optional<User> optionalUser = userService.getUser(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            // Actualizar solo los campos necesarios
            existingUser.setUsername(username);
            existingUser.setName(name);
            existingUser.setLastname(lastname);
            existingUser.setAge(age);
            existingUser.setGender(gender);
            existingUser.setCountry(country);
            // Actualizar la imagen de perfil si se proporciona una nueva imagen
            // Si no se proporciona una nueva imagen, actualizar el usuario sin cambiar la imagen de perfil
            userService.updateUser(existingUser, id, profileImage);
            return ResponseEntity.ok(existingUser);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        boolean isRemoved = userService.deleteUser(id);
        if (!isRemoved) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<User> users = userService.getAllUsers(PageRequest.of(page, size));
        return ResponseEntity.ok(users);
    }


}
