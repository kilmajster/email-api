package com.jvmops.api.emails;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
@EnableConfigurationProperties({Properties.class})
@Slf4j
public class Config {

    @Autowired
    private Properties properties;

    @Bean
    public Clock clock() {
        return Clock.system(ZoneId.of(properties.getTimeOffset()));
    }
}
