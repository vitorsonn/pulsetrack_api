package com.api.pulsetrack.modules.ingestion.controller;


import com.api.pulsetrack.modules.ingestion.dto.LocationIngestRequest;
import com.api.pulsetrack.modules.ingestion.producer.LocationProducerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/deliveries")
public class IngestionController {

    private LocationProducerService producerService;

    public IngestionController(LocationProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/location")
    public ResponseEntity<Void> sendLocation(@RequestBody @Valid LocationIngestRequest request) {
        producerService.sendLocationEvent(request);
        return ResponseEntity.accepted().build();
    }
}
