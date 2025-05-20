package com.crewmeister.cmcodingchallenge.mapper;

import com.crewmeister.cmcodingchallenge.dto.ExchangeRateResponseDto;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;

public class ExchangeRateMapper {

    public static ExchangeRateResponseDto mapToDto(ExchangeRateEntity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("ExchangeRateEntity cannot be null");
        }
        return new ExchangeRateResponseDto(
                entity.getBaseCurrency(),
                entity.getQuoteCurrency(),
                entity.getDate(),
                entity.getExchangeRate()
        );
    }
}
