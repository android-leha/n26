package com.n26.models;

import com.n26.exceptions.InvalidRequestException;
import com.n26.exceptions.InvalidTransaction;
import com.n26.exceptions.OldTransactionException;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;

import static com.n26.services.TransactionService.PERIOD;
import static java.math.BigDecimal.ROUND_HALF_UP;

/**
 * Transaction model
 */
public class Transaction {

    private final BigDecimal amount;

    private final Instant timestamp;

    /**
     * Transaction constructor
     * @param amount - transaction amount
     * @param timestamp - transaction timestamp
     * @throws InvalidTransaction - Cannot parse timestamp or amount
     */
    public Transaction(String amount, String timestamp) throws InvalidTransaction {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setLenient(false);
        try {
            format.parse(timestamp);
            this.timestamp = Instant.parse(timestamp);
            this.amount = (new BigDecimal(amount)).setScale(2, ROUND_HALF_UP);
            if (1 != this.amount.compareTo(BigDecimal.ZERO)) {
                throw new InvalidTransaction("Amount should be greater then 0, given: " + amount);
            }
        } catch (ParseException e) {
            throw new InvalidTransaction("Non parsable transaction timestamp: " + timestamp);
        } catch (NumberFormatException e) {
            throw new InvalidTransaction("Non parsable transaction amount: " + amount);
        }
    }

    /**
     * @return - amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * @return - timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }


    /**
     * Create and Validate Transaction from Payload
     *
     * @param payload - JSON request
     * @return - new transaction
     * @throws InvalidRequestException - wrong parameters in payload
     * @throws InvalidTransaction - Future transaction or unable to parse parameters
     * @throws OldTransactionException - Transaction too old
     */
    public static Transaction createFromPayload(HashMap<String, String> payload) throws InvalidRequestException, InvalidTransaction, OldTransactionException {
        if (!payload.containsKey("amount")) {
            throw new InvalidRequestException("Missed amount parameter");
        }

        if (!payload.containsKey("timestamp")) {
            throw new InvalidRequestException("Missed timestamp parameter");
        }

        if (payload.size() != 2) {
            throw new InvalidRequestException("Expected amount and timestamp parameters only");
        }

        Transaction transaction = new Transaction(payload.get("amount"), payload.get("timestamp"));

        Instant now = Instant.now();
        // Reject future transactions
        if (transaction.getTimestamp().compareTo(now) > 0) {
            throw new InvalidTransaction("Future transaction");
        }

        long diff = Duration.between(now, transaction.getTimestamp()).toMillis();

        // Reject old transactions
        if (transaction.getTimestamp().compareTo(now.minusMillis(PERIOD)) < 0) {
            throw new OldTransactionException(diff);
        }

        return transaction;
    }

}
