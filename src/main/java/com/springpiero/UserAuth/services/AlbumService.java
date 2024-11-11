package com.springpiero.UserAuth.services;

import com.springpiero.UserAuth.models.Album;
import com.springpiero.UserAuth.repositories.AlbumRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AlbumService {

    @Autowired
    private AlbumRepository albumRepository;

    public Album save(Album album){
        return albumRepository.save(album);
    }

    public List<Album> findByAccountId(Long id){
        return albumRepository.findByAccountUid(id);
    }

    public Optional<Album> findById(Long id){
        return albumRepository.findById(id);
    }
}
