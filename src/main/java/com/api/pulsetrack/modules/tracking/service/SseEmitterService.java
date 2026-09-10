package com.api.pulsetrack.modules.tracking.service;

import com.api.pulsetrack.modules.tracking.dto.LocationTrackingResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long orderId) {
        SseEmitter emitter = new SseEmitter(1800000L); // 30 min
        this.emitters.put(orderId, emitter);

        System.out.println("--> [SSE SERVICE] Cliente CONECTADO para orderId: " + orderId + " | Emitters ativos: " + emitters.keySet());

        // Evento de inicialização para firmar o aperto de mão com o navegador
        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Conexao estabelecida com sucesso"));
        } catch (IOException e) {
            this.emitters.remove(orderId);
        }

        emitter.onCompletion(() -> this.emitters.remove(orderId));
        emitter.onTimeout(() -> this.emitters.remove(orderId));
        emitter.onError((e) -> this.emitters.remove(orderId));

        return emitter;
    }

    public void sendLocationUpdate(LocationTrackingResponse payload) {
        SseEmitter emitter = this.emitters.get(payload.orderId());

        if (emitter != null) {
            try {
                // Especifica explicitamente o MediaType APPLICATION_JSON para evitar erro de I/O na conversão
                emitter.send(SseEmitter.event()
                        .name("location-update")
                        .data(payload, org.springframework.http.MediaType.APPLICATION_JSON));

                System.out.println("--> [SSE SERVICE] Evento enviado com SUCESSO para orderId: " + payload.orderId());

            } catch (Exception e) {
                this.emitters.remove(payload.orderId());
                System.out.println("--> [SSE SERVICE] Falha de I/O ao enviar para orderId: " + payload.orderId() + " -> " + e.getMessage());
            }
        } else {
            System.out.println("--> [SSE SERVICE] NENHUM CLIENTE CONECTADO ouvindo o orderId: " + payload.orderId() + " | Emitters ativos: " + emitters.keySet());
        }
    }
}