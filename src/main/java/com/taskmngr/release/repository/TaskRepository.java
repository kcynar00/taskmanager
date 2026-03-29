package com.taskmngr.release.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmngr.release.model.Task;
import com.taskmngr.release.model.User;

public interface TaskRepository extends JpaRepository<Task, Long> {
    // Zwraca zadania przypisane do konkretnego użytkownika
    List<Task> findByAssignedUser(User user);
    
    // Zwraca zadania przypisane do pracowników konkretnego menedżera
    List<Task> findByAssignedUser_Manager(User manager);
}