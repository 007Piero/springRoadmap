package com.springpiero.UserAuth.configs;

import com.springpiero.UserAuth.models.Account;
import com.springpiero.UserAuth.services.UsersService;
import com.springpiero.UserAuth.utils.constants.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner {

    @Autowired
    private UsersService usersService;

    @Override
    public void run(String... args) throws Exception {

        Account user1 = new Account();
        Account user2 = new Account();
        Account user3 = new Account();

        user1.setEmail("admin@medael.ca");
        user1.setPassword("123456");
        user1.setAuthorities(Authority.ADMIN.toString() + " " + Authority.USER.toString());
        usersService.save(user1);

        user2.setEmail("piero@medael.ca");
        user2.setPassword("123456");
        user2.setAuthorities(Authority.USER.toString());
        usersService.save(user2);

        user3.setEmail("manue@medael.ca");
        user3.setPassword("123456");
        user3.setAuthorities(Authority.USER.toString());
        usersService.save(user3);

    }
}
