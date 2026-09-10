package com.api.pulsetrack.modules.ingestion.controller;


import com.api.pulsetrack.modules.ingestion.dto.LocationIngestRequest;
import com.api.pulsetrack.modules.ingestion.producer.LocationProducerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/deliveries")
@CrossOrigin(origins = "http://localhost:4200")
public class IngestionController {

    private final LocationProducerService producerService;

    public IngestionController(LocationProducerService producerService) {
        this.producerService = producerService;
    }

    @PostMapping("/location")
    public ResponseEntity<Void> sendLocation(@RequestBody @Valid LocationIngestRequest request) {
        System.out.println("--> [INGESTION] Coordenada recebida para o pedido: " + request.orderId());
        producerService.sendLocationEvent(request);
        return ResponseEntity.accepted().build();
    }
}
