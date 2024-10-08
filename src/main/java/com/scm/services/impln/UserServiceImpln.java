package com.scm.services.impln;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.scm.entities.User;
import com.scm.helpers.AppConstants;
import com.scm.helpers.ResourceNotFoundException;
import com.scm.helpers.helper;
import com.scm.repositories.UserRepo;
import com.scm.services.EmailService;
import com.scm.services.UserService;

@Service
public class UserServiceImpln implements UserService {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Autowired
    private EmailService emailService;


    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public User saveUser(User user) {
        //user id : have to generate
        String userId = UUID.randomUUID().toString();
        user.setUserId(userId);
        //password encode
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setRolelist(List.of(AppConstants.ROLE_USER));
        logger.info(user.getProvider().toString());
        

        String emailToken = UUID.randomUUID().toString();

        user.setEmailToken(emailToken);
        User savedUser = userRepo.save(user);

        String emailLink = helper.getLinkForEmailVerification(emailToken);

        emailService.sendEmail(savedUser.getEmail(), "Verify Account : Email Smart contact manager", emailLink);

        return savedUser;


    }

    @Override
    public Optional<User> getUserById(String id) {
        return userRepo.findById(id);
    }

    @Override
    public Optional<User> updateUser(User user) {
        User user2 = userRepo.findById(user.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));

          // update karenge user2 from user 
          user2.setName(user.getName());
          user2.setEmail(user.getEmail());
          user2.setPassword(user.getPassword());
          user2.setAbout(user.getAbout());
          user2.setPhoneNumber(user.getPhoneNumber());    
          user2.setProfilepic(user.getProfilepic());
          user2.setEnabled(user.isEnabled());
          user2.setEmailVerified(user.isEmailVerified());
          user2.setPhoneNummberverified(user.isPhoneNummberverified());
          user2.setProvider(user.getProvider());
          user2.setProviderUserId(user.getProviderUserId());  

          // save the user in the database

          User save = userRepo.save(user2);
          return Optional.ofNullable(save);

    }

    @Override
    public void deleteUser(String id) {
        User user2 = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("user not found"));
          userRepo.delete(user2);      
       
    }

    @Override
    public boolean isUserExist(String userId) {
        User user2 = userRepo.findById(userId).orElse(null);
        return user2 != null ? true : false;
    }

    @Override
    public boolean isUserExistByEmail(String email) {
        User user = userRepo.findByEmail(email).orElse(null);
        return user != null ? true : false;
    }

    @Override
    public List<User> getAllUsers() {
       return userRepo.findAll();
    }

    @Override
    public User getUserByEmail(String email) {
       return userRepo.findByEmail(email).orElseThrow(()-> new ResourceNotFoundException("user not found"));
    }

}
