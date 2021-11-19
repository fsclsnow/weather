package com.example.search.controller;

import com.example.search.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
public class WeatherController {

    private final WeatherService weatherService;

    @Value("${server.port}")
    private int randomServerPort;

    @Autowired
    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/weather/list")
    public ResponseEntity<?> queryWeatherByCityList(@RequestParam(required = true) List<String> cities) {
        return new ResponseEntity<>(weatherService.findWeatherByNameList(cities), HttpStatus.OK);
    }

    @GetMapping("/weather")
    public ResponseEntity<?> queryWeatherByCity(@RequestParam(required = true) String city) {
        return new ResponseEntity<>(weatherService.findWeatherByName(city), HttpStatus.OK);
    }

    @GetMapping("/weather/{id}")
    public ResponseEntity<?> queryWeatherByCity(@PathVariable int id) {
        return new ResponseEntity<Map>(weatherService.findCityNameById(id), HttpStatus.OK);
    }

    @GetMapping("/weather/port")
    public ResponseEntity<?> queryWeatherByCity() {
        return new ResponseEntity<>("weather service + " + randomServerPort, HttpStatus.OK);
    }
}
