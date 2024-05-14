package com.fitness.app.persistence.repositories;

import com.fitness.app.persistence.entities.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetTokenRepository  extends JpaRepository<PasswordResetToken,Long> {

    PasswordResetToken findByToken(String token);
}
