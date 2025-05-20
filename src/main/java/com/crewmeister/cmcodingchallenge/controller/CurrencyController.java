package com.crewmeister.cmcodingchallenge.controller;

import com.crewmeister.cmcodingchallenge.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CurrencyController {

    private final CurrencyService currencyService;

    public CurrencyController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/currencies")
    public ResponseEntity<List<String>> getAllCurrencies() {
        return ResponseEntity.ok(currencyService.getAllCurrencyCodes());
    }
}
