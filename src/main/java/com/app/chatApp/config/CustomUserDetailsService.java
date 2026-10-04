package com.app.chatApp.config;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.app.chatApp.repository.RegisteredUsersRepo;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private RegisteredUsersRepo userRepo;

    public CustomUserDetailsService(RegisteredUsersRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String mblNo) throws UsernameNotFoundException {
        return userRepo.findUserByMblNo(mblNo)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with mobile number: " + mblNo));
    }

}
