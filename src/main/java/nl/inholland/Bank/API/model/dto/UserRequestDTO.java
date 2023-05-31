package nl.inholland.Bank.API.model.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import nl.inholland.Bank.API.model.Role;
// for creating/registering a new User
public record UserRequestDTO (String firstName, String lastName, String email, String password, String phoneNumber, String bsn, String birthdate, String streetName, int houseNumber, String zipCode, String city, String country){
}