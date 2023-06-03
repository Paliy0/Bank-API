package nl.inholland.Bank.API.model.dto;

import nl.inholland.Bank.API.model.Role;

//for getting a user /all users
public record UserResponseDTO(Long id, String firstName, String lastName, String email, String phoneNumber, String bsn, String birthdate, String streetName, int houseNumber, String zipCode, String city, String country, int dailyLimit, int transactionLimit, Role role) {
}
