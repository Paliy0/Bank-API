package nl.inholland.Bank.API.controller;

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

import java.util.Collections;
import java.util.List;
import java.util.Optional;


@RestController
//@CrossOrigin(origins = "*")
@RequestMapping("/users")
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
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Iterable<UserResponseDTO>> getAllUsers(
            @RequestParam(defaultValue = "0") int skip,
            @RequestParam(defaultValue = "50") int limit,
            @RequestParam(defaultValue = "true") boolean hasAccount)  { //will only return users without an account if specified hasAccount=false
        try{
            if (skip < 0 || limit <= 0) {
                return ResponseEntity.badRequest().build();
            }

            List<UserResponseDTO> users = userService.getAllUsers(hasAccount, skip, limit);

            return ResponseEntity.ok().body(users);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Post a user
     * HTTP Method: POST
     * URL: /users/register
     */

    @PostMapping(value = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> registerUser(@RequestBody UserRequestDTO userRequest) {
        try {
            String error = userService.registerChecking(userRequest);
            //check if new user detail is valid
            if(!error.isEmpty()){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request. " + error);
            }else {
                boolean registered = userService.registerLogic(userRequest);
                if(registered) {
                    return ResponseEntity.status(HttpStatus.OK).body("User created successfully");
                }
                else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Something went wrong. User not created.");
                }
            }
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    Collections.singletonMap(
                            "Error", e.getMessage()
                    )
            );
        }
    }

    /**
     * Update User information
     * HTTP Method: PUT
     * URL: users/updateInformation/{id}
     */
    @PreAuthorize("hasRole('ROLE_EMPLOYEE')")
    @PutMapping(value = "/updateInformation/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Object> changeUserData(@PathVariable long id, @RequestBody UserRequestDTO newUserData){
        try{
            Optional<User> response = userService.getUserById(id);
            User user = response.get();

            String error = userService.updateChecking(newUserData, user.getEmail());
            if(!error.isEmpty()){
                return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request. " + error);
            }else {
                boolean updated = userService.update(newUserData, id);
                if(updated){
                    return ResponseEntity.status(HttpStatus.CREATED).body("Information updated successfully");
                }else{
                    return  ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request. Something went wrong. Userdata is not updated.");
                }
            }
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
        return userService.deleteUserOrDeactivate(id);
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

}