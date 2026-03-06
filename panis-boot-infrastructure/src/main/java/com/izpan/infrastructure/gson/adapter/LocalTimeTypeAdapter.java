package com.izpan.infrastructure.gson.adapter;

import cn.hutool.core.date.DatePattern;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * LocalTime 类型适配器
 *
 * @Author payne.zhuang <paynezhuang@gmail.com>
 * @ProjectName panis-boot
 * @ClassName com.izpan.infrastructure.gson.adapter.LocalTimeTypeAdapter
 * @CreateTime 2024/4/27 - 19:56
 */
public class LocalTimeTypeAdapter implements JsonSerializer<LocalTime>, JsonDeserializer<LocalTime> {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern(DatePattern.NORM_TIME_PATTERN);
    private static final DateTimeFormatter TIME_SHORT_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Override
    public JsonElement serialize(final LocalTime time, final Type typeOfSrc,
                                 final JsonSerializationContext context) {
        return new JsonPrimitive(time.format(TIME_FORMATTER));
    }

    @Override
    public LocalTime deserialize(final JsonElement json, final Type typeOfT,
                                 final JsonDeserializationContext context) throws JsonParseException {
        String value = json.getAsString();
        try {
            return LocalTime.parse(value, TIME_FORMATTER);
        } catch (DateTimeParseException e) {
            return LocalTime.parse(value, TIME_SHORT_FORMATTER);
        }
    }
}
