package com.izpan.infrastructure.jackson.module;

import cn.hutool.core.date.DatePattern;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.PackageVersion;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;

import java.io.IOException;
import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * JAVA 8 时间默认序列化处理
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.infrastructure.jackson.module.CustomJavaTimeModule
 * @CreateTime 2024/4/20 - 13:19
 */
public class CustomJavaTimeModule extends SimpleModule {

    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_DATETIME_PATTERN);
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_DATE_PATTERN);
    public static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN);
    public static final DateTimeFormatter TIME_SHORT_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Serial
    private static final long serialVersionUID = 4275011080066438360L;

    public CustomJavaTimeModule() {
        super(PackageVersion.VERSION);

        this.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(DATE_TIME_FORMATTER));
        this.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(DATE_TIME_FORMATTER));

        this.addSerializer(LocalDate.class, new LocalDateSerializer(DATE_FORMATTER));
        this.addDeserializer(LocalDate.class, new LocalDateDeserializer(DATE_FORMATTER));

        this.addSerializer(LocalTime.class, new LocalTimeSerializer(TIME_FORMATTER));
        this.addDeserializer(LocalTime.class, new CustomLocalTimeDeserializer());
    }

    private static class CustomLocalTimeDeserializer extends JsonDeserializer<LocalTime> {
        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            String value = p.getValueAsString();
            if (value == null || value.isEmpty()) {
                return null;
            }
            try {
                return LocalTime.parse(value, TIME_FORMATTER);
            } catch (DateTimeParseException e) {
                try {
                    return LocalTime.parse(value, TIME_SHORT_FORMATTER);
                } catch (DateTimeParseException e2) {
                    throw new IOException("Failed to parse LocalTime: " + value, e2);
                }
            }
        }
    }
}
