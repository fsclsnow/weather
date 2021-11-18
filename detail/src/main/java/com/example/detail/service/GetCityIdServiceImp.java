package com.example.detail.service;
import com.example.detail.config.EndpointConfig;
import com.example.detail.pojo.City;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import rx.internal.schedulers.CachedThreadScheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class GetCityIdServiceImp implements GetCityIdService {
    private final RestTemplate restTemplate;

    @Autowired
    public GetCityIdServiceImp(RestTemplate getCityIdTemplate) {
        this.restTemplate = getCityIdTemplate;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public Integer findCityIdByName(String city) {
        City[] cities = restTemplate.getForObject(EndpointConfig.queryWeatherByCity + city, City[].class);
        List<Integer> ans = new ArrayList<>();
        for(City c: cities) {
            if(c != null && c.getWoeid() != null) {
                ans.add(c.getWoeid());
            }
        }
        return ans.get(0);
    }
}
