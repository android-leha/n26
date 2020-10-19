package com.n26.controllers;

import com.n26.services.TransactionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StatisticsController {

    private final TransactionService transactionService;


    public StatisticsController(final TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Get recent statistics
     * @return
     */
    @GetMapping("/statistics")
    public Map<String, Object> statistics() {
        return transactionService.getStatistics().getAsMap();
    }

}
