package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.dto.ConvertedExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.dto.ExchangeRateResponseDto;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExchangeRateService {
    Page<ExchangeRateResponseDto> getAllEuroFxRates(int page, int size);
    ExchangeRateResponseDto getEuroFxRateAtDate(LocalDate date, String targetCurrency);
    ConvertedExchangeRateResponseDto convertToEuro(String fromCurrency, LocalDate date, BigDecimal amount);

}
