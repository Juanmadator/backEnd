package com.fitness.app.persistence.repositories;

import com.fitness.app.persistence.entities.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<VerificationToken,Long> {

    VerificationToken findByToken(String token);

    void deleteByUserId(Long id);
}
