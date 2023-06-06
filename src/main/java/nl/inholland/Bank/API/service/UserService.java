package nl.inholland.Bank.API.service;

import nl.inholland.Bank.API.model.*;
import nl.inholland.Bank.API.model.dto.UserDLimitDTO;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import nl.inholland.Bank.API.model.dto.UserTLimitDTO;
import nl.inholland.Bank.API.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final AccountService accountService;
    private final ModelMapper modelMapper;
    private final TransactionService transactionService;


    public UserService(UserRepository userRepository, TransactionService transactionService, @Lazy AccountService accountService) {
        this.userRepository = userRepository;
        this.accountService = accountService;
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

    public User updateUserRole(User user) {
        User existingUser = userRepository.findById(user.getId()).get(); // Assuming there is a method to retrieve the existing user by ID
        if (existingUser != null) {
            existingUser.setRole(user.getRole()); // Update the role of the existing user
            // Perform any other necessary updates
            userRepository.save(existingUser); // Assuming there is a method to save the updated user
        }
        return existingUser;
    }


    public boolean update(UserRequestDTO newUserData, Long id) {
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

    public List<UserResponseDTO> getAllUsers(boolean hasAccount) {
        Iterable<User> users;

        if (!hasAccount) {
            users = userRepository.findByRole(Role.ROLE_USER);
        } else {
            users = userRepository.findAll();
        }
        //only return the required response data with the UserResponseDTO
        List<UserResponseDTO> responseUsers = new ArrayList<>();
        for (User user : users) {
            UserResponseDTO userResponse = new UserResponseDTO(user.getId(), user.getFirstName(), user.getLastName(), user.getEmail(), user.getPhoneNumber(), user.getBsn(), user.getBirthdate(), user.getStreetName(), user.getHouseNumber(), user.getZipCode(), user.getCity(), user.getCountry(), user.getDailyLimit(), user.getTransactionLimit(), user.getRole());
            responseUsers.add(userResponse);
        }
        return responseUsers;
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    // Get by Email
    //return User instead of response because password needed for login???
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email);
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
}