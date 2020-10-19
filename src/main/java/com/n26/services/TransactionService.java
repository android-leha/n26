package com.n26.services;

import com.n26.models.Transaction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Comparator;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;

@Service
public class TransactionService {

    /**
     * Statistics period in seconds
     */
    public static final int PERIOD = 60000;

    /**
     * List of transactions
     */
    private ConcurrentSkipListMap<Instant, Transaction> transactionList = new ConcurrentSkipListMap<>(Comparator.comparingLong(Instant::toEpochMilli));

    private final StatisticsService statisticsService;

    public TransactionService(final StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    /**
     * Add transaction asynchronously
     * @param transaction
     */
    @Async
    public synchronized void add(final Transaction transaction) {
        transactionList.put(transaction.getTimestamp(), transaction);
        statisticsService.add(transaction);
    }

    /**
     * Remove old transactions
     */
    @Scheduled(fixedRate = 500)
    public void removeOutdated() {
        transactionList.headMap(Instant.now().minusMillis(PERIOD)).clear();
    }

    @Scheduled(fixedDelay = 1)
    public synchronized void refreshStatistics() {
        statisticsService.updateStatistics(getLatest());
    }

    /**
     * Remove all transactions
     */
    public void clean() {
        transactionList.clear();
        statisticsService.cleanStatistics();
    }


    public ConcurrentNavigableMap<Instant, Transaction> getLatest() {
        return transactionList.tailMap(Instant.now().minusMillis(PERIOD));
    }

}
