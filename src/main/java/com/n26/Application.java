package com.n26;

import com.n26.exceptions.InvalidRequestException;
import com.n26.exceptions.InvalidTransaction;
import com.n26.exceptions.OldTransactionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
@RestController
@EnableAsync
@EnableScheduling
public class Application {

    Logger logger = LoggerFactory.getLogger(Application.class);

    private final TransactionService transactionService;

    @Autowired
    public Application(TransactionService transactionService) {
        this.transactionService = transactionService;
    }


    public static void main(String... args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * APIs
     */

    /**
     * Add new transaction
     *
     * @param payload
     * @throws InvalidTransaction
     * @throws InvalidRequestException
     * @throws OldTransactionException
     */
    @PostMapping("/transactions")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTransaction(@RequestBody HashMap<String, String> payload) throws InvalidTransaction, InvalidRequestException, OldTransactionException {
        long diff = TransactionService.add(transactionService, payload);
        logger.info("Added {} since {} ms",  payload.toString(), diff);
    }

    /**
     * Clean all transactions
     */
    @DeleteMapping("/transactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cleanTransactions() {
        transactionService.clean();
    }

    /**
     * Get recent statistics
     * @return
     */
    @GetMapping("/statistics")
    public Map<String, Object> statistics() {
        return transactionService.statistics();
    }

    /**
     * Exceptions Handlers
     */

    /**
     * Handle old transactions
     * @param e
     */
    @ExceptionHandler(OldTransactionException.class)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleException(OldTransactionException e) {
        logger.warn(e.getMessage());
    }

    /**
     * Handle wrong request
     * @param e
     */
    @ExceptionHandler(InvalidRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(InvalidRequestException e) {
        logger.warn(e.getMessage());
    }

    /**
     * Handle wrong JSON
     * @param e
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleException(HttpMessageNotReadableException e) {
        logger.warn(e.getMessage());
    }

    /**
     * Handle bad transactions
     * @param e
     */
    @ExceptionHandler(InvalidTransaction.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public void handleException(InvalidTransaction e) {
        logger.warn(e.getMessage());
    }


}
