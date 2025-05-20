package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.dto.ConvertedExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.dto.ExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.exception.GlobalExceptionHandler;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ExchangeRateControllerTest {

    @Mock
    private ExchangeRateServiceImpl exchangeRateService;

    @InjectMocks
    private ExchangeRateController exchangeRateController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(exchangeRateController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }


    private void initializeDataForConvertToEuro() {
        ConvertedExchangeRateResponseDto response = new ConvertedExchangeRateResponseDto(
                "EUR",
                "USD",
                LocalDate.of(2023, 1, 1),
                new BigDecimal("100"),
                new BigDecimal("85.50")
        );

        when(exchangeRateService
                .convertToEuro(eq("USD"), eq(LocalDate.of(2023, 1, 1)), any(BigDecimal.class)))
                .thenReturn(response);

    }

    @Test
    void convertToEuro_shouldReturn200AndConversionResult() throws Exception {
        initializeDataForConvertToEuro();
        ResultActions result = mockMvc.perform(get("/api/exchange-rates/convert")
                .param("quoteCurrency", "USD")
                .param("amount", "100")
                .param("date", "2023-01-01"));

        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteCurrency").value("USD"))
                .andExpect(jsonPath("$.date[0]").value("2023"))
                .andExpect(jsonPath("$.date[1]").value("1"))
                .andExpect(jsonPath("$.date[2]").value("1"))
                .andExpect(jsonPath("$.exchangeRate").value(100))
                .andExpect(jsonPath("$.convertedRate").value(85.50));
    }

    @Test
    void convertToEuro_shouldReturn500WhenCurrencyInvalid() throws Exception {
        initializeDataForConvertToEuro();
        mockMvc.perform(get("/api/exchange-rates/convert")
                        .param("quoteCurrency", "US") // Too short
                        .param("amount", "100")
                        .param("date", "2023-01-01"))
                .andExpect(status().isInternalServerError());
    }


    // Test cases for getAllRates endpoint
    private void initializeDataForGetAllRates() {
        ExchangeRateResponseDto rate = new ExchangeRateResponseDto(
                "EUR",
                "USD",
                LocalDate.of(2023, 1, 1),
                new BigDecimal("1.1234")
        );
        Page<ExchangeRateResponseDto> page = new PageImpl<>(List.of(rate));

        when(exchangeRateService.getAllEuroFxRates(0, 10)).thenReturn(page);
    }

    @Test
    void getAllRates_shouldReturn200AndPagedResults() throws Exception {
        initializeDataForGetAllRates();
        mockMvc.perform(get("/api/exchange-rates")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].quoteCurrency").value("USD"))
                .andExpect(jsonPath("$.content[0].date[0]").value("2023"))
                .andExpect(jsonPath("$.content[0].date[1]").value("1"))
                .andExpect(jsonPath("$.content[0].date[2]").value("1"))
                .andExpect(jsonPath("$.content[0].exchangeRate").value(1.1234));
    }


    @Test
    void getAllRates_shouldReturn500WhenPageNegative() throws Exception {
        initializeDataForGetAllRates();
        mockMvc.perform(get("/api/exchange-rates")
                        .param("page", "-1")
                        .param("size", "10"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void getAllRates_shouldReturn500WhenSizeTooLarge() throws Exception {
        initializeDataForGetAllRates();
        mockMvc.perform(get("/api/exchange-rates")
                        .param("page", "0")
                        .param("size", "101")) // Max is 100
                .andExpect(status().isInternalServerError());
    }

    // Test cases for getRateForDate endpoint

    private void intializeDataForGetRateForDate() {
        ExchangeRateResponseDto response = new ExchangeRateResponseDto(
                "EUR",
                "USD",
                LocalDate.of(2023, 1, 1),
                new BigDecimal("1.1234")
        );

        when(exchangeRateService.getEuroFxRateAtDate(LocalDate.of(2023, 1, 1), "USD"))
                .thenReturn(response);
    }

    @Test
    void getRateForDate_shouldReturn200AndRate() throws Exception {
        intializeDataForGetRateForDate();
        mockMvc.perform(get("/api/exchange-rates/rate")
                        .param("quoteCurrency", "USD")
                        .param("date", "2023-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quoteCurrency").value("USD"))
                .andExpect(jsonPath("$.date[0]").value("2023"))
                .andExpect(jsonPath("$.date[1]").value("1"))
                .andExpect(jsonPath("$.date[2]").value("1"))
                .andExpect(jsonPath("$.exchangeRate").value(1.1234));
    }

    @Test
    void getRateForDate_shouldReturn404WhenNotFound() throws Exception {
        when(exchangeRateService.getEuroFxRateAtDate(any(), any()))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(get("/api/exchange-rates/rate")
                        .param("quoteCurrency", "USD")
                        .param("date", "2023-01-01"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getRateForDate_shouldReturn500WhenCurrencyInvalid() throws Exception {
        intializeDataForGetRateForDate();
        mockMvc.perform(get("/api/exchange-rates/rate")
                        .param("quoteCurrency", "US1") // Invalid format
                        .param("date", "2023-01-01"))
                .andExpect(status().isInternalServerError());
    }

}