package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    private final ExchangeRateRepository rateRepository;

    public CurrencyServiceImpl(ExchangeRateRepository rateRepository) {
        this.rateRepository = rateRepository;
    }


    public List<String> getAllCurrencyCodes() {
        List<String> currencies = rateRepository.findAllDistinctCurrencies()
                .stream()
                .sorted()
                .collect(Collectors.toList());

        if (currencies.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No currencies found in the database"
            );
        }

        return currencies;
    }
}

