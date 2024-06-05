package com.fitness.app.persistence.repositories;


import com.fitness.app.persistence.entities.GroupMessage;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GroupMessageRepository extends JpaRepository<GroupMessage,Long> {


    List<GroupMessage> findByGroupId(Long id);
}
