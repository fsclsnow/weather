package com.example.search.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

public class EndpointConfig {
    public static final String queryWeatherByCity = "https://www.metaweather.com/api/location/search/?query=";
    public static final String queryWeatherById = "https://www.metaweather.com/api/location/";
    public static final String detail_service = "http://detail-service/?city=";
}
