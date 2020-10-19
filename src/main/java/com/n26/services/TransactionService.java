package com.n26.services;

import com.n26.models.Statistics;
import com.n26.models.Transaction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Iterator;
import java.util.LinkedList;

@Service
public class TransactionService {

    /**
     * Statistics period in seconds
     */
    public static final int PERIOD = 60000;

    /**
     * List of transactions
     */
    private LinkedList<Transaction> transactionList = new LinkedList<>();

    private Statistics statistics = new Statistics();

    /**
     * Add transaction asynchronously
     * @param transaction
     */
    @Async
    public synchronized void add(final Transaction transaction) {
        transactionList.add(transaction);
        Statistics statistics = this.statistics;

        BigDecimal sum = statistics.getSum();
        BigDecimal max = statistics.getMax();
        BigDecimal min = statistics.getMin();
        long count = statistics.getCount();


        if (count++ == 0) {
            min = transaction.getAmount();
            max = transaction.getAmount();
            sum = transaction.getAmount();
        } else {
            min = min.min(transaction.getAmount());
            max = max.max(transaction.getAmount());
            sum = sum.add(transaction.getAmount());
        }

        this.statistics = new Statistics(sum, min, max, count);
    }


    /**
     * Refresh statistic
     * Delay parameter should be defined depends on requirements of accuracy/performance
     */
    @Scheduled(fixedDelay = 1)
    public synchronized void refreshStatistics() {
        Instant start = Instant.now().minusMillis(PERIOD);

        Iterator<Transaction> iterator = transactionList.iterator();

        Statistics statistics = this.statistics;

        BigDecimal sum = statistics.getSum();
        BigDecimal max = statistics.getMax();
        BigDecimal min = statistics.getMin();
        long count = statistics.getCount();

        while (iterator.hasNext()) {
            Transaction transaction = iterator.next();
            if (transaction.getTimestamp().compareTo(start) < 0) {
                iterator.remove();
                count--;
                sum = sum.subtract(transaction.getAmount());
            } else {
                min = min.min(transaction.getAmount());
                max = max.max(transaction.getAmount());
            }
        }
        // Update statistics object
        this.statistics = new Statistics(sum, min, max, count);
    }

    /**
     * Remove all transactions and clear statistics
     */
    public synchronized void clean() {
        transactionList.clear();
        statistics = new Statistics();
    }

    /**
     * Return cached statistics
     * @return
     */
    public Statistics getStatistics() {
        return statistics;
    }
}
