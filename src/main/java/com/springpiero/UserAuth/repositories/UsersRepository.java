package com.springpiero.UserAuth.repositories;

import com.springpiero.UserAuth.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

}
