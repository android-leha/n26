package com.n26.services;

import com.n26.exceptions.InvalidTransaction;
import com.n26.models.Transaction;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.time.Instant;

import static org.junit.Assert.*;

public class TransactionServiceTest {

    @Test
    public void addAndCleanTest() throws InvalidTransaction, InterruptedException {
        TransactionService transactionService = new TransactionService();

        // Add normal
        transactionService.add(new Transaction("10.103", Instant.now().minusSeconds(10).toString()));
        Assert.assertEquals(transactionService.getStatistics().getCount(), 1);
        Assert.assertEquals(transactionService.getStatistics().getSum(), new BigDecimal("10.10"));
        Assert.assertEquals(transactionService.getStatistics().getMax(), new BigDecimal("10.10"));
        Assert.assertEquals(transactionService.getStatistics().getMin(), new BigDecimal("10.10"));
        // Add normal
        transactionService.add(new Transaction("5.103", Instant.now().minusSeconds(10).toString()));
        Assert.assertEquals(transactionService.getStatistics().getCount(), 2);
        Assert.assertEquals(transactionService.getStatistics().getSum(), new BigDecimal("15.20"));
        Assert.assertEquals(transactionService.getStatistics().getMax(), new BigDecimal("10.10"));
        Assert.assertEquals(transactionService.getStatistics().getMin(), new BigDecimal("5.10"));
        // Add close to old
        transactionService.add(new Transaction("10.103", Instant.now().minusSeconds(57).toString()));
        Assert.assertEquals(transactionService.getStatistics().getCount(), 3);
        Assert.assertEquals(transactionService.getStatistics().getSum(), new BigDecimal("25.30"));
        Assert.assertEquals(transactionService.getStatistics().getMax(), new BigDecimal("10.10"));
        Assert.assertEquals(transactionService.getStatistics().getMin(), new BigDecimal("5.10"));

        Thread.sleep(5000);
        transactionService.refreshStatistics();

        Assert.assertEquals(transactionService.getStatistics().getCount(), 2);
        Assert.assertEquals(transactionService.getStatistics().getSum(), new BigDecimal("15.20"));
        Assert.assertEquals(transactionService.getStatistics().getMax(), new BigDecimal("10.10"));
        Assert.assertEquals(transactionService.getStatistics().getMin(), new BigDecimal("5.10"));

        transactionService.clean();
        Assert.assertEquals(transactionService.getStatistics().getCount(), 0);
        Assert.assertEquals(transactionService.getStatistics().getSum(), new BigDecimal("0.00"));
        Assert.assertEquals(transactionService.getStatistics().getMax(), new BigDecimal("0.00"));
        Assert.assertEquals(transactionService.getStatistics().getMin(), new BigDecimal("0.00"));

        transactionService.refreshStatistics();
        Assert.assertEquals(transactionService.getStatistics().getCount(), 0);
        Assert.assertEquals(transactionService.getStatistics().getSum(), new BigDecimal("0.00"));
        Assert.assertEquals(transactionService.getStatistics().getMax(), new BigDecimal("0.00"));
        Assert.assertEquals(transactionService.getStatistics().getMin(), new BigDecimal("0.00"));
    }


}
