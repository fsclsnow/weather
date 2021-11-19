package com.example.search.service;


import com.example.search.config.EndpointConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;

@Service
public class WeatherServiceImpl implements WeatherService{
    private final RestTemplate restTemplate;
    private final ExecutorService es;
    private final RestTemplate APIRestTemplate;

    @Autowired
    public WeatherServiceImpl(RestTemplate getRestTemplate, RestTemplate getAPIRestTemplate, ExecutorService getExecutorService) {
        this.restTemplate = getRestTemplate;
        this.es = getExecutorService;
        this.APIRestTemplate = getAPIRestTemplate;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public List<Map<String, Map>>findWeatherByNameList(List<String> cities) {
        List<Map<String, Map>> res = new ArrayList<>();
        List<CompletableFuture<?>> future = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        for (String city: cities) {
            ids.add(findCityIdByName(city));
        }
        for (Integer id: ids) {
            CompletableFuture<?> f = CompletableFuture.supplyAsync(() -> {
                        return APIRestTemplate.getForObject(EndpointConfig.queryWeatherById + id, HashMap.class);
                    }, es).thenApplyAsync(w -> res.add(w));
            future.add(f);
        }
        return res;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public Map<String, Map>findWeatherByName(String city) {
        Integer id = findCityIdByName(city);
        Map<String, Map> ans = APIRestTemplate.getForObject(EndpointConfig.queryWeatherById + id, HashMap.class);
        return ans;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public Map<String, Map> findCityNameById(int id) {
        Map<String, Map> ans = APIRestTemplate.getForObject(EndpointConfig.queryWeatherById + id, HashMap.class);
        return ans;
    }

    @Override
    @Retryable(include = IllegalAccessError.class)
    public Integer findCityIdByName (String city) {
        return restTemplate.getForObject((EndpointConfig.detail_service + city),Integer.class);
    }
}


/**
 *  -> gateway -> eureka
 *       |
 *   weather-search -> hystrix(thread pool) -> 3rd party weather api
 *
 *
 *  circuit breaker(hystrix)
 * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *  * *
 *   weather-search service should get city id from detail service
 *   and use multi-threading to query city's weather details
 *
 *   gateway
 *     |
 *  weather-service -> 3rd party api(id <-> weather)
 *    |
 *  detail-service -> 3rd party api (city <-> id)
 *
 *  failed situations:
 *      1. 3rd party api timeout -> retry + hystrix
 *      2. 3rd party api available time / rate limit
 *      3. security verification
 *  response
 *      1. no id -> error / empty
 *      2. large response -> pagination / file download (link / email)
 *  performance
 *      1. cache / db
 *
 *   gateway
 *     |
 *  weather-service -> cache(city - id - weather) (LFU)
 *    |
 *   DB (city - id - weather) <-> service <->  message queue  <-> scheduler <-> 3rd party api(city - id)
 *                                                                  |
 *                                                         update id - weather every 30 min
 *                                                         update city - id relation once per day
 *
 *  homework :
 *      deadline -> Wednesday midnight
 *      1. update detail service
 *          a. send request to 3rd party api -> get id by city
 *      2. update search service
 *          a. add ThreadPool
 *          b. send request to detail service -> get id by city
 *          c. use CompletableFuture send request to 3rd party api -> get weather by ids
 *          d. add retry feature
 */