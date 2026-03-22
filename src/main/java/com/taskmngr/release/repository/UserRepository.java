package com.taskmngr.release.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taskmngr.release.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}