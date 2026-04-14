package com.elbakali.vehicle_aggregator.client;

import com.elbakali.vehicle_aggregator.dto.NhtsaResponse;
import com.elbakali.vehicle_aggregator.dto.RecallResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Web client responsible for communicating with external NHTSA government APIs.
 * Utilizes dedicated WebClient instances for distinct government domains.
 */
@Service
public class NhtsaClient {

    private final WebClient vpicWebClient;
    private final WebClient recallsWebClient;

    // We instantiate separate clients with their own dedicated base URLs
    public NhtsaClient() {
        this.vpicWebClient = WebClient.create("https://vpic.nhtsa.dot.gov/api/");
        this.recallsWebClient = WebClient.create("https://api.nhtsa.gov/");
    }

    /**
     * Decodes a 17-character VIN to retrieve standard vehicle specifications.
     *
     * @param vin The Vehicle Identification Number.
     * @return A Mono emitting the raw NHTSA decoding response.
     */
    public Mono<NhtsaResponse> fetchVehicleDetails(String vin) {
        return this.vpicWebClient.get()
                .uri("vehicles/DecodeVin/{vin}?format=json", vin)
                .retrieve()
                .bodyToMono(NhtsaResponse.class);
    }

    /**
     * Fetches safety recalls for a specific vehicle from the NHTSA database.
     *
     * @param make  The vehicle manufacturer (e.g., "Honda").
     * @param model The vehicle model (e.g., "Accord").
     * @param year  The model year (e.g., "2015").
     * @return A Mono emitting the safety recall response payload.
     */
    public Mono<RecallResponse> fetchRecalls(String make, String model, String year) {
        return this.recallsWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("recalls/recallsByVehicle")
                        .queryParam("make", make)
                        .queryParam("model", model)
                        .queryParam("modelYear", year)
                        .queryParam("format", "json")
                        .build())
                .retrieve()
                .bodyToMono(RecallResponse.class);
    }
}