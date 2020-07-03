package com.jvmops.api.emails.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.time.Clock;
import java.time.ZoneId;
import java.util.List;

@EnableWebMvc
@Configuration
@EnableConfigurationProperties({ApiProperties.class, JacksonProperties.class})
@Slf4j
class Api implements WebMvcConfigurer {

    @Autowired
    private ApiProperties apiProperties;

    @Autowired
    private JacksonProperties jacksonProperties;

    @Bean
    Clock clock() {
        return Clock.system(ZoneId.of(apiProperties.getTimeOffset()));
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        var mapper = new Jackson2ObjectMapperBuilder()
                .serializerByType(ObjectId.class, new ToStringSerializer())
                .serializationInclusion(jacksonProperties.getDefaultPropertyInclusion())
                .build();
        converters.add(new MappingJackson2HttpMessageConverter(mapper));
    }
}
