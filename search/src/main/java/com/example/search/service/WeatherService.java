package com.example.search.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Service
public interface WeatherService {
    Map<String, Map> findCityNameById(int id);
    List<Integer> findCityIdByName(String city);
    Map<String, Map> findWeatherByName(String city);
}
