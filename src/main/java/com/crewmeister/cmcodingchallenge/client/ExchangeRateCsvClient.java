package com.crewmeister.cmcodingchallenge.client;

import com.crewmeister.cmcodingchallenge.exception.BundesBankClientException;
import com.crewmeister.cmcodingchallenge.utils.ApiConstants;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.stream.Stream;

@Component
public class ExchangeRateCsvClient {
    private static final String URL = ApiConstants.ExchangeRateAPI.BASE_URL;
    private final HttpClient httpClient;

    public ExchangeRateCsvClient() {
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    public Stream<String> fetchCsvLines() {
        try {
            HttpRequest request = buildRequest(URL);
            HttpResponse<Stream<String>> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofLines());

            int statusCode = response.statusCode();

            if (statusCode != 200) {
                throw new BundesBankClientException("Unexpected response from Bundesbank: " + statusCode);
            }

            return response.body();

        } catch (URISyntaxException e) {
            throw new BundesBankClientException("Invalid URL for Bundesbank: " + URL, e);
        } catch (IOException e) {
            throw new BundesBankClientException("IO error while fetching exchange rates", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new BundesBankClientException("Request interrupted while fetching exchange rates", e);
        }
    }

    private HttpRequest buildRequest(String url) throws URISyntaxException {
        return HttpRequest.newBuilder()
                .uri(new URI(url))
                .GET()
                .header("Accept", "text/csv")
                .build();
    }
}

