package com.hailang.config.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

public class BeanUtils {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static <T> T copy(Object source, Class<T> targetClass) {
        if (source == null) {
            return null;
        }
        return MAPPER.convertValue(source, targetClass);
    }

    public static <T> List<T> copyList(List<?> source, Class<T> targetClass) {
        if (source == null) {
            return Collections.emptyList();
        }
        return source.stream()
                .map(item -> MAPPER.convertValue(item, targetClass))
                .toList();
    }
}
