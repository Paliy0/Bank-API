package nl.inholland.Bank.API.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class FindAccountResponseDTO {
    private String iban;
    private String user;
}
