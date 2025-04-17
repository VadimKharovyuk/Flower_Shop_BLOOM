package com.example.flowershoptr.util;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Component
public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Override
    public LocalDateTime convert(String source) {
        if (source == null || source.trim().isEmpty()) {
            return null;
        }

        try {
            return LocalDateTime.parse(source.trim(), FORMATTER);
        } catch (DateTimeParseException e) {
            // Попробуем другие форматы, если основной не сработал
            try {
                return LocalDateTime.parse(source.trim(), DateTimeFormatter.ISO_DATE_TIME);
            } catch (DateTimeParseException ex) {
                throw new IllegalArgumentException("Не удалось преобразовать строку в дату и время: " + source, ex);
            }
        }
    }
}