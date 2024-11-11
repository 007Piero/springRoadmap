package com.springpiero.UserAuth.services;

import com.springpiero.UserAuth.models.Account;
import com.springpiero.UserAuth.repositories.UsersRepository;
import com.springpiero.UserAuth.utils.constants.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UsersService implements UserDetailsService {

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account save(Account newUser){
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        if(newUser.getAuthorities() == null){
            newUser.setAuthorities(Authority.USER.toString());
        }
        return usersRepository.save(newUser);
    }

    public List<Account> findAll(){
        return usersRepository.findAll();
    }

    public Optional<Account> findByEmail(String email){
        return usersRepository.findByEmail(email);
    }

    public Optional<Account> findById(Long id){
        return usersRepository.findById(id);
    }

    public void deleteById(Long id){
        usersRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalUser = usersRepository.findByEmail(email);

        if (!optionalUser.isPresent()){
            throw new UsernameNotFoundException("User not found");
        }
        Account myUser = optionalUser.get();

        List<GrantedAuthority> grantedAuthority = new ArrayList<>();
        grantedAuthority.add(new SimpleGrantedAuthority(myUser.getAuthorities()));

        return new User(myUser.getEmail(), myUser.getPassword(), grantedAuthority);
    }
}
