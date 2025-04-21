package com.example.flowershoptr.service;
import com.example.flowershoptr.model.User;
import com.example.flowershoptr.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User processOAuthPostLogin(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String googleId = oAuth2User.getAttribute("sub");

        logger.info("Processing OAuth login for email: {}, googleId: {}", email, googleId);

        User existingUser = userRepository.findByGoogleId(googleId).orElse(null);

        if (existingUser == null) {
            logger.info("New user detected, creating database entry");

            // Новый пользователь, создаем запись
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setName(oAuth2User.getAttribute("name"));
            newUser.setGoogleId(googleId);
            newUser.setPictureUrl(oAuth2User.getAttribute("picture"));

            User savedUser = userRepository.save(newUser);
            logger.info("New user saved with ID: {}", savedUser.getId());
            return savedUser;
        } else {
            logger.info("Existing user found with ID: {}", existingUser.getId());
        }

        return existingUser;
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User getUserByGoogleId(String googleId) {
        return userRepository.findByGoogleId(googleId).orElse(null);
    }

    @Transactional
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public User getUserById(Long userId) {
        return userRepository.findById(userId).orElse(null);
    }
}


