package com.scm.services.impln;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.repositories.UserRepo;

@Service
public class SecurityCustomUserDetailService implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(SecurityCustomUserDetailService.class);

    @Autowired
    private UserRepo userRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        logger.info("Attempting to load user by username (email): {}", username);

        if (username == null || username.trim().isEmpty()) {
            logger.warn("Provided username is null or empty");
            throw new UsernameNotFoundException("Email cannot be empty");
        }

        // Optional: normalize email if needed
        String email = username.trim().toLowerCase(); // You can remove toLowerCase() if not needed

        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            logger.warn("User not found with email: {}", email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User user = userOptional.get();

        logger.info("User found: {}", user.getEmail());

        return user; // ✅ This should implement UserDetails
    }
}
