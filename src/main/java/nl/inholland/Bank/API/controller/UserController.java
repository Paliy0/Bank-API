package nl.inholland.Bank.API.controller;

import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.model.User;
import nl.inholland.Bank.API.model.dto.UserDTO;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import nl.inholland.Bank.API.service.UserService;

import java.util.Optional;


@RestController
@RequestMapping(value = "/users" , produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    private final UserService userService;
    private final ModelMapper modelMapper;

    public UserController(UserService userService) {
        this.userService = userService;
        this.modelMapper = new ModelMapper();
    }

    @GetMapping
    public ResponseEntity<Iterable<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit) {
        try{
            // do something with skip and limit

            return ResponseEntity.ok().body(userService.getAllUsers());

        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Object> registerUser(@RequestBody UserDTO userRequest) {
        try {
            //check if new user detail is valid
            if(!validateEmail(userRequest.getEmail())){
                return  ResponseEntity.status(HttpStatus.CREATED).body("Bad request. User with this email already exists");
            }
            if(!checkUserBody(userRequest)){
                return  ResponseEntity.status(HttpStatus.CREATED).body("Bad request. Invalid User Information.");
            }

            User newUser = modelMapper.map(userRequest, User.class);
            newUser.setRole(Role.ROLE_USER);

            userService.add(newUser);

            return  ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");
        }
    }
    @PutMapping(value = "/updateInformation/{id}")
    public ResponseEntity<Object> changeUserData(@PathVariable long id, @RequestBody UserDTO newUserData){
        try{
            Optional<User> response = userService.getUserById(id);
            User user = response.get();

            if(!validateEmailChange(newUserData.getEmail(), user.getEmail())){
                return  ResponseEntity.status(HttpStatus.CREATED).body("Bad request. User with this email already exists or email is in wrong format");
            }
            if(!checkUserBody(newUserData)){
                return  ResponseEntity.status(HttpStatus.CREATED).body("Bad request. Invalid User Information.");
            }
            userService.update(newUserData, id);

            return  ResponseEntity.status(HttpStatus.CREATED).body("Information updated successfully");
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Unexpected server error");

        }
    }
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getLoggedInUser(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.getUserById(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping(value = "dailyLimit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getDailyLimitById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.getDailyLimit(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "transactionLimit/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> getTransactionLimitById(@PathVariable long id) {
        try {
            return ResponseEntity.ok().body(userService.getTransactionLimit(id));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    private boolean checkUserBody(UserDTO userBody) {

        if (!(userBody.getFirstName().length() > 1 &&
                userBody.getLastName().length() > 1 &&
                userBody.getStreetName().length() > 2 &&
                userBody.getHouseNumber() > 0 &&
                userBody.getZipCode().length() > 3 &&
                userBody.getCity().length() > 3 &&
                userBody.getCountry().length() > 3 &&
                isStrongPassword(userBody.getPassword()))
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