package com.smartcity.urban_platform.traffic.controller;

import com.smartcity.urban_platform.traffic.entity.TrafficEventEntity;
import com.smartcity.urban_platform.traffic.model.TrafficSensorEvent;
import com.smartcity.urban_platform.traffic.producer.TrafficEventProducer;
import com.smartcity.urban_platform.traffic.repository.TrafficEventRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Expone endpoints REST para el dominio de tráfico.
 */
@RestController
@RequestMapping("/api/v1/traffic")
public class TrafficController {

    private final TrafficEventProducer producer;
    private final TrafficEventRepository repository;

    public TrafficController(
            TrafficEventProducer producer,
            TrafficEventRepository repository
    ) {
        this.producer = producer;
        this.repository = repository;
    }

    // Simula un evento de sensor
    @PostMapping("/simulate")
    public ResponseEntity<String> simulate(
            @RequestParam(defaultValue = "ZONA-CENTRO") String zona,
            @RequestParam(defaultValue = "30")          int vehiculos,
            @RequestParam(defaultValue = "45.5")        double velocidad,
            @RequestParam(defaultValue = "0.6")         double congestion
    ) {
        TrafficSensorEvent event = TrafficSensorEvent.of(
                zona,
                "SENSOR-" + zona + "-001",
                vehiculos,
                velocidad,
                congestion
        );

        producer.send(event);

        return ResponseEntity.ok(
                "Evento enviado → ID: " + event.eventId() +
                        " | Zona: " + event.zoneId() +
                        " | Vehículos: " + event.vehicleCount()
        );
    }

    // Consulta el historial de una zona
    @GetMapping("/historial")
    public ResponseEntity<List<TrafficEventEntity>> historial(
            @RequestParam String zona
    ) {
        List<TrafficEventEntity> eventos =
                repository.findTop10ByZoneIdOrderByRecordedAtDesc(zona);
        return ResponseEntity.ok(eventos);
    }

    // Consulta todos los eventos críticos
    @GetMapping("/criticos")
    public ResponseEntity<List<TrafficEventEntity>> criticos() {
        List<TrafficEventEntity> eventos =
                repository.findByStatusOrderByRecordedAtDesc("CRITICO");
        return ResponseEntity.ok(eventos);
    }
}