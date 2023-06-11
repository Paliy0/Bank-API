package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountStatus;
import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.TokenDTO;
import nl.inholland.Bank.API.model.dto.UserDLimitDTO;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import nl.inholland.Bank.API.model.dto.UserTLimitDTO;
import nl.inholland.Bank.API.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import ch.qos.logback.core.subst.Token;
import nl.inholland.Bank.API.repository.UserRepository;
import nl.inholland.Bank.API.util.JwtTokenProvider;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

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
    public String registerChecking(UserRequestDTO userRequest){
        String errorMessage = "";
        if(!validateEmail(userRequest.email())){
            return  "User with this email already exists";
        }
        if(!checkUserBody(userRequest)){
            return  "Bad request. Invalid User Information.";
        }
        return errorMessage;
    }
    public boolean registerLogic(UserRequestDTO userRequest){
        User newUser = new User();
        newUser.setFirstName(userRequest.firstName());
        newUser.setLastName(userRequest.lastName());
        newUser.setEmail(userRequest.email());
        newUser.setPassword(userRequest.password());
        newUser.setBsn(userRequest.bsn());
        newUser.setPhoneNumber(userRequest.phoneNumber());
        newUser.setBirthdate(userRequest.birthdate());
        newUser.setStreetName(userRequest.streetName());
        newUser.setHouseNumber(userRequest.houseNumber());
        newUser.setCity(userRequest.city());
        newUser.setZipCode(userRequest.zipCode());
        newUser.setCountry(userRequest.country());
        newUser.setRole(Role.ROLE_USER);
        newUser.setTransactionLimit(100);
        newUser.setDailyLimit(200);
        User added = add(newUser);
        if(added != null){
            return true;
        }else{
            return false;
        }
    }
    public boolean update(UserRequestDTO newUserData, Long id){
        Optional<User> response = userRepository.findById(id);
        User currentUser = response.get();

        if (currentUser != null) {
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

        if (updated != null) {
            return true;
        } else {
            return false;
        }
    }
    public String updateChecking(UserRequestDTO newUserData, String userEmail){
        if(!validateEmailChange(newUserData.email(), userEmail)){
            return  "User with this email already exists or email is in wrong format";
        }
        if(!checkUserBody(newUserData)){
            return  "Invalid User Information.";
        }
        return "";
    }
    public List<UserResponseDTO> getAllUsers(boolean hasAccount, int skip, int limit) {
        Iterable<User> response = userRepository.findAll();

        if (!hasAccount) {
            response = userRepository.findUsersWithoutAccount();
        }

        List<User> users = new ArrayList<>();
        response.forEach(users::add);

        // only return the required response data with the UserResponseDTO
        List<UserResponseDTO> responseUsers = new ArrayList<>();
        for (User user : users) {
            UserResponseDTO userResponse = new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getBsn(), user.getBirthdate(), user.getStreetName(), user.getHouseNumber(), user.getZipCode(), user.getCity(), user.getCountry(), user.getDailyLimit(), user.getTransactionLimit(), user.getRole());
            responseUsers.add(userResponse);
        }

        // Perform pagination logic
        List<UserResponseDTO> paginatedUsers = StreamSupport.stream(responseUsers.spliterator(), false)
                .skip(skip)
                .limit(limit)
                .collect(Collectors.toList());

        return paginatedUsers;
    }
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get by Email
    //return User instead of response because password needed for login???
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).get();
    }
    public boolean emailExistsCheck(String email){
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return false;
        } else {
            return true;
        }
    }

    //still needs to be calculated with the transactions
    public UserDLimitDTO getDailyLimit(Long id) {
        Optional<User> response = userRepository.findById(id);
        User user = response.get();

        LocalDate today = LocalDate.now();
        List<Transaction> transactionsOfToday = transactionService.getUserTransactionsByDay(user.getId(), today);

        int dailyTotal = 0;
        for (Transaction transaction : transactionsOfToday) {
            dailyTotal += transaction.getAmount();
        }

        int dailyLimitLeft = user.getDailyLimit() - dailyTotal;

        UserDLimitDTO dailyLimit = new UserDLimitDTO(user.getId(), dailyLimitLeft);
        return dailyLimit;
    }

    public UserDLimitDTO updateDailyLimit(Long id, int dailyLimit) {
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        user.setDailyLimit(dailyLimit);
        User updatedUser = userRepository.save(user);

        return new UserDLimitDTO(updatedUser.getId(), updatedUser.getDailyLimit());
    }

    public UserTLimitDTO getTransactionLimit(Long id) {
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        UserTLimitDTO transactionLimit = new UserTLimitDTO(user.getId(), user.getTransactionLimit());
        return transactionLimit;
    }

    public UserTLimitDTO updateTransactionLimit(Long id, int transactionLimit) {
        Optional<User> response = userRepository.findById(id);
        User user = response.get();
        user.setTransactionLimit(transactionLimit);
        User updatedUser = userRepository.save(user);

        return new UserTLimitDTO(updatedUser.getId(), updatedUser.getTransactionLimit());
    }

    public ResponseEntity<String> deleteUserOrDeactivate(Long userId) {
        Optional<User> userOpt = getUserById(userId);
        if (!userOpt.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        User user = userOpt.get();
        Iterable<Account> userAccounts = accountService.findByAccountHolder(user);

        if (userAccounts.iterator().hasNext()) {
            for (Account account : userAccounts) {
                if(account.getAccountStatus() == AccountStatus.ACTIVE)
                account.setAccountStatus(AccountStatus.INACTIVE);
                accountService.saveAccount(account);
            }
            return ResponseEntity.ok("All the accounts were deactivated successfully. User with accounts cannot be deleted.");
        }

        userRepository.deleteById(userId);
        return ResponseEntity.ok("User was deleted successfully.");
    }
    /**
     * Checking Methods
     */
    private boolean checkUserBody(UserRequestDTO userBody) {

        if (!(userBody.firstName().length() > 1 &&
                userBody.lastName().length() > 1 &&
                userBody.streetName().length() > 2 &&
                userBody.houseNumber() > 0 &&
                userBody.zipCode().length() > 3 &&
                userBody.city().length() > 3 &&
                userBody.country().length() > 3 &&
                isStrongPassword(userBody.password()))
        ) {
            return false;
        }

        return true;
    }
    private boolean validateEmail(String emailStr) {
        //check if email already exists
        return emailExistsCheck(emailStr);
    }
    private boolean validateEmailChange(String newEmailStr, String oldEmailStr){

        if(!newEmailStr.equals(oldEmailStr)){
            return validateEmail(newEmailStr);
        }else{
            return newEmailStr.matches("^(.+)@(.+)$");
        }
    }

    //for password containing at least 8 letters, one uppercase/lowercase, a number, a special character
    private boolean isStrongPassword(String password){
        return password.matches("(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Z])(?=.*[a-z]).*$");
    }


    public TokenDTO login(String email, String password) throws Exception {
        // See if a user with the provided username exists or throw exception
        Optional<User> optionalUser = this.userRepository.findByEmail(email);
        if (!optionalUser.isPresent()) {
            throw new AuthenticationException("User not found");
        }
        User user = optionalUser.get();

        // Check if the password hash matches
        if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
            // Return a JWT to the client
            return new TokenDTO(jwtTokenProvider.createToken(user.getEmail(), user.getRole()), user.getId());
        } else {
            throw new AuthenticationException("Invalid username/password");
        }
    }
}