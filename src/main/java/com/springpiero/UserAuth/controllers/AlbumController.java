package com.springpiero.UserAuth.controllers;

import com.springpiero.UserAuth.models.Album;
import com.springpiero.UserAuth.models.Account;
import com.springpiero.UserAuth.models.Photo;
import com.springpiero.UserAuth.piero.album.AlbumDTO;
import com.springpiero.UserAuth.piero.album.AlbumViewDTO;
import com.springpiero.UserAuth.services.AlbumService;
import com.springpiero.UserAuth.services.PhotoService;
import com.springpiero.UserAuth.services.UsersService;
import com.springpiero.UserAuth.utils.AppUtils.AppUtil;
import com.springpiero.UserAuth.utils.constants.AlbumError;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Album Controller", description = "Controller for album and photo management")
@Slf4j
public class AlbumController {

    static  final String PHOTOS_FOLDER_NAME = "photos";
    static  final String THUMBNAIL_FOLDER_NAME = "thumbnails";
    static  final int THUMBNAIL_WIDTH = 300;
    @Autowired
    private UsersService usersService;

    @Autowired
    private AlbumService albumService;

    @Autowired
    private PhotoService photoService;

    @PostMapping(value = "/albums/add", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "200", description = "Album added")
    @ApiResponse(responseCode = "400", description = "Please, enter a valid description")
    @Operation(summary = "Add a new album")
    @SecurityRequirement(name = "app-secure-scheme")
    public ResponseEntity<AlbumViewDTO> addAlbum(@Valid @RequestBody AlbumDTO albumDTO, Authentication authentication){

        try {
            Album album = new Album();
            album.setName(albumDTO.getName());
            album.setDescription(albumDTO.getDescription());
            String email = authentication.getName();
            Optional<Account> optionalUser = usersService.findByEmail(email);
            Account owner = optionalUser.get();
            album.setAccount(owner);
            album = albumService.save(album);
            AlbumViewDTO albumViewDTO = new AlbumViewDTO(album.getId(), albumDTO.getName(), album.getDescription());
            return ResponseEntity.ok(albumViewDTO);
        }catch (Exception e){
            log.debug(AlbumError.ADD_ALBUM_ERROR.toString() + " " + e.getMessage() );
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @GetMapping(value = "/albums", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "List of Album")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "List of album")
    @SecurityRequirement(name = "app-secure-scheme")
    public List<AlbumViewDTO> albums(Authentication authentication){

        String email = authentication.getName();
        Optional<Account> optionalUser = usersService.findByEmail(email);
        Account owner = optionalUser.get();

        List<AlbumViewDTO> albums = new ArrayList<>();
        for(Album album: albumService.findByAccountId(owner.getUid())){
            albums.add(new AlbumViewDTO(album.getId(), album.getName(), album.getDescription()));
        }

        return albums;
    }

    @PostMapping(value = "/albums/{album_id}/upload-photos", consumes = {"multipart/form-data"})
    @ApiResponse(responseCode = "400", description = "Please check the paylod or token")
    @Operation(summary = "Uplaod photo into album")
    @SecurityRequirement(name = "app-secure-scheme")
    public ResponseEntity<List<HashMap<String, List<String>>>> photos(@RequestPart(required = true) MultipartFile[] files,
                                                                      @PathVariable Long album_id, Authentication authentication){

        String email = authentication.getName();
        Optional<Account> optionalAccount = usersService.findByEmail(email);
        Account account = optionalAccount.get();
        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;
        if(optionalAlbum.isPresent()){
            album = optionalAlbum.get();
            if(account.getUid() != album.getAccount().getUid()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        List<String> fileNamesWithSuccess = new ArrayList<>();
        List<String> fileNamesWithError = new ArrayList<>();

        Arrays.asList(files).stream().forEach(file -> {
            String contentType = file.getContentType();
            if(contentType.equals("image/png")
              || contentType.equals("image/jpg")
              || contentType.equals("image/jpeg")
            ){
                fileNamesWithSuccess.add(file.getOriginalFilename());

                int lenght = 10;
                boolean useLetters = true;
                boolean useNumber = true;

                try{
                    String fileName = file.getOriginalFilename();
                    String generatedString = RandomStringUtils.random(lenght,useLetters,useNumber);
                    String final_photo_name = generatedString+fileName;

                    String absoluteFileLocation = AppUtil.get_photo_upload_path(final_photo_name, PHOTOS_FOLDER_NAME, album_id);
                    Path path = Paths.get(absoluteFileLocation);
                    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                    Photo photo = new Photo();
                    photo.setName(fileName);
                    photo.setFileName(final_photo_name);
                    photo.setOriginalFileName(fileName);
                    photo.setAlbum(album);
                    photoService.save(photo);

                    BufferedImage thumbImg = AppUtil.getThumbnail(file, THUMBNAIL_WIDTH);
                    File thumbnail_location = new File(AppUtil.get_photo_upload_path(final_photo_name,THUMBNAIL_FOLDER_NAME,album_id));
                    ImageIO.write(thumbImg, file.getContentType().split("/")[1],thumbnail_location);

                }catch (Exception e){
                    log.debug(AlbumError.PHOTO_UPLOAD_ERROR+": "+e.toString());
                    fileNamesWithError.add(file.getOriginalFilename());
                }
            }else {
                fileNamesWithError.add(file.getOriginalFilename());
            }

        });

        HashMap<String, List<String>> result = new HashMap<>();
        result.put("SUCCESS", fileNamesWithSuccess);
        result.put("ERROR",fileNamesWithError);

        List<HashMap<String, List<String>>> response = new ArrayList<>();
        response.add(result);

        return ResponseEntity.ok(response);
    }

    @GetMapping("albums/{album_id}/photos/{photo_id}/download-photo")
    @SecurityRequirement(name = "app-secure-scheme")
    public ResponseEntity<?> downloadPhoto(@PathVariable("album_id") Long album_id,
                                           @PathVariable("photo_id") Long photo_id, Authentication authentication){

        Optional<Account> optionalAccount = usersService.findByEmail(authentication.getName());
        Account account = optionalAccount.get();
        Optional<Album> optionalAlbum = albumService.findById(album_id);
        Album album;

        if(optionalAlbum.isPresent()){
            album = optionalAlbum.get();
            if (account.getUid() != album.getAccount().getUid()){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }
        }else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        Optional<Photo> optionalPhoto = photoService.findById(photo_id);

        if(optionalPhoto.isPresent()){
            Photo photo = optionalPhoto.get();
            Resource resource = null;

            try {
                resource = AppUtil.getFileAsResource(album_id,PHOTOS_FOLDER_NAME,photo.getFileName());
            }catch (Exception e){
                return ResponseEntity.internalServerError().build();
            }

            if(resource == null){
                return new ResponseEntity<>("File not find", HttpStatus.NOT_FOUND);
            }
            String contentType = "application/octet-stream";
            String headerValue = "attachment; filename=\""+ photo.getOriginalFileName()+"\"";

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, headerValue)
                    .body(resource);
        }else {
            return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

}
