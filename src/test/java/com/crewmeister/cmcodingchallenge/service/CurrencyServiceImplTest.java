package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private ExchangeRateRepository rateRepository;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    void getAllCurrencyCodes_returnsSortedCurrencies() {
        when(rateRepository.findAllDistinctCurrencies())
                .thenReturn(Arrays.asList("USD", "EUR", "JPY"));

        List<String> result = currencyService.getAllCurrencyCodes();

        assertEquals(List.of("EUR", "JPY", "USD"), result);
        verify(rateRepository).findAllDistinctCurrencies();
    }

    @Test
    void getAllCurrencyCodes_throwsWhenNoCurrenciesFound() {
        when(rateRepository.findAllDistinctCurrencies())
                .thenReturn(Collections.emptyList());

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> currencyService.getAllCurrencyCodes()
        );

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("No currencies found in the database", exception.getReason());
    }
}
