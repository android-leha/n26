package com.n26.model;

import com.n26.exceptions.InvalidRequestException;
import com.n26.exceptions.InvalidTransaction;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.HashMap;

import static java.math.BigDecimal.ROUND_HALF_UP;


public class Transaction {

    private BigDecimal amount;

    private Instant timestamp;

    public Transaction(String amount, String timestamp) throws InvalidTransaction {
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        format.setLenient(false);
        try {
            format.parse(timestamp);
            this.timestamp = Instant.parse(timestamp);
            this.amount = (new BigDecimal(amount)).setScale(2, ROUND_HALF_UP);
        } catch (ParseException e) {
            throw new InvalidTransaction("Non parsable transaction timestamp: " + timestamp);
        } catch (NumberFormatException e) {
            throw new InvalidTransaction("Non parsable transaction amount: " + amount);
        }
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Instant getTimestamp() {
        return timestamp;
    }


    /**
     * Transaction factory from Payload
     * @param payload
     * @return
     * @throws InvalidRequestException
     * @throws InvalidTransaction
     */
    public static Transaction createFromPayload(HashMap<String, String> payload) throws InvalidRequestException, InvalidTransaction {
        if (!payload.containsKey("amount")) {
            throw new InvalidRequestException("Missed amount parameter");
        }

        if (!payload.containsKey("timestamp")) {
            throw new InvalidRequestException("Missed timestamp parameter");
        }

        if (payload.size() != 2) {
            throw new InvalidRequestException("Expected amount and timestamp parameters only");
        }

        return new Transaction(payload.get("amount"), payload.get("timestamp"));
    }

}
