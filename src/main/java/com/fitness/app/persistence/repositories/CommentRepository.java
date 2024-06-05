package com.fitness.app.persistence.repositories;

import com.fitness.app.persistence.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByPostId(Long postId);
}
