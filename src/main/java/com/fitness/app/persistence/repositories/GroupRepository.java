package com.fitness.app.persistence.repositories;

import com.fitness.app.persistence.entities.Group;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.awt.print.Pageable;
import java.util.List;


@Repository
public interface GroupRepository extends JpaRepository<Group,Long> {

    void deleteById(Long id);

    Group findById(int id);


    List<Group> findByCoachId(int id);
}
