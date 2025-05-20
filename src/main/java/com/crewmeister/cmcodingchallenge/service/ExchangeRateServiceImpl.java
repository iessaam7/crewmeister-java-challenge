package com.crewmeister.cmcodingchallenge.service;

import com.crewmeister.cmcodingchallenge.dto.ConvertedExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.dto.ExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;
import com.crewmeister.cmcodingchallenge.mapper.ConvertedExchangeRateMapper;
import com.crewmeister.cmcodingchallenge.mapper.ExchangeRateMapper;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.utils.ApiConstants;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;


@Service
public class ExchangeRateServiceImpl implements ExchangeRateService{

    private final ExchangeRateRepository exchangeRateRepository;

    public ExchangeRateServiceImpl(ExchangeRateRepository exchangeRateRepository) {
        this.exchangeRateRepository = exchangeRateRepository;
    }


    public Page<ExchangeRateResponseDto> getAllEuroFxRates(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ExchangeRateEntity> ratePage = exchangeRateRepository.findByBaseCurrency(ApiConstants.ExchangeRateAPI.EURO_BASE_CURRENCY_CODE, pageable);

        if (ratePage.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No EUR-FX exchange rates found.");
        }

        return ratePage.map(ExchangeRateMapper::mapToDto);
    }

    public ExchangeRateResponseDto getEuroFxRateAtDate(LocalDate date, String quoteCurrency) {
        return exchangeRateRepository
                .findByBaseCurrencyAndQuoteCurrencyAndDate(ApiConstants.ExchangeRateAPI.EURO_BASE_CURRENCY_CODE, quoteCurrency, date)
                .map(rate -> {
                    if (rate.getExchangeRate() == null || rate.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
                        //Validation of the null rate value (.)
                        throw new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                String.format("Invalid exchange rate for EUR to %s on %s", quoteCurrency, date)
                        );
                    }
                    return ExchangeRateMapper.mapToDto(rate);
                })
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("Exchange rate not found for EUR to %s on %s", quoteCurrency, date)
                ));
    }

    public ConvertedExchangeRateResponseDto convertToEuro(String quoteCurrency, LocalDate date, BigDecimal amount) {
        // Handle EUR to EUR case
        if (ApiConstants.ExchangeRateAPI.EURO_BASE_CURRENCY_CODE.equalsIgnoreCase(quoteCurrency)) {
            return ConvertedExchangeRateMapper.mapToDto(
                    "EUR",
                    "EUR",
                    date,
                    amount.setScale(2, RoundingMode.HALF_UP),
                    amount.setScale(2, RoundingMode.HALF_UP)
            );
        }

        ExchangeRateEntity rate = exchangeRateRepository
                .findByBaseCurrencyAndQuoteCurrencyAndDate(ApiConstants.ExchangeRateAPI.EURO_BASE_CURRENCY_CODE, quoteCurrency, date)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        String.format("No exchange rate found for EUR to %s on %s", quoteCurrency, date)
                ));

        // Validation of the null rate value (.)
        if (rate.getExchangeRate() == null || rate.getExchangeRate().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    String.format("Invalid exchange rate for EUR to %s on %s", quoteCurrency, date)
            );
        }

        BigDecimal convertedAmount = amount.divide(rate.getExchangeRate(), 8, RoundingMode.HALF_UP);

        return ConvertedExchangeRateMapper.mapToDto(
                quoteCurrency,
                ApiConstants.ExchangeRateAPI.EURO_BASE_CURRENCY_CODE,
                date,
                amount.setScale(2, RoundingMode.HALF_UP),
                convertedAmount.setScale(2, RoundingMode.HALF_UP)
        );
    }
}
