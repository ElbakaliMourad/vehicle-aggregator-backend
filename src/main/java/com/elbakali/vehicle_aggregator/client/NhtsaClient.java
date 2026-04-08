package com.elbakali.vehicle_aggregator.client;

import com.elbakali.vehicle_aggregator.dto.NhtsaResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class NhtsaClient {

    private final WebClient webClient;

    public NhtsaClient() {
        this.webClient = WebClient.create("https://vpic.nhtsa.dot.gov/api/");
    }

    public Mono<NhtsaResponse> fetchVehicleDetails(String vin) {
        return this.webClient.get()
                .uri("vehicles/DecodeVin/{vin}?format=json", vin)
                .retrieve()
                .bodyToMono(NhtsaResponse.class);
    }
}
