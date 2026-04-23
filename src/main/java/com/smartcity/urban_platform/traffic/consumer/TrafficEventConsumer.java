package com.smartcity.urban_platform.traffic.consumer;

import com.smartcity.urban_platform.notification.TelegramNotificationService;
import com.smartcity.urban_platform.traffic.entity.TrafficEventEntity;
import com.smartcity.urban_platform.traffic.model.TrafficSensorEvent;
import com.smartcity.urban_platform.traffic.repository.TrafficEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Escucha el topic de tráfico en Kafka, procesa cada evento,
 * evalúa el estado de la zona, lo guarda en PostgreSQL
 * y envía alertas a Telegram cuando hay situaciones críticas.
 */
@Service
public class TrafficEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TrafficEventConsumer.class);

    private final TrafficEventRepository repository;
    private final TelegramNotificationService telegram;

    public TrafficEventConsumer(
            TrafficEventRepository repository,
            TelegramNotificationService telegram
    ) {
        this.repository = repository;
        this.telegram = telegram;
    }

    @KafkaListener(
            topics = "urban.raw.traffic",
            groupId = "traffic-consumer-group"
    )
    public void consume(TrafficSensorEvent event) {

        ZoneStatus status = evaluateStatus(event.congestionIndex());

        TrafficEventEntity entity = new TrafficEventEntity(
                event.eventId(),
                event.zoneId(),
                event.sensorId(),
                event.vehicleCount(),
                event.avgSpeedKmh(),
                event.congestionIndex(),
                status.name(),
                event.timestamp()
        );

        repository.save(entity);

        log.info(
                "Evento guardado → zona={}, vehiculos={}, congestion={}, estado={}",
                event.zoneId(),
                event.vehicleCount(),
                event.congestionIndex(),
                status
        );

        // Enviar alerta a Telegram si la zona es crítica
        if (status == ZoneStatus.CRITICO) {
            log.warn("ALERTA CRÍTICA → zona={}", event.zoneId());
            telegram.sendZoneCriticalAlert(
                    event.zoneId(),
                    "TRÁFICO",
                    event.congestionIndex()
            );
        }
    }

    private ZoneStatus evaluateStatus(double congestionIndex) {
        if (congestionIndex < 0.4) {
            return ZoneStatus.NORMAL;
        } else if (congestionIndex < 0.7) {
            return ZoneStatus.ALERTA;
        } else {
            return ZoneStatus.CRITICO;
        }
    }

    enum ZoneStatus {
        NORMAL,
        ALERTA,
        CRITICO
    }
}