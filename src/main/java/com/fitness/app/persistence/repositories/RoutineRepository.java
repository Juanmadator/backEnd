package com.fitness.app.persistence.repositories;


import com.fitness.app.persistence.entities.Routine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface RoutineRepository extends JpaRepository<Routine,Long> {

    List<Routine> findByUserIdOrderByGrupoMuscular(Long userId);
}
