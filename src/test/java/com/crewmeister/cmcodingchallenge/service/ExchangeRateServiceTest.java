package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.dto.ConvertedExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.dto.ExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateServiceTest {

    @Mock
    private ExchangeRateRepository exchangeRateRepository;

    @InjectMocks
    private ExchangeRateServiceImpl exchangeRateService;

    private final LocalDate testDate = LocalDate.now();
    private final ExchangeRateEntity validRate =
            new ExchangeRateEntity("EUR", "USD", testDate, new BigDecimal("1.2"), LocalDateTime.now());

    @Test
    void getAllEuroFxRates_throwsWhenNoRatesFound() {
        when(exchangeRateRepository.findByBaseCurrency(eq("EUR"), any(Pageable.class)))
                .thenReturn(Page.empty());

        assertThrows(ResponseStatusException.class, () ->
                exchangeRateService.getAllEuroFxRates(0, 10));
    }

    @Test
    void getEuroFxRateAtDate_returnsValidRate() {
        when(exchangeRateRepository.findByBaseCurrencyAndQuoteCurrencyAndDate(
                "EUR", "USD", testDate))
                .thenReturn(Optional.of(validRate));

        ExchangeRateResponseDto result =
                exchangeRateService.getEuroFxRateAtDate(testDate, "USD");
        assertEquals(new BigDecimal("1.2"), result.getExchangeRate());
    }

    @Test
    void getEuroFxRateAtDate_throwsWhenRateNotFound() {
        when(exchangeRateRepository.findByBaseCurrencyAndQuoteCurrencyAndDate(
                "EUR", "JPY", testDate))
                .thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () ->
                exchangeRateService.getEuroFxRateAtDate(testDate, "JPY"));
    }

    @Test
    void convertToEuro_convertsCorrectly() {
        when(exchangeRateRepository.findByBaseCurrencyAndQuoteCurrencyAndDate(
                "EUR", "USD", testDate))
                .thenReturn(Optional.of(validRate));

        ConvertedExchangeRateResponseDto result =
                exchangeRateService.convertToEuro("USD", testDate, new BigDecimal("120"));
        assertEquals(new BigDecimal("100.00"), result.getConvertedRate()); // 120 / 1.2 = 100
    }

    @Test
    void convertToEuro_handlesEURtoEUR() {
        ConvertedExchangeRateResponseDto result =
                exchangeRateService.convertToEuro("EUR", testDate, new BigDecimal("100"));
        assertEquals(new BigDecimal("100.00"), result.getConvertedRate());
    }
}
