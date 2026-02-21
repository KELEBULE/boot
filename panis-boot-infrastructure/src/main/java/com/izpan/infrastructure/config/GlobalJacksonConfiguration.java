package com.izpan.infrastructure.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.util.StdDateFormat;
import com.izpan.infrastructure.jackson.module.*;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.util.Locale;
import java.util.TimeZone;

@Configuration
public class GlobalJacksonConfiguration {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder ->
                builder
                        .dateFormat(new StdDateFormat().withColonInTimeZone(true))
                        .locale(Locale.CHINA)
                        .timeZone(TimeZone.getTimeZone(ZoneId.systemDefault()))
                        .serializationInclusion(JsonInclude.Include.ALWAYS)
                        .propertyNamingStrategy(PropertyNamingStrategies.LOWER_CAMEL_CASE)

                        .featuresToEnable(
                                JsonParser.Feature.ALLOW_SINGLE_QUOTES,
                                JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES,
                                JsonParser.Feature.IGNORE_UNDEFINED,
                                JsonGenerator.Feature.AUTO_CLOSE_TARGET,
                                SerializationFeature.WRITE_ENUMS_USING_TO_STRING,
                                DeserializationFeature.READ_ENUMS_USING_TO_STRING,
                                DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY
                        )

                        .featuresToDisable(
                                SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,
                                SerializationFeature.FAIL_ON_EMPTY_BEANS,
                                DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES
                        )

                        .modulesToInstall(
                                new CustomJavaTimeModule(),
                                new LocalDateTimeModule(),
                                new NullSerializersModule(),
                                new LongAsStringModule(),
                                new BigDecimalAsStringModule());

    }

}
