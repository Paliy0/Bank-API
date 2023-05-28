package nl.inholland.Bank.API.config;

import nl.inholland.Bank.API.model.User;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
import jakarta.transaction.Transactional;

import nl.inholland.Bank.API.model.Role;
import nl.inholland.Bank.API.service.UserService;


@Component
@Transactional
public class MyApplicationRunner implements ApplicationRunner {

    private final UserService userService;

    public MyApplicationRunner(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        User user = new User();
        user.setFirstName("Pablo");
        user.setLastName("Gulias");
        user.setEmail("pablo@student.nl");
        user.setPassword("secret123");
        user.setBirthdate("01-10-2000");
        user.setStreetName("Schoonzichtlaan");
        user.setHouseNumber(8);
        user.setZipCode("2015CV");
        user.setCity("Haarlem");
        user.setCountry("Netherlands");

        user.setRole(Role.ROLE_USER);
        userService.add(user);

        User user2 = new User();
        user2.setFirstName("Johanna");
        user2.setLastName("Becker");
        user2.setEmail("jo@student.nl");
        user2.setPassword("secret123");
        user2.setBirthdate("16-06-2001");
        user2.setStreetName("Lelyweg");
        user2.setHouseNumber(42);
        user2.setZipCode("2031CD");
        user2.setCity("Haarlem");
        user2.setCountry("Netherlands");

        user2.setRole(Role.ROLE_USER);
        userService.add(user2);
    }

}