package com.taskmngr.release.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
     http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/login", "/register", "/css/**").permitAll()
            .requestMatchers("/add", "/toggle/**").hasAnyRole("USER", "MANAGER", "ADMIN") // Wszyscy mogą dodawać/klikać
            .requestMatchers("/delete/**").hasAnyRole("MANAGER", "ADMIN") // Tylko szefowie usuwają (lub Twoja logika z kontrolera)
            .anyRequest().authenticated()
        )
         .formLogin(form -> form
            .loginPage("/login") // Nasza własna strona logowania
            .defaultSuccessUrl("/", true) // Po zalogowaniu rzucamy na stronę główną
            .permitAll()
            )
          .logout(logout -> logout
            .logoutUrl("/logout") // Adres, na który wysyłamy POST z HTML
            .logoutSuccessUrl("/login?logout") // Gdzie przekierować po wyjściu
            .invalidateHttpSession(true) // Czyścimy sesję
            .deleteCookies("JSESSIONID") // Usuwamy ciasteczka
            .permitAll()
            );

        return http.build();
    }

    // Narzędzie do szyfrowania haseł
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}