package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.id NOT IN (SELECT a.accountHolder.id FROM Account a)")
    Iterable<User> findUsersWithoutAccount();

    //Iterable<User> findUsersByRolesIterable(Role roleUser);
}