package com.springpiero.UserAuth.repositories;

import com.springpiero.UserAuth.models.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {

    List<Album> findByAccountUid(Long id);
}
