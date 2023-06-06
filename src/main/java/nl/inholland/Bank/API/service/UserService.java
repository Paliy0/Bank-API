package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.UserDLimitDTO;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import nl.inholland.Bank.API.model.dto.UserTLimitDTO;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import nl.inholland.Bank.API.repository.UserRepository;
import nl.inholland.Bank.API.util.JwtTokenProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.naming.AuthenticationException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final TransactionService transactionService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;


    public UserService(UserRepository userRepository, TransactionService transactionService, @Lazy AccountService accountService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.accountService = accountService;
        this.modelMapper = new ModelMapper();
        this.transactionService = transactionService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public User add(User user) {
        if (user != null) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        
        } else {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "error, user was null");
        }
        return userRepository.save(user);
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
    public List<UserResponseDTO> getAllUsers(boolean hasAccount){
        Iterable<User> users = userRepository.findAll();

        /* if(!hasAccount){
            users = userRepository.findUsersByRole(Role.ROLE_USER);
        } else {
            users = userRepository.findAll();
        } */
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
        return userRepository.findByEmail(email).get();
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

    public UserDLimitDTO updateDailyLimit(Long id, int dailyLimit){
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        user.setDailyLimit(dailyLimit);
        User updatedUser = userRepository.save(user);
        
        return new UserDLimitDTO(updatedUser.getId(), updatedUser.getDailyLimit());
    }

    public UserTLimitDTO getTransactionLimit(Long id){
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        UserTLimitDTO transactionLimit = new UserTLimitDTO(user.getId(), user.getTransactionLimit());
        return transactionLimit;
    }

    public UserTLimitDTO updateTransactionLimit(Long id, int transactionLimit){
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        user.setTransactionLimit(transactionLimit);
        User updatedUser = userRepository.save(user);
        
        return new UserTLimitDTO(updatedUser.getId(), updatedUser.getTransactionLimit());
    }

    public String deleteUserOrDeactivate(Long userId) {

        Optional<User> userOpt = this.getUserById(userId);
        if (!userOpt.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        User user = userOpt.get();

        Iterable<Account> userAccounts = accountService.findByAccountHolder(user);

        if (userAccounts.iterator().hasNext()) {
            for (Account account : userAccounts) {
                account.setAccountStatus(AccountStatus.INACTIVE);
                accountService.saveAccount(account);
            }
            return "All the accounts were deactivated successfully.";
        } 
        userRepository.deleteById(userId);
        return "User was deleted successfully.";
    }

    public String login(String email, String password) throws Exception {
        // See if a user with the provided username exists or throw exception
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new AuthenticationException("User not found");
        }
        User user = optionalUser.get();

        // Check if the password hash matches
        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            // Return a JWT to the client
            return jwtTokenProvider.createToken(user.getEmail(), user.getRole());
        } else {
            throw new AuthenticationException("Invalid username/password");
        }
    }
}