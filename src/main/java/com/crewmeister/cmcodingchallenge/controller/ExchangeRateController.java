package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.dto.ConvertedExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.dto.ExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.service.ExchangeRateService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/exchange-rates")
@Validated
public class ExchangeRateController {

    private final ExchangeRateService exchangeRateService;

    public ExchangeRateController(ExchangeRateService exchangeRateService) {
        this.exchangeRateService = exchangeRateService;
    }

    @Operation(summary = "Get all Euro Fx exchange rates on all dates")
    @GetMapping
    public ResponseEntity<Page<ExchangeRateResponseDto>> getAllRates(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "40") @Min(1) @Max(100) int size) {

        Page<ExchangeRateResponseDto> response = exchangeRateService.getAllEuroFxRates(page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get rate on a specific date")
    @GetMapping("/rate")
    public ResponseEntity<ExchangeRateResponseDto> getRateForDate(
            @RequestParam @NotNull @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @NotBlank @Size(min = 3, max = 3) @Pattern(regexp = "[A-Z]{3}") String quoteCurrency) {

        ExchangeRateResponseDto response = exchangeRateService.getEuroFxRateAtDate(date, quoteCurrency);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Convert the current currency to Euro")
    @GetMapping("/convert")
    public ResponseEntity<ConvertedExchangeRateResponseDto> convertToEuro(
            @RequestParam @NotBlank @Size(min = 3, max = 3) @Pattern(regexp = "[A-Z]{3}") String quoteCurrency,
            @RequestParam @NotNull @Positive BigDecimal amount,
            @RequestParam @NotNull @PastOrPresent @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        ConvertedExchangeRateResponseDto response = exchangeRateService.convertToEuro(quoteCurrency, date, amount);
        return ResponseEntity.ok(response);
    }
}
