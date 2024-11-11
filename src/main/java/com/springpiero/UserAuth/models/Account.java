package com.springpiero.UserAuth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long uid;

    @Column(unique = true)
    private String email;

    private String password;

    private String Authorities;

}
