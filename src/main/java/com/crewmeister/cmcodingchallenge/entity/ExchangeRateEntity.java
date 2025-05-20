package com.crewmeister.cmcodingchallenge.entity;

import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Entity
@Table(name = "exchange_rates")
public class ExchangeRateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 3)
    @NotBlank(message = "Base currency code cannot be empty")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Base currency code must be 3 uppercase letters")
    private String baseCurrency;

    @Column(nullable = false, length = 3)
    @NotBlank(message = "Quote currency code cannot be empty")
    @Pattern(regexp = "^[A-Z]{3}$", message = "Quote currency code must be 3 uppercase letters")
    private String quoteCurrency;

    @Column(nullable = false)
    @NotNull(message = "Date cannot be null")
    @PastOrPresent(message = "Date must not be in the future")
    private LocalDate date;

    @Column(nullable = true)
    @Positive(message = "Exchange rate must be positive")
    @Digits(integer = 10, fraction = 6, message = "Exchange rate must have up to 6 decimal places")
    private BigDecimal exchangeRate;

    @Column(nullable = false)
    @PastOrPresent(message = "Last updated date must not be in the future")
    private LocalDateTime lastUpdated;

    public ExchangeRateEntity() {
    }

    public ExchangeRateEntity(
            String baseCurrency,
            String quoteCurrency,
            LocalDate date,
            BigDecimal exchangeRate,
            LocalDateTime lastUpdated) {
        this.baseCurrency = baseCurrency;
        this.quoteCurrency = quoteCurrency;
        this.date = date;
        this.exchangeRate = exchangeRate;
        this.lastUpdated = lastUpdated;
    }

    public Long getId() {
        return id;
    }

    public String getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(String baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public String getQuoteCurrency() {
        return quoteCurrency;
    }

    public void setQuoteCurrency(String quoteCurrency) {
        this.quoteCurrency = quoteCurrency;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public BigDecimal getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(BigDecimal exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}

