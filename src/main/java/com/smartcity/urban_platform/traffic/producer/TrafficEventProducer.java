package com.smartcity.urban_platform.traffic.producer;

import com.smartcity.urban_platform.traffic.model.TrafficSensorEvent;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Responsable de enviar eventos de sensores de tráfico a Kafka.
 * Cada evento enviado queda registrado en el topic "urban.raw.traffic"
 * para que el procesador lo consuma.
 */
@Service
public class TrafficEventProducer {

    private static final Logger log = LoggerFactory.getLogger(TrafficEventProducer.class);
    private static final String TOPIC = "urban.raw.traffic";

    private final KafkaTemplate<String, TrafficSensorEvent> kafkaTemplate;
    private final Timer sendTimer;

    public TrafficEventProducer(
            KafkaTemplate<String, TrafficSensorEvent> kafkaTemplate,
            MeterRegistry meterRegistry
    ) {
        this.kafkaTemplate = kafkaTemplate;
        // Este timer mide cuánto tarda cada envío a Kafka
        // Lo vamos a ver más adelante en el dashboard de métricas
        this.sendTimer = Timer.builder("traffic.events.send.duration")
                .description("Tiempo que tarda en enviarse un evento de tráfico a Kafka")
                .register(meterRegistry);
    }

    /**
     * Envía un evento de tráfico a Kafka de forma asíncrona.
     * Usamos el zoneId como clave para que todos los eventos
     * de la misma zona vayan a la misma partición de Kafka,
     * garantizando el orden de los mensajes por zona.
     */
    public void send(TrafficSensorEvent event) {
        sendTimer.record(() -> {

            CompletableFuture<SendResult<String, TrafficSensorEvent>> future =
                    kafkaTemplate.send(TOPIC, event.zoneId(), event);

            future.whenComplete((result, error) -> {
                if (error != null) {
                    log.error(
                            "Error enviando evento a Kafka: eventId={}, zoneId={}, error={}",
                            event.eventId(), event.zoneId(), error.getMessage()
                    );
                } else {
                    log.info(
                            "Evento enviado: eventId={}, zoneId={}, particion={}, offset={}",
                            event.eventId(),
                            event.zoneId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                }
            });

        });
    }
}