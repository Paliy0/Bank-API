package nl.inholland.Bank.API.config;

import java.time.LocalDateTime;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import jakarta.transaction.Transactional;
import nl.inholland.Bank.API.model.Transaction;
import nl.inholland.Bank.API.service.TransactionService;


@Component
@Transactional
public class MyApplicationRunner implements ApplicationRunner {

    private final TransactionService transactionService;

    public MyApplicationRunner(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        Transaction transaction1 = new Transaction();
        transaction1.setFromAccountIban("NL47INGB1234567890");
        transaction1.setToAccountIban("NL56ABNA0987654321");
        transaction1.setAmount(50);
        transaction1.setDescription("test transaction");
        transaction1.setUserId(1);

        Transaction transaction2 = new Transaction();
        transaction2.setFromAccountIban("NL91ABNA0417164300");
        transaction2.setToAccountIban("NL69RABO0123456789");
        transaction2.setAmount(25);
        transaction2.setDescription("some random transaction");
        transaction2.setUserId(2);

        transactionService.performTransaction(transaction1);
        transactionService.performTransaction(transaction2);
    }

}