package nl.inholland.Bank.API.model;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class TransactionSpecifications {
    public static Specification<Transaction> withAmountBetween(double minAmount, double maxAmount) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("amount"), minAmount, maxAmount);
    }

    public static Specification<Transaction> withTimestampBetween(LocalDate startDate, LocalDate endDate) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.between(root.get("timestamp"), startDate, endDate);
    }

    public static Specification<Transaction> withUserID(Long accountHolderID) {
        return (root, query, criteriaBuilder) -> {
            Join<Transaction, Account> fromAccountJoin = root.join("fromAccount", JoinType.LEFT);
            Join<Transaction, Account> toAccountJoin = root.join("toAccount", JoinType.LEFT);

            Predicate fromAccountPredicate = criteriaBuilder.equal(fromAccountJoin.get("accountHolder").get("id"), accountHolderID);
            Predicate toAccountPredicate = criteriaBuilder.equal(toAccountJoin.get("accountHolder").get("id"), accountHolderID);
            return criteriaBuilder.or(fromAccountPredicate, toAccountPredicate);
        };
    }

    public static Specification<Transaction> withTransactionType(TransactionType transactionType) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("transactionType"), transactionType);
    }
}
