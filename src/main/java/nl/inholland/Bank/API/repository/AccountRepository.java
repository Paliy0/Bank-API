package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    Account findAccountById(Long id);
}
