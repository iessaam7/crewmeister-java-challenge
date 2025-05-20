package com.crewmeister.cmcodingchallenge.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConvertedExchangeRateResponseDto {
    private String baseCurrency;
    private String quoteCurrency;
    private LocalDate date;
    private BigDecimal exchangeRate;
    private BigDecimal convertedRate;
}
