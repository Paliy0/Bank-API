package nl.inholland.Bank.API.model.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

public record UserDLimitDTO (Long userId, int dailyLimit) {
}