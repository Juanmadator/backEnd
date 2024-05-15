package com.fitness.app.persistence.repositories;

import com.fitness.app.persistence.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface RoleRepository extends JpaRepository<Role,Long> {

    Role findByRoleName(@Param("rol") String rol);
}
