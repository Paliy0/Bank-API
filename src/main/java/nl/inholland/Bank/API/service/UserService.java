package nl.inholland.Bank.API.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void add(User user) {
//        if (user != null) {
        userRepository.save(user);
//        } else {
//            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "error, user was null");
//        }
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void save(User user) {
        userRepository.save(user);
    }
}
