package com.fitness.app.persistence.repositories;

import com.fitness.app.persistence.entities.Favourite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite,Long> {


   void deleteByPostIdAndUserId(int postId,int userId);
   Page<Favourite> findByUserId(int userId, Pageable pageable);

   @Query("SELECT f.postId FROM Favourite f WHERE f.userId = :userId")
   List<Integer> findPostIdsByUserId(@Param("userId") int userId);
   boolean existsByPostIdAndUserId(int postId,int userId);
   Favourite findByPostIdAndUserId(int postId,int userId);
}
