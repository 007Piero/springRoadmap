package com.springpiero.UserAuth.controllers;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home(){
        return "API V1.1 Auth";
    }


    @GetMapping("/authentication")
    @SecurityRequirement(name = "app-secure-scheme")
    public String auth(){
        return "Well, you're now authenticated";
    }
}
