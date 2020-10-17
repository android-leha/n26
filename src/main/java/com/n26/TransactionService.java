package com.n26;

import com.n26.exceptions.InvalidRequestException;
import com.n26.exceptions.InvalidTransaction;
import com.n26.exceptions.OldTransactionException;
import com.n26.model.Statistics;
import com.n26.model.Transaction;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

@Service
public class TransactionService {

    /**
     * Statistics period in seconds
     */
    public static final int PERIOD = 60;

    /**
     * List of transactions
     */
    private LinkedList<Transaction> transactionList = new LinkedList<>();

    /**
     * Statistics container
     */
    private Statistics statistics = new Statistics();


    /**
     * Schedule adding transaction
     * Need static, because @Async should be called from outsid
     *
     * @param transactionService
     * @param payload
     * @return milliseconds between transaction and now
     * @throws InvalidTransaction
     * @throws OldTransactionException
     * @throws InvalidRequestException
     */
    static public long add(final TransactionService transactionService, final HashMap<String, String> payload)
            throws InvalidTransaction, OldTransactionException, InvalidRequestException {

        Transaction transaction = Transaction.createFromPayload(payload);

        Instant now = Instant.now();
        // Skip Future
        if (transaction.getTimestamp().compareTo(now) > 0) {
            throw new InvalidTransaction("Future transaction");
        }

        long diff = Duration.between(now, transaction.getTimestamp()).toMillis();

        // Ignore old
        if (transaction.getTimestamp().compareTo(now.minusSeconds(PERIOD)) < 0) {
            throw new OldTransactionException(diff);
        }

        transactionService.add(transaction);
        return diff;
    }

    /**
     * Add transaction asynchronously
     * @param transaction
     */
    @Async
    public synchronized void add(final Transaction transaction) {

        long start = System.nanoTime();

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

        System.out.println(System.nanoTime() - start);

    }

    /**
     * Remove old transactions
     */
    @Scheduled(fixedDelay = 1)
    protected synchronized void removeOutdated() {
        Instant start = Instant.now().minusSeconds(PERIOD);

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
                if (count == 0) {
                    min = transaction.getAmount();
                    max = transaction.getAmount();
                } else {
                    min = min.min(transaction.getAmount());
                    max = max.max(transaction.getAmount());
                }
            }
        }

        this.statistics = new Statistics(sum, min, max, count);

    }

    /**
     * Remove all transactions
     */
    public synchronized void clean() {
        transactionList = new LinkedList<>();
        statistics = new Statistics();
    }

    /**
     * Return statistic
     * @return
     */
    public Map<String, Object> statistics() {
        return statistics.getAsMap();
    }
}
