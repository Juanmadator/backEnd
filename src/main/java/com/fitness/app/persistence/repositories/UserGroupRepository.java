package com.fitness.app.persistence.repositories;

import com.fitness.app.persistence.entities.Group;
import com.fitness.app.persistence.entities.UserGroup;
import jdk.jfr.Registered;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup,Long> {
    List<UserGroup> findByUserId(Long userId);

    Long countByGroupId(Long groupId);


    void deleteByUserId(Long id);
    boolean existsByGroupIdAndUserId(Long groupId, Long userId);



    // Consulta personalizada para obtener la información completa de cada grupo por su ID
}
