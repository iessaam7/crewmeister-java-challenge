package com.crewmeister.cmcodingchallenge.scheduler;

import com.crewmeister.cmcodingchallenge.client.ExchangeRateCsvClient;
import com.crewmeister.cmcodingchallenge.client.ExchangeRateCsvParser;
import com.crewmeister.cmcodingchallenge.entity.ExchangeRateEntity;
import com.crewmeister.cmcodingchallenge.exception.DataImportException;
import com.crewmeister.cmcodingchallenge.repository.ExchangeRateRepository;
import com.crewmeister.cmcodingchallenge.utils.ApiConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class ExchangeRateScheduler {
    private final ExchangeRateCsvClient csvClient;
    private final ExchangeRateCsvParser csvParser;
    private final ExchangeRateRepository exchangeRateRepository;
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateCsvParser.class);

    public ExchangeRateScheduler(ExchangeRateCsvClient csvClient, ExchangeRateCsvParser csvParser, ExchangeRateRepository exchangeRateRepository) {
        this.csvClient = csvClient;
        this.csvParser = csvParser;
        this.exchangeRateRepository = exchangeRateRepository;
    }


    @Transactional
    @Scheduled(fixedDelay = ApiConstants.ExchangeRateAPI.DATA_REFRESH_RATE) // 1 hour refresh rate
    public void scheduledRateImport() {
        try {
            log.info("Starting exchange rate import");

            Optional<LocalDate> latestDateInDb = exchangeRateRepository.findMaxDate();

            try (Stream<String> csvLines = csvClient.fetchCsvLines()) {
                // Filter to only gett new records
                List<ExchangeRateEntity> newEntities = csvParser.convertCsv(csvLines)
                        .filter(entity -> latestDateInDb.isEmpty() ||
                                entity.getDate().isAfter(latestDateInDb.get()))
                        .collect(Collectors.toList());

                if (newEntities.isEmpty()) {
                    log.info("No new exchange rates found skipping save operation");
                    return;
                }

                // Only delete and save if we have new data
                if (latestDateInDb.isPresent()) {
                    exchangeRateRepository.deleteAfterDate(latestDateInDb.get());
                }

                saveInBatches(newEntities.stream(), 1000);
                log.info("Saved {} new exchange rate records", newEntities.size());
            }

        } catch (Exception e) {
            log.error("Failed to import exchange rates", e);
            throw new DataImportException("Exchange rate import failed", e);
        }
    }

    private void saveInBatches(Stream<ExchangeRateEntity> stream, int batchSize) {
        List<ExchangeRateEntity> batch = new ArrayList<>(batchSize);

        stream.forEach(entity -> {
            batch.add(entity);
            if (batch.size() >= batchSize) {
                exchangeRateRepository.saveAll(batch);
                batch.clear();
            }
        });

        if (!batch.isEmpty()) {
            exchangeRateRepository.saveAll(batch);
        }
    }
}
