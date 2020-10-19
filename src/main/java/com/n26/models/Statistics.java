package com.n26.models;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class Statistics {

    private BigDecimal sum = new BigDecimal(0);
    private BigDecimal min = new BigDecimal(0);
    private BigDecimal max = new BigDecimal(0);
    private long count = 0;


    public Statistics() {}

    public Statistics(final BigDecimal sum, final BigDecimal min, final BigDecimal max, final long count) {
        this.sum = sum;
        this.min = min;
        this.max = max;
        this.count = count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public BigDecimal getMax() {
        return max;
    }

    public BigDecimal getMin() {
        return min;
    }

    public long getCount() {
        return count;
    }

    /**
     * Create statistics Map for output
     * @return
     */
    public Map<String, Object> getAsMap() {
        Map<String, Object> result = new LinkedHashMap<>();
        if (count > 0) {
            result.put("sum", sum.toString());
            result.put("avg", sum.divide(new BigDecimal(count), BigDecimal.ROUND_HALF_UP).toString());
            result.put("max", max.toString());
            result.put("min", min.toString());
            result.put("count", count);
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
