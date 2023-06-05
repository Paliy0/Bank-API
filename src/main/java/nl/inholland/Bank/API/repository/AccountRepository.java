package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.User;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Iterable<Account> findAllByIbanNot(@Param("iban") String iban);

    Account findAccountByIban(@Param("iban") String iban);

    Iterable<Account> findIbanByAccountHolder_FirstName(@Param("firstName") String firstName);

    boolean existsAccountByIbanEquals(@Param("iban") String iban);

    boolean existsAccountByAccountHolder_Id(@Param("id") Long id);

    boolean existsAccountByAccountHolder_IdAndAccountTypeEquals(Long accountHolder_id, AccountType accountType);

    long countAccountByAccountHolder_Id(@Param("id") Long id);

    Iterable<Account> findAccountsByAccountHolder(User user);
}
