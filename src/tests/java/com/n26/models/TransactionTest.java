package com.n26.models;

import com.n26.exceptions.InvalidRequestException;
import com.n26.exceptions.InvalidTransaction;
import com.n26.exceptions.OldTransactionException;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;


public class TransactionTest {

    @Test
    public void createTransactionTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", "10.10");
        Instant now = Instant.now();
        payload.put("timestamp", now.toString());
        Transaction transaction = Transaction.createFromPayload(payload);
        Assert.assertEquals(transaction.getAmount(), new BigDecimal("10.10"));
        Assert.assertEquals(transaction.getTimestamp(), now);
    }

    @Test(expected = InvalidRequestException.class)
    public void noAmountTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("timestamp", Instant.now().toString());
        Transaction.createFromPayload(payload);
    }

    @Test(expected = InvalidRequestException.class)
    public void noTimestampTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", "10.00");
        Transaction.createFromPayload(payload);
    }

    @Test(expected = InvalidRequestException.class)
    public void wrongPayloadTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", "10.00");
        payload.put("timestamp", Instant.now().toString());
        payload.put("additional_param", "");
        Transaction.createFromPayload(payload);
    }


    @Test(expected = InvalidTransaction.class)
    public void futureTransactionTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", "10.00");
        payload.put("timestamp", Instant.now().plusSeconds(10).toString());
        Transaction.createFromPayload(payload);
    }

    @Test(expected = OldTransactionException.class)
    public void oldTransactionTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", "10.00");
        payload.put("timestamp", Instant.now().minusSeconds(100).toString());
        Transaction.createFromPayload(payload);
    }


    @Test(expected = InvalidTransaction.class)
    public void wrongAmountTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", "x10.00");
        payload.put("timestamp", Instant.now().toString());
        Transaction.createFromPayload(payload);
    }

    @Test(expected = InvalidTransaction.class)
    public void wrongTimestampTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", "10.00");
        payload.put("timestamp", "xxx");
        Transaction.createFromPayload(payload);
    }

    @Test(expected = InvalidTransaction.class)
    public void negativeTransactionTest() throws InvalidTransaction, OldTransactionException, InvalidRequestException {
        HashMap<String, String> payload = new HashMap<>();
        payload.put("amount", "-10.00");
        payload.put("timestamp", Instant.now().toString());
        Transaction.createFromPayload(payload);
    }
}
