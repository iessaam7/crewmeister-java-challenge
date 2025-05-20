package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.exception.GlobalExceptionHandler;
import com.crewmeister.cmcodingchallenge.service.CurrencyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class CurrencyControllerTest {

    @Mock
    private CurrencyServiceImpl currencyService;

    @InjectMocks
    private CurrencyController currencyController;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(currencyController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void getAllCurrencies_returns200WithCurrencies() throws Exception {
        when(currencyService.getAllCurrencyCodes())
                .thenReturn(Arrays.asList("USD", "EUR", "JPY"));

        mockMvc.perform(get("/api/currencies")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("USD"))
                .andExpect(jsonPath("$[1]").value("EUR"))
                .andExpect(jsonPath("$[2]").value("JPY"));
    }

    // In test
    @Test
    void getAllCurrencies_returns404WhenEmpty() throws Exception {
        when(currencyService.getAllCurrencyCodes())
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found"));

        mockMvc.perform(get("/api/currencies"))
                .andExpect(status().isNotFound());
    }
}

