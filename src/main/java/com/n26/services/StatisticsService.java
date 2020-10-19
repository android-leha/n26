package com.n26.services;

import com.n26.models.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentNavigableMap;

@Service
public class StatisticsService {

    private volatile DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();


    public synchronized void add(final Transaction transaction) {
        DoubleSummaryStatistics statistics = new DoubleSummaryStatistics();
        statistics.accept(transaction.getAmountAsDouble());
        statistics.combine(this.statistics);
        this.statistics = statistics;
    }


    public synchronized void updateStatistics(final ConcurrentNavigableMap<Instant, Transaction> latest) {
        statistics = latest.values().stream().mapToDouble(Transaction::getAmountAsDouble).summaryStatistics();
    }

    public synchronized void cleanStatistics() {
        statistics = new DoubleSummaryStatistics();
    }


    public Map<String, Object> getStatistics() {
        DoubleSummaryStatistics statistics = this.statistics;

        Map<String, Object> result = new LinkedHashMap<>();
        if (statistics.getCount() > 0) {
            result.put("sum", BigDecimal.valueOf(statistics.getSum()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            result.put("avg", BigDecimal.valueOf(statistics.getAverage()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            result.put("max", BigDecimal.valueOf(statistics.getMax()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            result.put("min", BigDecimal.valueOf(statistics.getMin()).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
            result.put("count", statistics.getCount());
        } else {
            result.put("sum", "0.00");
            result.put("avg", "0.00");
            result.put("max", "0.00");
            result.put("min", "0.00");
            result.put("count", 0);
        }
        return result;
    }

}
