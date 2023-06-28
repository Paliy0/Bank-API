package nl.inholland.Bank.API.model;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AccountTest {

    private Account account;

    @Before
    public void init() {
        account = new Account();
        account.setIban("NL011393294");
        account.setBalance(250.25);
        account.setCreatedAt(LocalDate.now());
    }

    @Test
    public void newAccountShouldNotBeNull() {
        Assert.assertNotNull(account);
    }

    @Test
    public void accountCreationDateShouldBeToday() {
        Assert.assertEquals(LocalDate.now(), account.getCreatedAt());
    }

    @Test
    public void createAccountWithParametersShouldSetAccountStatusAndAccountType() {
        User user = new User();
        account = new Account(
                AccountType.CURRENT,
                AccountStatus.ACTIVE,
                user
        );
        Assert.assertEquals(AccountType.CURRENT, account.getAccountType());
        Assert.assertEquals(AccountStatus.ACTIVE, account.getAccountStatus());
        Assert.assertEquals(user, account.getAccountHolder());
    }
}