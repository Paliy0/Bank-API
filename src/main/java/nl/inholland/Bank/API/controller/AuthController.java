package nl.inholland.Bank.API.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import nl.inholland.Bank.API.model.dto.LoginDTO;
import nl.inholland.Bank.API.service.UserService;

@RestController
@CrossOrigin(origins = "http://localhost:5173")
@RequestMapping("/login")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public Object login(@RequestBody LoginDTO dto) throws Exception {
            return userService.login(dto.email(), dto.password());
    }
}
