package com.yun.hello.service.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class WeatherDataCollectionServiceImpl implements WeatherDataCollectionService {

    private static String WEATHER_URI="http://wthrcdn.etouch.cn/weather_mini?";
    private static final Long TIME_OUT = 1800L;//30 minutes

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void syncDataByCityId(String cityId) {
        String uri = WEATHER_URI +"citykey="+ cityId;

        this.saveWeatherData(uri);
    }

    private void saveWeatherData(String uri) {
        String key = uri;
        String resBody= null;

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(uri,String.class);
        if(responseEntity.getStatusCodeValue() == 200){
            resBody = responseEntity.getBody();
        }

        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        // 数据写入缓存
        ops.set(key,resBody,TIME_OUT, TimeUnit.SECONDS);
    }

}
