package com.springpiero.UserAuth.controllers;

import com.springpiero.UserAuth.models.Account;
import com.springpiero.UserAuth.piero.auth.*;
import com.springpiero.UserAuth.services.TokenService;
import com.springpiero.UserAuth.services.UsersService;
import com.springpiero.UserAuth.utils.constants.AccountError;
import com.springpiero.UserAuth.utils.constants.AccountSuccess;
import com.springpiero.UserAuth.utils.constants.Authority;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth Controller", description = "Controller for Account management")
@Slf4j
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    private UsersService userService;

    public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
    }

    //for this function we need class user & token
    @PostMapping("/token")
    @ResponseBody
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<TokenDTO> token(@RequestBody UserLoginDTO userLogin) {

        try {
            Authentication authentication = authenticationManager
                    .authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
            return ResponseEntity.ok(new TokenDTO(tokenService.generateToken(authentication)));

        } catch (Exception e) {
            log.debug(AccountError.TOKEN_GENERATION_ERROR.toString() + ": " + e.getMessage());
            return new ResponseEntity<>(new TokenDTO(null), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/users/add", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponse(responseCode = "200", description = "Account added")
    @ApiResponse(responseCode = "400", description = "Please, enter a valid email or password")
    @Operation(summary = "Add a new user")
    public ResponseEntity<String> addUser(@Valid @RequestBody AccountDTO accountDTO) {

        try {
            Account newUser = new Account();
            newUser.setEmail(accountDTO.getEmail());
            newUser.setPassword(accountDTO.getPassword());
            newUser.setAuthorities(Authority.USER.toString());
            userService.save(newUser);
            return ResponseEntity.ok(AccountSuccess.ACCOUNT_ADDED.toString());

        } catch (Exception e) {
            log.debug(AccountError.ADD_ACCOUNT_ERROR.toString() + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping(value = "/users")
    @ApiResponse(responseCode = "200", description = "List of all users")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "List of users")
    @SecurityRequirement(name = "app-secure-scheme")
    public List<UserViewDTO> users() {

        List<UserViewDTO> users = new ArrayList<>();
        for (Account user : userService.findAll()) {
            users.add(new UserViewDTO(user.getUid(), user.getEmail(), user.getAuthorities()));
        }
        return users;
    }

    @PutMapping(value = "/users/{user_id}/update-authorities", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Update authorities")
    @ApiResponse(responseCode = "400", description = "Invalid user")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "Update authorities")
    @SecurityRequirement(name = "app-secure-scheme")
    public ResponseEntity<UserViewDTO> updatePassword(@Valid @RequestBody AuthoritiesDTO authoritiesDTO,
                                                      @PathVariable long user_id) {

        Optional<Account> optionalUser = userService.findById(user_id);

        if (optionalUser.isPresent()) {
            Account a_user = optionalUser.get();
            a_user.setAuthorities(authoritiesDTO.getAuthorities());
            userService.save(a_user);
            UserViewDTO userViewDTO = new UserViewDTO(a_user.getUid(), a_user.getEmail(), a_user.getAuthorities());
            return ResponseEntity.ok(userViewDTO);
        }
        return new ResponseEntity<UserViewDTO>(new UserViewDTO(), HttpStatus.BAD_REQUEST);
    }

    @GetMapping(value = "/profile", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Connected user information")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "User information")
    @SecurityRequirement(name = "app-secure-scheme")
    public ProfileDTO profile(Authentication authentication) {

        String email = authentication.getName();
        Optional<Account> optionalUsers = userService.findByEmail(email);

        Account a_user = optionalUsers.get();
        ProfileDTO profileDTO = new ProfileDTO(a_user.getUid(), a_user.getEmail(), a_user.getAuthorities());
        return profileDTO;
    }

    @PutMapping(value = "/profile/update-password", produces = "application/json")
    @ApiResponse(responseCode = "200", description = "Password changed")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "Update password")
    @SecurityRequirement(name = "app-secure-scheme")
    public UserViewDTO updatePassword(@Valid @RequestBody PasswordDTO passwordDTO, Authentication authentication) {

        String email = authentication.getName();
        Optional<Account> optionalUser = userService.findByEmail(email);

        Account a_user = optionalUser.get();
        a_user.setPassword(passwordDTO.getPassword());
        userService.save(a_user);
        UserViewDTO userViewDTO = new UserViewDTO(a_user.getUid(), a_user.getEmail(), a_user.getAuthorities());
        return userViewDTO;
    }

    @DeleteMapping(value = "/users/{user_id}/delete-user")
    @ApiResponse(responseCode = "200", description = "User deleted")
    @ApiResponse(responseCode = "401", description = "Token missing")
    @ApiResponse(responseCode = "403", description = "Token error")
    @Operation(summary = "Delete user by id")
    @SecurityRequirement(name = "app-secure-scheme")
    public ResponseEntity<String> deletePassword(@PathVariable Long user_id) {

        Optional<Account> optionalUser = userService.findById(user_id);

        if(optionalUser.isPresent()){
            userService.deleteById(user_id);
            return ResponseEntity.ok("User deleted");
        }

        return new ResponseEntity<String>("Bad request", HttpStatus.BAD_REQUEST);
    }

}
