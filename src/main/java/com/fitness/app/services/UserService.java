package com.fitness.app.services;

import com.fitness.app.persistence.entities.User;
import com.fitness.app.persistence.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    @Value("${file.upload-dir2}")
    private String uploadDirectory;

    public Optional<User> getUser(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user, Long id, MultipartFile profileImage) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();
            existingUser.setUsername(user.getUsername());
            existingUser.setName(user.getName());
            existingUser.setLastname(user.getLastname());
            existingUser.setAge(user.getAge());
            existingUser.setGender(user.getGender());
            existingUser.setCountry(user.getCountry());
            if (profileImage != null) {
                try {
                    String filename = id + "-" + profileImage.getOriginalFilename();
                    existingUser.setProfilepicture(filename);
                    existingUser = userRepository.save(existingUser);
                    String imagePath = uploadDirectory +File.separator+ filename;
                    Path path = Paths.get(imagePath);
                    // Crea el directorio si no existe
                    Files.createDirectories(path.getParent());
                    Files.write(path, profileImage.getBytes());
                } catch (IOException e) {
                    log.error("Error al guardar la imagen: {}", e.getMessage());
                    // Manejar la excepción según sea necesario
                }
            }

            return userRepository.save(existingUser);
        } else {
            throw new EntityNotFoundException("Usuario no encontrado con ID: " + id);
        }
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public boolean deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return true;
        }
        return false;
    }



}
