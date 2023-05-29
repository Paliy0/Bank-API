package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.UserDLimitDTO;
import nl.inholland.Bank.API.model.dto.UserDTO;
import nl.inholland.Bank.API.model.dto.UserTLimitDTO;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import nl.inholland.Bank.API.repository.UserRepository;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    //private final ModelMapper modelMapper =  new ModelMapper();


    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User add(User user) {
        if (user != null) {
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "error, user was null");
        }
        return user;
    }
    public User update(UserDTO newUserData, Long id){
        Optional<User> response = getUserById(id);
        User currentUser = response.get();

        if(currentUser != null){
            currentUser.setFirstName(newUserData.getFirstName());
            currentUser.setLastName(newUserData.getLastName());
            currentUser.setEmail(newUserData.getEmail());
            currentUser.setPassword(newUserData.getPassword());
            currentUser.setCity(newUserData.getCity());
            currentUser.setStreetName(newUserData.getStreetName());
            currentUser.setHouseNumber(newUserData.getHouseNumber());
            currentUser.setZipCode(newUserData.getZipCode());
            currentUser.setCountry(newUserData.getCountry());
            currentUser.setBirthdate(newUserData.getBirthdate());
        }

        return userRepository.save(currentUser);
    }
    public List<User> getAllUsers(){
        return (List<User>) userRepository.findAll();
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }
    // Get by Username
    public User getUserByEmail(String username) {
        return userRepository.findByEmail(username);
    }
    public UserDLimitDTO getDailyLimit(Long id){
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        UserDLimitDTO dailyLimit = new UserDLimitDTO();
        dailyLimit.setId(id);
        dailyLimit.setDailyLimit(String.valueOf(user.getDailyLimit()));
        return dailyLimit;
    }

    public UserTLimitDTO getTransactionLimit(Long id){
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        UserTLimitDTO transactionLimit = new UserTLimitDTO();
        transactionLimit.setId(id);
        transactionLimit.setTransactionLimit(String.valueOf(user.getTransactionLimit()));
        return transactionLimit;
    }
}