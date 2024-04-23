package com.example.qisynthbanking.configs;

import kong.unirest.JsonObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class BeansConfig {

    @Bean
    public JsonObjectMapper jsonObjectMapper(){
        return new JsonObjectMapper();
    }

}
