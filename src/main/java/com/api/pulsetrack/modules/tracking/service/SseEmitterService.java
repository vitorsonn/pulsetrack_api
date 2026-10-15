package com.api.pulsetrack.modules.tracking.service;

import com.api.pulsetrack.modules.tracking.dto.LocationTrackingResponse;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseEmitterService {

    private final Map<Long, Set<SseEmitter>> emitters = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long orderId) {
        SseEmitter emitter = new SseEmitter(1800000L); // 30 min
        Set<SseEmitter> orderEmitters = this.emitters.computeIfAbsent(
                orderId,
                ignored -> ConcurrentHashMap.newKeySet()
        );
        orderEmitters.add(emitter);

        System.out.println("--> [SSE SERVICE] Cliente CONECTADO para orderId: " + orderId + " | Emitters ativos: " + emitters.keySet());


        try {
            emitter.send(SseEmitter.event()
                    .name("INIT")
                    .data("Conexao estabelecida com sucesso"));
        } catch (IOException e) {
            removeEmitter(orderId, emitter);
        }

        emitter.onCompletion(() -> removeEmitter(orderId, emitter));
        emitter.onTimeout(() -> removeEmitter(orderId, emitter));
        emitter.onError((e) -> removeEmitter(orderId, emitter));

        return emitter;
    }

    public void sendLocationUpdate(LocationTrackingResponse payload) {
        Set<SseEmitter> orderEmitters = this.emitters.get(payload.orderId());

        if (orderEmitters != null && !orderEmitters.isEmpty()) {
            int successfulSends = 0;
            for (SseEmitter emitter : orderEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .name("location-update")
                            .data(payload, MediaType.APPLICATION_JSON));
                    successfulSends++;
                } catch (Exception e) {
                    removeEmitter(payload.orderId(), emitter);
                    System.out.println("--> [SSE SERVICE] Falha de I/O ao enviar para orderId: "
                            + payload.orderId() + " -> " + e.getMessage());
                }
            }

            System.out.println("--> [SSE SERVICE] Evento enviado com SUCESSO para orderId: "
                    + payload.orderId() + " | Clientes notificados: " + successfulSends);
        } else {
            System.out.println("--> [SSE SERVICE] NENHUM CLIENTE CONECTADO ouvindo o orderId: " + payload.orderId() + " | Emitters ativos: " + emitters.keySet());
        }
    }

    public void closeStream(Long orderId) {
        Set<SseEmitter> orderEmitters = emitters.remove(orderId);
        if (orderEmitters != null && !orderEmitters.isEmpty()) {
            for (SseEmitter emitter : orderEmitters) {
                try {
                    emitter.complete();
                } catch (Exception e) {
                    System.err.println("--> [SSE SERVICE] Erro ao fechar emitter: " + e.getMessage());
                }
            }
            System.out.println("--> [SSE SERVICE] Stream SSE encerrada com sucesso para orderId: " + orderId
                    + " | Clientes encerrados: " + orderEmitters.size());
        }
    }

    private void removeEmitter(Long orderId, SseEmitter emitter) {
        Set<SseEmitter> orderEmitters = this.emitters.get(orderId);
        if (orderEmitters != null && orderEmitters.remove(emitter) && orderEmitters.isEmpty()) {
            this.emitters.remove(orderId, orderEmitters);
        }
    }
}