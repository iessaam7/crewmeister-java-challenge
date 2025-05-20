package com.crewmeister.cmcodingchallenge.repository;

import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ExchangeRateRepository extends JpaRepository<ExchangeRateEntity, Long> {

    @Query("SELECT MAX(e.date) FROM ExchangeRateEntity e")
    Optional<LocalDate> findMaxDate();

    Optional<ExchangeRateEntity> findByBaseCurrencyAndQuoteCurrencyAndDate(String baseCurrency, String quoteCurrency, LocalDate date);

    @Query("select distinct e.quoteCurrency from ExchangeRateEntity e")
    List<String> findAllDistinctCurrencies();

    @Modifying
    @Query("DELETE FROM ExchangeRateEntity e WHERE e.date > :cutoffDate")
    void deleteAfterDate(@Param("cutoffDate") LocalDate cutoffDate);

    Page<ExchangeRateEntity> findByBaseCurrency(String baseCurrency, Pageable pageable);
}
