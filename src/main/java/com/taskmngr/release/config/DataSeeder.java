package com.taskmngr.release.config;

import com.taskmngr.release.model.User;
import com.taskmngr.release.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        
        if (userRepository.count() == 0) {
            String defaultPassword = passwordEncoder.encode("1234"); 

            
            User admin = new User("admin", defaultPassword);
            admin.setRole("ADMIN");
            userRepository.save(admin);

            
            User manager1 = new User("kierownik_jan", defaultPassword);
            manager1.setRole("MANAGER");
            
            User manager2 = new User("kierownik_anna", defaultPassword);
            manager2.setRole("MANAGER");

            
            userRepository.saveAll(List.of(manager1, manager2));

            
            
            
            User worker1 = new User("piotr_pracownik", defaultPassword);
            worker1.setRole("USER");
            worker1.setManager(manager1); 

            User worker2 = new User("ewa_pracownik", defaultPassword);
            worker2.setRole("USER");
            worker2.setManager(manager1); 

            
            User worker3 = new User("michal_pracownik", defaultPassword);
            worker3.setRole("USER");
            worker3.setManager(manager2); 

            User worker4 = new User("kasia_pracownik", defaultPassword);
            worker4.setRole("USER");
            worker4.setManager(manager2); 

            User worker5 = new User("tomek_pracownik", defaultPassword);
            worker5.setRole("USER");
            worker5.setManager(manager2); 

            
            userRepository.saveAll(List.of(worker1, worker2, worker3, worker4, worker5));

            System.out.println("Utworzono konta testowe: 1 Admin, 2 Menedżerów, 5 Pracowników.");
        }
    }
}