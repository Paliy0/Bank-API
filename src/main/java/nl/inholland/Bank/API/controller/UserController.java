package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.UserDLimitDTO;
import nl.inholland.Bank.API.model.dto.UserRequestDTO;
import nl.inholland.Bank.API.model.dto.UserResponseDTO;
import nl.inholland.Bank.API.model.dto.UserTLimitDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import nl.inholland.Bank.API.service.UserService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping(value = "/users" , produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Get all accounts
     * HTTP Method: Get
     * URL: /users
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @GetMapping
    public ResponseEntity<Iterable<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "true") boolean hasAccount)  { //will only return users without an account if specified hasAccount=false
        try{
            if (skip < 0 || limit <= 0) {
                return ResponseEntity.badRequest().build();
            }

            Iterable<UserResponseDTO> users = userService.getAllUsers(hasAccount);

            // Perform pagination logic
            List<UserResponseDTO> paginatedUsers = StreamSupport.stream(users.spliterator(), false)
                    .skip(skip)
                    .limit(limit)
                    .collect(Collectors.toList());

            return ResponseEntity.ok().body(paginatedUsers);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Post a user
     * HTTP Method: POST
     * URL: /users
     */

    @PostMapping
    public ResponseEntity<Object> registerUser(@RequestBody UserRequestDTO userRequest) {
        try {
            //check if new user detail is valid
            if(!validateEmail(userRequest.email())){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request. User with this email already exists");
            }
            if(!checkUserBody(userRequest)){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request. Invalid User Information.");
            }

            //model mapper is not working with Record DTO classes????
            //User newUser = modelMapper.map(userRequest, User.class);

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

            userService.add(newUser);

            return  ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }

    /**
     * Update User information
     * HTTP Method: PUT
     * URL: users/updateInformation/{id}
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping(value = "/updateInformation/{id}")
    public ResponseEntity<Object> changeUserData(@PathVariable long id, @RequestBody UserRequestDTO newUserData){
        try{
            Optional<User> response = userService.getUserById(id);
            User user = response.get();

            if(!validateEmailChange(newUserData.email(), user.getEmail())){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request. User with this email already exists or email is in wrong format");
            }
            if(!checkUserBody(newUserData)){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request. Invalid User Information.");
            }
            userService.update(newUserData, id);

            return  ResponseEntity.status(HttpStatus.CREATED).body("Information updated successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");

        }
    }

    /**
     * Get a User
     * HTTP Method: Get
     * URL: /users/{id}
     */
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_CUSTOMER', 'ROLE_EMPLOYEE')")
    @CrossOrigin
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLoggedInUser(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Delete a User
     * HTTP Method: Delete
     * URL: /users/{id}
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable long id){
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(userService.deleteUserOrDeactivate(id));
    }

    /**
     * Get a user's daily limit
     * HTTP Method: GET
     * URL: /users/dailyLimit/{id}
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE') || hasRole('ROLE_CUSTOMER')")
    @GetMapping(value = "dailyLimit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDailyLimitById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.getDailyLimit(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update a user's daily limit as an employee
     * HTTP Method: PUT
     * URL: /users/{userId}/dailyLimit
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping(value = "/{userId}/dailyLimit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDLimitDTO> updateDailyLimitById(@PathVariable Long userId, @RequestParam int dailyLimit) {
        try {
            return ResponseEntity.ok().body(userService.updateDailyLimit(userId, dailyLimit));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Get a user's transaction Limit
     * HTTP Method: GET
     * URL: /users/transactionLimit/{id}
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE') || hasRole('ROLE_CUSTOMER')")
    @GetMapping(value = "transactionLimit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTransactionLimitById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.getTransactionLimit(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Update a user's transaction limit as an employee
     * HTTP Method: PUT
     * URL: /users/{userId}/transactionLimit
     */
    @PutMapping(value = "/{userId}/transactionLimit", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserTLimitDTO> updateTransactionLimitById(@PathVariable Long userId, @RequestParam int transactionLimit) {
        try {
            return ResponseEntity.ok().body(userService.updateTransactionLimit(userId, transactionLimit));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
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
        if(userService.getUserByEmail(emailStr) != null){
            return false;
        }

        //check correct email format
        return emailStr.matches("^(.+)@(.+)$");
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

}