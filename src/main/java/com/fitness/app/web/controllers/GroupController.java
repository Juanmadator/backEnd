package com.fitness.app.web.controllers;

import com.fitness.app.persistence.entities.Group;
import com.fitness.app.persistence.entities.GroupMessage;
import com.fitness.app.persistence.entities.User;
import com.fitness.app.persistence.repositories.GroupMessageRepository;
import com.fitness.app.persistence.repositories.GroupRepository;
import com.fitness.app.persistence.repositories.UserRepository;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/groups")
public class GroupController {

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private GroupMessageRepository groupMessageRepository;

    @Autowired
    private UserRepository userRepository;
    @Value("${file.upload-dir}")
    private String uploadDirectory = "";

    public GroupController(GroupRepository groupRepository, GroupMessageRepository groupMessageRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMessageRepository = groupMessageRepository;
        this.userRepository = userRepository;
    }


    //CREAR UN GRUPO
    @PostMapping(value = "/create/{coachId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Group> createGroup(
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @PathVariable("coachId") Long coachId,
            @RequestPart(value = "profileImage", required = false) MultipartFile profileImage
    ) {
        try {
            // Verificar si se proporcionó una imagen de perfil
            String profileImageUrl = null;
            if (profileImage != null && !profileImage.isEmpty()) {
                // Verificar si el directorio de carga existe
                Path directoryPath = Paths.get(uploadDirectory);
                if (!Files.exists(directoryPath)) {
                    return ResponseEntity.badRequest().build();
                }

                // Guardar la imagen de perfil en el servidor
                String filename = coachId + "-" + profileImage.getOriginalFilename();
                Path imagePath = directoryPath.resolve(filename);
                Files.write(imagePath, profileImage.getBytes());

                // Verificar si el archivo se ha guardado correctamente
                if (Files.exists(imagePath) && Files.size(imagePath) > 0) {
                    // Si el archivo existe en el directorio y tiene un tamaño mayor que cero, establecer la URL de la imagen de perfil en el grupo
                    profileImageUrl = filename;
                } else {
                    // Si el archivo no se ha guardado correctamente, devuelve una respuesta de error
                    return ResponseEntity.badRequest().build();
                }
            }

            // Crear un nuevo objeto FitnessGroup
            Group group = new Group();
            // Establecer el nombre del grupo
            group.setName(name);
            // Establecer la descripción del grupo
            group.setDescription(description);
            // Establecer el ID del coach en el grupo
            group.setCoachId(coachId);
            // Establecer la URL de la imagen de perfil en el grupo si existe
            group.setProfileImage(profileImageUrl);

            // Guardar el grupo en la base de datos
            Group createdGroup = groupRepository.save(group);

            return ResponseEntity.ok(createdGroup);
        } catch (IOException e) {
            // Si ocurre un error de E/S al guardar el archivo, devuelve una respuesta de error
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Si ocurre un error inesperado, devuelve una respuesta de error
            return ResponseEntity.badRequest().build();
        }
    }


    //GET GROUP POR EL ID
    @GetMapping("/group/{groupId}")
    public ResponseEntity<Group> getGroupById(@PathVariable Long groupId) {
        // Buscar el grupo por su ID en la base de datos
        Optional<Group> optionalGroup = groupRepository.findById(groupId);

        // Verificar si el grupo existe
        if (optionalGroup.isPresent()) {
            // Si el grupo existe, devolverlo en la respuesta
            Group group = optionalGroup.get();
            return ResponseEntity.ok(group);
        } else {
            // Si el grupo no existe, devolver una respuesta de no encontrado
            return ResponseEntity.notFound().build();
        }
    }



    //ELIMINAR UN GRUPO
    @DeleteMapping("/{groupId}")
    public ResponseEntity<?> deleteGroup(@PathVariable Long groupId) {
        groupRepository.deleteById(groupId);
        return ResponseEntity.ok().build();
    }

    //OBTENER GRUPOS DE UN COACH
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Group>> getGroupsByUser(@PathVariable int userId) {
        List<Group> userGroups = groupRepository.findByCoachId(userId);
        if (userGroups.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(userGroups);
        }
    }

//OBTENER TODOS LOS GRUPOS
    @GetMapping("/all/groups")
    public ResponseEntity<List<Group>> getAllGroups() {
        List<Group> groups = groupRepository.findAll();
        if (groups.isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(groups);
        }
    }


    //OBTENER NOMBRES DE USUARIO DE LOS COACH QUE HAN CREADO EL GRUPO
    @GetMapping("/{groupId}")
    public ResponseEntity<Map<String, String>> getCoachUsernameByGroupId(@PathVariable int groupId) {
        Group group = groupRepository.findById(groupId);
        if (group != null) {
            Long coachId = group.getCoachId();
            User coach = userRepository.findUserById(coachId);
            if (coach != null) {
                Map<String, String> response = new HashMap<>();
                response.put("username", coach.getUsername());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }


    @PostMapping("/{groupId}/send")
    public ResponseEntity<String> sendGroupMessage(
            @PathVariable Long groupId,
            @RequestParam("senderId") Long senderId,
            @RequestParam("message") String message,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        // Verificar si el grupo existe
        Group group = groupRepository.findById(groupId).orElse(null);
        if (group == null) {
            return ResponseEntity.notFound().build();
        }

        // Verificar si el usuario remitente existe
        User sender = userRepository.findById(senderId).orElse(null);
        if (sender == null) {
            return ResponseEntity.badRequest().body("El remitente no existe");
        }

        // Guardar el archivo adjunto si se proporciona
        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                // Verificar si el directorio de carga existe
                Path directoryPath = Paths.get(uploadDirectory);
                if (!Files.exists(directoryPath)) {
                    return ResponseEntity.badRequest().build();
                }

                // Guardar el archivo en el servidor
                String filename = group.getId() + "-" + file.getOriginalFilename();
                Path filePath = directoryPath.resolve(filename);
                Files.write(filePath, file.getBytes());

                // Verificar si el archivo se ha guardado correctamente
                if (Files.exists(filePath) && Files.size(filePath) > 0) {
                    fileUrl = filename; // Guardar la URL del archivo
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo adjunto");
                }
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el archivo adjunto");
            }
        }

        // Crear el mensaje del grupo
        GroupMessage groupMessage = new GroupMessage();
        groupMessage.setGroup(group);
        groupMessage.setSender(sender);
        groupMessage.setMessage(message);
        groupMessage.setDateSent(new Date());
        groupMessage.setFileUrl(fileUrl); // Establecer la URL del archivo en el mensaje

        // Guardar el mensaje del grupo
        groupMessageRepository.save(groupMessage);

        return ResponseEntity.status(HttpStatus.CREATED).body("Mensaje enviado con éxito al grupo " + group.getName());
    }



    @Getter
    @Setter
    public static class GroupMessageRequest {
        private Long senderId;
        private String message;
    }


    @GetMapping("/group/{groupId}/messages")
    public ResponseEntity<Page<GroupMessageResponse>> getGroupMessages(
            @PathVariable Long groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Crear un objeto Pageable para la paginación
        Pageable pageable = PageRequest.of(page, size);

        // Buscar todos los mensajes del grupo por su ID de grupo con paginación
        Page<GroupMessage> groupMessagePage = groupMessageRepository.findByGroupId(groupId, pageable);

        // Convertir los mensajes del grupo en respuestas de mensajes del grupo
        Page<GroupMessageResponse> responsePage = groupMessagePage.map(message -> {
            GroupMessageResponse response = new GroupMessageResponse();
            response.setId(message.getId());
            response.setGroupId(message.getGroup().getId());
            response.setSenderId(message.getSender().getId());
            response.setMessage(message.getMessage());
            response.setDateSent(message.getDateSent());
            response.setFileUrl(message.getFileUrl());
            return response;
        });

        return ResponseEntity.ok(responsePage);
    }

    // Clase de respuesta para el cuerpo de la respuesta JSON
    @Getter
    @Setter
  public  static class GroupMessageResponse {
        private Long id;
        private Long groupId;
        private Long senderId;
        private String message;
        private String fileUrl;
        private Date dateSent;
    }


}
