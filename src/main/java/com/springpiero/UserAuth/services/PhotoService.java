package com.springpiero.UserAuth.services;

import com.springpiero.UserAuth.models.Account;
import com.springpiero.UserAuth.models.Photo;
import com.springpiero.UserAuth.repositories.PhotoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PhotoService {

    @Autowired
    private PhotoRepository photoRepository;

    public Photo save(Photo photo){
        return photoRepository.save(photo);
    }

    public Optional<Photo> findById(Long id){
        return photoRepository.findById(id);
    }
}
