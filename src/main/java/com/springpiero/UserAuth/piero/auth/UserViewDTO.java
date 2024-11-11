package com.springpiero.UserAuth.piero.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserViewDTO {

    private Long uid;

    private String email;

    private String authorities;

}
