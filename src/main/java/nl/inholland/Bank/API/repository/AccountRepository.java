package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findAccountById(Long id);
}
