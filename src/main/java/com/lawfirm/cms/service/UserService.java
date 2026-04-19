package com.lawfirm.cms.service;

import com.lawfirm.cms.model.Admin;
import com.lawfirm.cms.model.Client;
import com.lawfirm.cms.model.Lawyer;
import com.lawfirm.cms.model.Staff;
import com.lawfirm.cms.model.User;
import com.lawfirm.cms.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> findByRole(String role) {
        return userRepository.findByRole(role);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public User createUser(String name, String email, String password, String role) {
        User user;
        switch (role.toUpperCase()) {
            case "ADMIN":
                user = new Admin();
                break;
            case "LAWYER":
                user = new Lawyer();
                break;
            case "STAFF":
                user = new Staff();
                break;
            case "CLIENT":
                user = new Client();
                break;
            default:
                user = new Client();
                break;
        }
        user.setName(name);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role.toUpperCase());
        return userRepository.save(user);
    }

    public User updateUser(Long id, String name, String email, String role) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isPresent()) {
            User user = opt.get();
            user.setName(name);
            user.setEmail(email);
            user.setRole(role);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with id: " + id);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public void incrementFailedAttempts(User user) {
        int attempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(attempts);
        if (attempts >= 3) {
            user.setLocked(true);
        }
        userRepository.save(user);
    }

    public void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        user.setLocked(false);
        userRepository.save(user);
    }

    public void unlockAccount(User user) {
        user.setLocked(false);
        user.setFailedAttempts(0);
        userRepository.save(user);
    }
}
