package com.fitness.app.persistence.repositories;

import com.fitness.app.persistence.entities.Post;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PostRepositoryPagination extends PagingAndSortingRepository<Post,Long> {

}
