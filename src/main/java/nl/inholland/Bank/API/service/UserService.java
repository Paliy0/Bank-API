package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.UserDLimitDTO;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import nl.inholland.Bank.API.model.dto.UserTLimitDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import nl.inholland.Bank.API.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final TransactionService transactionService;


    public UserService(UserRepository userRepository, TransactionService transactionService) {
        this.userRepository = userRepository;
        this.modelMapper = new ModelMapper();
        this.transactionService = transactionService;
    }

    public User add(User user) {
        if (user != null) {
            userRepository.save(user);
        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "error, user was null");
        }
        return user;
    }
    public boolean update(UserRequestDTO newUserData, Long id){
        Optional<User> response = userRepository.findById(id);
        User currentUser = response.get();

        if(currentUser != null){
            currentUser.setFirstName(newUserData.firstName());
            currentUser.setLastName(newUserData.lastName());
            currentUser.setEmail(newUserData.email());
            currentUser.setPhoneNumber(newUserData.phoneNumber());
            currentUser.setBsn(newUserData.bsn());
            currentUser.setCity(newUserData.city());
            currentUser.setStreetName(newUserData.streetName());
            currentUser.setHouseNumber(newUserData.houseNumber());
            currentUser.setZipCode(newUserData.zipCode());
            currentUser.setCountry(newUserData.country());
            currentUser.setBirthdate(newUserData.birthdate());
        }

        User updated = userRepository.save(currentUser);

        if(updated != null) {
            return true;
        }else{
            return false;
        }
    }
    public List<UserResponseDTO> getAllUsers(){
        //get users
        Iterable<User> users = userRepository.findAll();
        //only return the required response data with the UserResponseDTO
        List<UserResponseDTO> responseUsers = new ArrayList<>();
        for(User user : users){
            UserResponseDTO userResponse = new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getBsn(), user.getBirthdate(), user.getStreetName(), user.getHouseNumber(), user.getZipCode(), user.getCity(), user.getCountry(), user.getDailyLimit(), user.getTransactionLimit(), user.getRole());
            responseUsers.add(userResponse);
        }
        return responseUsers;
    }

    public Optional<User> getUserById(Long id){
        return userRepository.findById(id);
    }
    // Get by Email
    //return User instead of response because password needed for login???
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    //still needs to be calculated with the transactions
    public UserDLimitDTO getDailyLimit(Long id){
        Optional<User> response = userRepository.findById(id);
        User user = response.get();

        LocalDate today = LocalDate.now();
        List<Transaction>transactionsOfToday = transactionService.getUserTransactionsByDay(user.getId(), today);

        int dailyTotal = 0;
        for(Transaction transaction : transactionsOfToday){
            dailyTotal += transaction.getAmount();
        }

        int dailyLimitLeft = user.getDailyLimit() - dailyTotal;

        UserDLimitDTO dailyLimit = new UserDLimitDTO(user.getId(), dailyLimitLeft);
        return dailyLimit;
    }
    public UserTLimitDTO getTransactionLimit(Long id){
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        UserTLimitDTO transactionLimit = new UserTLimitDTO(user.getId(), user.getTransactionLimit());
        return transactionLimit;
    }
}