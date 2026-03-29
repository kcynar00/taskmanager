package com.taskmngr.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmngr.release.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
    // Zwraca wszystkich użytkowników podlegających pod konkretnego menedżera
    List<User> findByManager(User manager);
}