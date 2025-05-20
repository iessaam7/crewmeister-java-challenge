package com.crewmeister.cmcodingchallenge.mapper;

import com.crewmeister.cmcodingchallenge.dto.ConvertedExchangeRateResponseDto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ConvertedExchangeRateMapper {

    public static ConvertedExchangeRateResponseDto mapToDto(String fromCurrency,
                                                            String toCurrency,
                                                            LocalDate date,
                                                            BigDecimal originalAmount,
                                                            BigDecimal convertedAmount) {
        return new ConvertedExchangeRateResponseDto(
                fromCurrency,
                toCurrency,
                date,
                originalAmount,
                convertedAmount
        );
    }
}
