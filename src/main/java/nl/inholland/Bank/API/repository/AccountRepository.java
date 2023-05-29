package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Iterable<Account> findAllByIbanNot(@Param("iban") String iban);

    Account findAccountByIban(@Param("iban") String iban);

    Iterable<Account> findIbanByAccountHolder_FirstName(@Param("firstName") String firstName);
}
