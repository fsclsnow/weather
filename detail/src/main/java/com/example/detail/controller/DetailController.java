package com.example.detail.controller;

import com.example.detail.service.GetCityIdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DetailController {

    private final GetCityIdService getCityIdService;

    @Value("${server.port}")
    private int serverPort;

    public DetailController(GetCityIdService getCityIdService){
        this.getCityIdService = getCityIdService;
    }

    @GetMapping("/detail/port")
    public ResponseEntity<?> getDetails() {
        return new ResponseEntity<>("detail service port is " + serverPort, HttpStatus.OK);
    }

    @GetMapping("")
    public ResponseEntity<?> queryIDByCity(@RequestParam(required = true) String city) {
        return new ResponseEntity<>(getCityIdService.findCityIdByName(city), HttpStatus.OK);
    }
}
