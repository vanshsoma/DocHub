package com.ooad.project.collabeditor.service;

import com.ooad.project.collabeditor.model.Role;
import com.ooad.project.collabeditor.model.User;
import com.ooad.project.collabeditor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    /** Observer Pattern — publish events instead of calling ActivityTracker directly */
    @Autowired
    private EventPublisher eventPublisher;

    public User authenticate(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            eventPublisher.publish("LOGIN", user, null);
            return user;
        }
        return null;
    }

    public boolean authorize(User user, String action) {
        if (user == null) return false;
        if (user.getRole() == Role.ADMIN) return true;
        
        if (user.getRole() == Role.VIEWER && !action.equals("READ")) {
            return false; // Viewers can only read
        }
        return true;
    }
}
