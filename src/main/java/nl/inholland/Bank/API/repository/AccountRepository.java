package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.Account;
import nl.inholland.Bank.API.model.AccountType;
import nl.inholland.Bank.API.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    @Query("SELECT a FROM Account a WHERE a.iban <> :iban")
    Page<Account> findAllExceptBank(Pageable pageable, @Param("iban") String iban);

    Account findAccountByIban(@Param("iban") String iban);

    Account findAccountByIbanAndIbanNot(@Param("iban") String iban, String ibanBank);

    Iterable<Account> findIbanByAccountHolder_FirstName(@Param("firstName") String firstName);

    boolean existsAccountByIbanEquals(@Param("iban") String iban);

    boolean existsAccountByAccountHolder_Id(@Param("id") Long id);

    boolean existsAccountByAccountHolder_IdAndAccountTypeEquals(Long accountHolder_id, AccountType accountType);

    Iterable<Account> findAccountsByAccountHolder(User user);

    Iterable<Account> findAccountsByAccountHolder_Id(Long id);

    @Query("SELECT SUM(a.balance) FROM Account a WHERE a.accountHolder.id = :id")
    Double getCombinedBalanceByAccountHolderId(@Param("id") Long id);
}
