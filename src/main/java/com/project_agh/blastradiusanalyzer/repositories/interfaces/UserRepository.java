package com.project_agh.blastradiusanalyzer.repositories.interfaces;

import com.project_agh.blastradiusanalyzer.models.User;
import java.util.Optional;

public interface UserRepository {
    void saveUser(User user);
    Optional<User> findByUsername(String username);
}