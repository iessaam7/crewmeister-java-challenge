package com.crewmeister.cmcodingchallenge.client;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;
import com.crewmeister.cmcodingchallenge.exception.ParseException;
import com.crewmeister.cmcodingchallenge.utils.ApiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ExchangeRateCsvParser {
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateCsvParser.class);

    // Expected date format
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    public Stream<ExchangeRateEntity> convertCsv(Stream<String> csvLines) {
        if (csvLines == null) {
            return Stream.empty();
        }

        List<String> lines = csvLines.collect(Collectors.toList());
        if (lines.isEmpty()) {
            return Stream.empty();
        }

        // Parse header to get column indices
        String[] headers = lines.get(0).split(";");
        Map<String, Integer> columnIndices = mapColumnIndices(headers);

        return lines.stream()
                .skip(1) // Skip header row
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .map(line -> line.split(";", -1))
                .map(cols -> parseRate(cols, columnIndices))
                .filter(Objects::nonNull);
    }

    private Map<String, Integer> mapColumnIndices(String[] headers) {
        Map<String, Integer> indices = new HashMap<>();
        for (int i = 0; i < headers.length; i++) {
            indices.put(headers[i].trim(), i);
        }
        return indices;
    }

    private ExchangeRateEntity parseRate(String[] columns, Map<String, Integer> columnIndices) {
        try {
            String quoteCurrency = getColumnValue(columns, columnIndices, ApiConstants.ExchangeRateAPI.QUOTE_CURRENCY_LABEL);
            String baseCurrency = getColumnValue(columns, columnIndices, ApiConstants.ExchangeRateAPI.BASE_CURRENCY_LABEL);
            LocalDate date = parseDate(getColumnValue(columns, columnIndices, ApiConstants.ExchangeRateAPI.DATE_LABEL));
            BigDecimal exchangeRate = parseRateValue(getColumnValue(columns, columnIndices, ApiConstants.ExchangeRateAPI.RATE_LABEL));

            return new ExchangeRateEntity(baseCurrency, quoteCurrency, date, exchangeRate, LocalDateTime.now());
        } catch (ParseException e) {
            log.warn("Failed to parse CSV row: {}", Arrays.toString(columns), e);
            return null;
        } catch (IllegalArgumentException e) {
            log.warn("Missing required column in CSV header: {}", e.getMessage());
            return null;
        }
    }

    private String getColumnValue(String[] columns, Map<String, Integer> columnIndices, String columnLabel) {
        Integer index = columnIndices.get(columnLabel);
        if (index == null) {
            throw new IllegalArgumentException("Column not found: " + columnLabel);
        }
        if (index >= columns.length) {
            return "";
        }
        return columns[index].trim();
    }

    private LocalDate parseDate(String dateString) throws ParseException {
        if (dateString.isEmpty()) {
            throw new ParseException("Date string is empty");
        }
        try {
            return LocalDate.parse(dateString, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new ParseException("Invalid date format: " + dateString);
        }
    }

    private BigDecimal parseRateValue(String rateStr) throws ParseException {
        if (rateStr.isEmpty() || ".".equals(rateStr)) {
            return null;
        }

        try {
            return new BigDecimal(rateStr);
        } catch (NumberFormatException e) {
            throw new ParseException("Invalid rate format: " + rateStr);
        }
    }
}
