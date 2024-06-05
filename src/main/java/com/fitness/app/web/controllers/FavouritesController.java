package com.fitness.app.web.controllers;


import com.fitness.app.persistence.entities.Favourite;
import com.fitness.app.persistence.repositories.FavouriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class FavouritesController {

private final FavouriteRepository favouriteRepository;
    @PostMapping("/favourites/fav")
    public ResponseEntity<Favourite> addOrRemoveFavourite(@RequestBody Favourite favorito) {
        try {
            Favourite existingFavourite = favouriteRepository.findByPostIdAndUserId(favorito.getPostId(), favorito.getUserId());

            if (existingFavourite!=null) {
                // Si ya existe un favorito, eliminarlo
                favouriteRepository.delete(existingFavourite);
                return ResponseEntity.ok().build(); // Devuelve una respuesta OK sin contenido
            } else {
                // Si no existe un favorito, crear uno nuevo
                Favourite favourite = new Favourite();
                favourite.setPostId(favorito.getPostId());
                favourite.setUserId(favorito.getUserId());
                favourite.setDateLiked(LocalDateTime.now());
                favouriteRepository.save(favourite);
                return ResponseEntity.ok(favourite);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
