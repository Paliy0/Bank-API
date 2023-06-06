package nl.inholland.Bank.API.model.dto;

// for creating/registering a new User
public record UserRequestDTO (String firstName, String lastName, String email, String password, String phoneNumber, String bsn, String birthdate, String streetName, int houseNumber, String zipCode, String city, String country){
}