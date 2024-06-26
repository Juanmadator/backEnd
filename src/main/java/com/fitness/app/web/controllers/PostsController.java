package com.fitness.app.web.controllers;


import com.fitness.app.persistence.entities.Comment;
import com.fitness.app.persistence.entities.Favourite;
import com.fitness.app.persistence.entities.Post;
import com.fitness.app.persistence.repositories.CommentRepository;
import com.fitness.app.persistence.repositories.FavouriteRepository;
import com.fitness.app.persistence.repositories.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class PostsController {

    private final PostRepository postRepository;
    private final FavouriteRepository favouriteRepository;
    private final CommentRepository commentRepository;

    @Value("${file.upload-dir}")
    private String uploadDirectory = "";

    @GetMapping("/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        Path imagePath = Paths.get(uploadDirectory).resolve(filename);
        Resource resource;
        try {
            resource = new UrlResource(imagePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.IMAGE_JPEG)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping(value = "/post/create/{id}", consumes = {"multipart/form-data"})
    public ResponseEntity<Post> createPost(
            @RequestParam("content") String content,
            @PathVariable("id") Long userId,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) {
        try {
            // Verificar si se proporcionó una imagen
            String imageUrl = null;
            if (image != null && !image.isEmpty()) {
                // Verificar si el directorio de carga existe
                Path directoryPath = Paths.get(uploadDirectory);
                if (!Files.exists(directoryPath)) {
                    Files.createDirectories(directoryPath);
                }

                // Guardar la imagen en el servidor
                String filename = userId + "-" + image.getOriginalFilename();
                Path imagePath = directoryPath.resolve(filename);

                // Guardar la imagen en el servidor sin optimización
                Files.copy(image.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);

                // Verificar si el archivo se ha guardado correctamente
                if (Files.exists(imagePath) && Files.size(imagePath) > 0) {
                    // Si el archivo existe en el directorio y tiene un tamaño mayor que cero, establecer la URL de la imagen en el post
                    imageUrl = filename;
                } else {
                    // Si el archivo no se ha guardado correctamente, devuelve una respuesta de error
                    return ResponseEntity.badRequest().build();
                }
            }

            // Crear un nuevo objeto Post
            Post post = new Post();
            // Establecer la fecha de publicación del post
            post.setDatePosted(LocalDateTime.now());
            // Establecer el ID del usuario en el post
            post.setUserId(userId);
            // Establecer el contenido del post
            post.setContent(content);
            // Establecer la URL de la imagen en el post si existe
            post.setImageUrl(imageUrl);

            // Guardar el post en la base de datos
            postRepository.save(post);

            return ResponseEntity.ok(post);
        } catch (IOException e) {
            // Si ocurre un error de E/S al guardar el archivo, devuelve una respuesta de error
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Si ocurre un error inesperado, devuelve una respuesta de error
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/posts/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable("postId") Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            postRepository.deleteById(postId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }



    @GetMapping("/posts")
    public ResponseEntity<List<Post>> getAllPosts(
            @RequestParam("page") int page,
            @RequestParam("pageSize") int pageSize
    ) {
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("datePosted").descending());
        Page<Post> postPage = postRepository.findAll(pageable);

        if (postPage.isEmpty()) {
            // Manejar caso cuando no hay posts disponibles
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(postPage.getContent());
    }

    @DeleteMapping("/posts/{postId}/{userId}")
    public ResponseEntity<Void> deletePostByUserIdAndPostId(
            @PathVariable("userId") Long userId,
            @PathVariable("postId") Long postId
    ) {
        // Verificar si el post pertenece al usuario especificado
        Optional<Post> optionalPost = postRepository.findById(postId);
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (post.getUserId().equals(userId)) {
                postRepository.deleteById(postId);
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                // El post no pertenece al usuario especificado
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
        } else {
            // No se encontró el post con el postId especificado
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("/{userId}/favourite/{postId}")
    public ResponseEntity<Boolean> checkFavourite(@PathVariable int postId, @PathVariable int userId) {
        boolean isFavourite = favouriteRepository.existsByPostIdAndUserId(postId, userId);
        return ResponseEntity.ok(isFavourite);
    }


    @GetMapping("/posts/favourites/{userId}")
    public ResponseEntity<List<Integer>> getFavouritePosts(@PathVariable int userId) {
        List<Integer> favouritePostsIds = favouriteRepository.findPostIdsByUserId(userId);
        return ResponseEntity.ok(favouritePostsIds);
    }


    @GetMapping("/favourites/{userId}")
    public ResponseEntity<List<Post>> getFavouritePostsByUserId(
            @PathVariable int userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Favourite> favouritePostsPage = favouriteRepository.findByUserId(userId, pageable);

            List<Post> posts = favouritePostsPage.getContent().stream()
                    .map(favourite -> postRepository.findById((long) favourite.getPostId()).orElse(null))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(posts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @Transactional
    @DeleteMapping("/favourites/{userId}/{postId}")
    public ResponseEntity<Void> deleteFavourite(
            @PathVariable int userId,
            @PathVariable int postId
    ) {
        try {
            // Verificar si el favorito existe
            if (!favouriteRepository.existsByPostIdAndUserId(postId, userId)) {
                return ResponseEntity.notFound().build();
            }
            // Eliminar el favorito
            favouriteRepository.deleteByPostIdAndUserId(postId, userId);

            return ResponseEntity.ok().build(); // Devolver ResponseEntity<Void>
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    //COMENTARIOS
    //CREAR UN COMENTARIO
    @PostMapping("/comment")
    public ResponseEntity<Comment> createComment(@RequestBody Comment comment) {
        Comment createdComment = commentRepository.save(comment);
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    //COMMENTS DE UN PSOT
    @GetMapping("/posts/comments/{postId}")
    public ResponseEntity<List<Comment>> getAllCommentsByPostId(@PathVariable("postId") Long postId) {
        List<Comment> comments = commentRepository.findByPostId(postId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }


    //ELIMINAR UN COMENTARIO
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable("commentId") Long commentId) {
        if (commentRepository.existsById(commentId)) {
            commentRepository.deleteById(commentId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);


        }
    }

}
