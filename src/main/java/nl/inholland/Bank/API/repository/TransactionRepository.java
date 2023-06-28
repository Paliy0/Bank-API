package nl.inholland.Bank.API.repository;

import nl.inholland.Bank.API.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    List<Transaction> findTransactionsByUserIdAndTimestampBetween(Long userId, LocalDateTime startDate, LocalDateTime endDate);

    default Page<Transaction> findTransactions(Long userId, LocalDate startDate, LocalDate endDate, Double minAmount, Double maxAmount,
                                               TransactionType transactionType, String fromIban, String toIban, Pageable pageable) {

        Specification<Transaction> specification = Specification.where(null);

        if (userId != null) {
            specification = specification.and(TransactionSpecifications.withUserID(userId));
        }

        if (startDate != null && endDate != null) {
            specification = specification.and(TransactionSpecifications.withTimestampBetween(startDate, endDate));
        }

        if (minAmount >= 0 && maxAmount >= 0) {
            specification = specification.and(TransactionSpecifications.withAmountBetween(minAmount, maxAmount));
        }

        if (transactionType != null) {
            specification = specification.and(TransactionSpecifications.withTransactionType(transactionType));
        }

        if (fromIban != null) {
            specification = specification.and(TransactionSpecifications.withFromIban(fromIban));
        }

        if (toIban != null) {
            specification = specification.and(TransactionSpecifications.withToIban(toIban));
        }

        return findAll(specification, pageable);
    }

}