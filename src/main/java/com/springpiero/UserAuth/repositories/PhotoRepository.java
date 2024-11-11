package com.springpiero.UserAuth.repositories;

import com.springpiero.UserAuth.models.Photo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {

    List<Photo> findByid(Long id);
}
