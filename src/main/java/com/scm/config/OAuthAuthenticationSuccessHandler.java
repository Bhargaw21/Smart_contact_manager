package com.scm.config;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.scm.entities.Providers;
import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.repositories.UserRepo;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuthAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(OAuthAuthenticationSuccessHandler.class);

    @Autowired
    private UserRepo userRepo;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        logger.info("OAuth Authentication Successful");

        // Identify the provider
        var oauth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        String providerId = oauth2AuthenticationToken.getAuthorizedClientRegistrationId();
        logger.info("Provider: " + providerId);

        var oauthUser = (DefaultOAuth2User) authentication.getPrincipal();

        // Debugging: Log attributes safely
        oauthUser.getAttributes().forEach((key, value) -> logger.info("{}: {}", key, value != null ? value : "NULL"));

        // Extract user details
        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String profilePic = oauthUser.getAttribute("picture");

        // Special handling for GitHub, as it sometimes lacks an email
        if (providerId.equalsIgnoreCase("github")) {
            email = (email != null) ? email : oauthUser.getAttribute("login") + "@github.com";
            profilePic = oauthUser.getAttribute("avatar_url");
            name = oauthUser.getAttribute("login");
        }

        if (email == null) {
            logger.error("No email found! Cannot proceed with authentication.");
            new DefaultRedirectStrategy().sendRedirect(request, response, "/login?error=email_not_found");
            return;
        }

        // Check if user exists
        Optional<User> existingUserOptional = userRepo.findByEmail(email);
        User user;
        
        if (existingUserOptional.isPresent()) {
            // Update existing user
            user = existingUserOptional.get();
            logger.info("User already exists, updating profile: {}", email);
        } else {
            // Register new user
            user = new User();
            user.setUserId(UUID.randomUUID().toString());
            user.setEmailVerified(true);
            user.setEnabled(true);
            user.setPassword("dummy");  // OAuth users don't use passwords
            user.setRolelist(List.of(AppConstants.ROLE_USER));
            logger.info("Registering new user: {}", email);
        }

        // Update user details
        user.setEmail(email);
        user.setProfilepic(profilePic);
        user.setName(name);
        user.setProviderUserId(oauthUser.getName());
        user.setProvider(providerId.equalsIgnoreCase("google") ? Providers.GOOGLE : Providers.GITHUB);
        user.setAbout("Account created using " + providerId);

        // Save user to database
        userRepo.save(user);
        logger.info("User saved successfully: {}", email);

        // Redirect user to profile page
        logger.info("Redirecting user to profile page...");
        new DefaultRedirectStrategy().sendRedirect(request, response, "/user/profile");
    }
}
