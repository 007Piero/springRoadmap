package com.springpiero.UserAuth.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    private String name;

    private String description;

    @ManyToOne
    @JoinColumn(name = "account_uid", referencedColumnName = "uid", nullable = false)
    private Account account;
}
