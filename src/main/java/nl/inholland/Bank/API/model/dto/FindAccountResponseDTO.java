package nl.inholland.Bank.API.model.dto;

import nl.inholland.Bank.API.model.User;

//this should be used for /findIbanByCustomerName
//change user to userDTO
public record FindAccountResponseDTO(String iban, User user) {
}
