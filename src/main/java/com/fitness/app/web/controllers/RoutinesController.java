package com.fitness.app.web.controllers;

import com.fitness.app.persistence.entities.Routine;
import com.fitness.app.persistence.repositories.RoutineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value="/api")
@RequiredArgsConstructor

public class RoutinesController {

    private final RoutineRepository routineRepository;

    @PostMapping(value = "/crear/{userId}", consumes = {"multipart/form-data"})
    public ResponseEntity<Routine> createRoutine(
            @PathVariable("userId") Long userId,
            @RequestParam("name") String name,
            @RequestParam("description") String description,
            @RequestParam("repeticiones") int repeticiones,
            @RequestParam("peso") int peso,
            @RequestParam("routineId") String routineId,
            @RequestParam(value = "tiempo", required = false, defaultValue = "0") int tiempo,
            @RequestParam(value = "kilometros", required = false, defaultValue = "0") int kilometros,
            @RequestParam(value = "grupoMuscular", required = false) String grupoMuscular
    ) {
        try {
            // Crear un nuevo objeto Routine sin usar el builder
            Routine routine = new Routine();
            routine.setUserId(userId);
            routine.setRoutine(routineId);
            routine.setName(name);
            routine.setDescription(description);
            routine.setRepeticiones(repeticiones);
            routine.setPeso(peso);
            routine.setTiempo(tiempo);
            routine.setKilometros(kilometros);
            routine.setGrupoMuscular(grupoMuscular);
            routine.setDateCreated(LocalDateTime.now());

            // Guardar la rutina en la base de datos
            routineRepository.save(routine);

            return ResponseEntity.ok(routine);
        } catch (Exception e) {
            // Si ocurre un error inesperado, devuelve una respuesta de error
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/ejercicios/{userId}")
    public ResponseEntity<List<Routine>> getRoutinesByUserIdOrderedByMuscleGroup(@PathVariable Long userId) {
        try {
            List<Routine> routines = routineRepository.findByUserIdOrderByGrupoMuscular(userId);
            return ResponseEntity.ok(routines);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }


    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteRoutine(@PathVariable Long id) {
        if (routineRepository.existsById(id)) {
            routineRepository.deleteById(id);
            return ResponseEntity.ok("Routine with id " + id + " deleted successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Routine with id " + id + " not found");
        }
    }
}
