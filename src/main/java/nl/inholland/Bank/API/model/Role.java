package nl.inholland.Bank.API.model;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority{
    ROLE_USER,
    ROLE_CUSTOMER,
    ROLE_EMPLOYEE;

	@Override
    public String getAuthority() {
        return name();
    }
}