package nl.inholland.Bank.API.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor(force = true)
@Entity
@Data
@Table(name = "users")
public class User {

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Id
    private Long id;

    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private String bsn;
    private String birthdate;
    private String streetName;
    private int houseNumber;
    private String zipCode;
    private String city;
    private String country;
    private int dailyLimit;
    private int transactionLimit;

    private Role role;

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("User{");
        sb.append("id=").append(id);
        sb.append(", firstName='").append(firstName).append('\'');
        sb.append(", lastName='").append(lastName).append('\'');
        sb.append(", password='").append(password).append('\'');
        sb.append(", roles=").append(role);
        sb.append('}');
        return sb.toString();
    }

}