package com.smartcity.urban_platform.traffic.consumer;

import com.smartcity.urban_platform.traffic.entity.TrafficEventEntity;
import com.smartcity.urban_platform.traffic.model.TrafficSensorEvent;
import com.smartcity.urban_platform.traffic.repository.TrafficEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Escucha el topic de tráfico en Kafka, procesa cada evento,
 * evalúa el estado de la zona y lo guarda en PostgreSQL.
 */
@Service
public class TrafficEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(TrafficEventConsumer.class);

    private final TrafficEventRepository repository;

    public TrafficEventConsumer(TrafficEventRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(
            topics = "urban.raw.traffic",
            groupId = "traffic-consumer-group"
    )
    public void consume(TrafficSensorEvent event) {

        ZoneStatus status = evaluateStatus(event.congestionIndex());

        // Guardar en PostgreSQL
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

        if (status == ZoneStatus.CRITICO) {
            log.warn(
                    "ALERTA CRÍTICA → zona={} con congestion={}",
                    event.zoneId(),
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