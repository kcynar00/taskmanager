package com.taskmngr.release.config;

import com.taskmngr.release.model.User;
import com.taskmngr.release.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByLogin(String login) throws LoginNotFoundException {
        User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new LoginNotFoundException("Nie znaleziono użytkownika: " + username));

        return org.springframework.security.core.userdetails.User
                .withLogin(user.getLogin())
                .password(user.getPassword())
                .roles(user.getRole()) // Wrzucamy czysty, pojedynczy String prosto z bazy!
                .build();
    }
}