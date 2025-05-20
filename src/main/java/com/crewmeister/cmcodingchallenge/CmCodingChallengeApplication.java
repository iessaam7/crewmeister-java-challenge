package com.crewmeister.cmcodingchallenge;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@SpringBootApplication
@EnableScheduling
public class CmCodingChallengeApplication {

	public static void main(String[] args) {
		SpringApplication.run(CmCodingChallengeApplication.class, args);
	}
}
