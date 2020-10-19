package com.n26.controllers;

import com.n26.Application;
import com.n26.exceptions.InvalidRequestException;
import com.n26.exceptions.InvalidTransaction;
import com.n26.exceptions.OldTransactionException;
import com.n26.models.Transaction;
import com.n26.services.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;

@RestController
public class TransactionController {

    Logger logger = LoggerFactory.getLogger(Application.class);

    private final TransactionService transactionService;


    @Autowired
    public TransactionController(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }


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
        Transaction transaction = Transaction.createFromPayload(payload);

        transactionService.add(transaction);
        logger.info("Added {}",  payload.toString());
    }

    /**
     * Clean all transactions
     */
    @DeleteMapping("/transactions")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cleanTransactions() {
        transactionService.clean();
    }

}
