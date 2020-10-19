package com.n26.controllers;

import com.n26.services.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Autowired
    public StatisticsController(final StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }


    /**
     * Get recent statistics
     * @return
     */
    @GetMapping("/statistics")
    public Map<String, Object> statistics() {
        return statisticsService.getStatistics();
    }

}
