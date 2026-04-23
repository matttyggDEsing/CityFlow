package com.smartcity.urban_platform.traffic.model;

import lombok.Builder;
import java.time.Instant;
import java.util.UUID;

/**
 * Representa un evento generado por un sensor de tráfico.
 * Cada vez que un sensor detecta vehículos, crea un objeto de este tipo
 * y lo envía por Kafka para que el sistema lo procese.
 */
@Builder
public record TrafficSensorEvent(

        // Identificador único del evento (se genera automáticamente)
        String eventId,

        // En qué zona de la ciudad está el sensor
        String zoneId,

        // ID del sensor específico que generó el evento
        String sensorId,

        // Cuántos vehículos detectó en este intervalo
        int vehicleCount,

        // Velocidad promedio de los vehículos en km/h
        double avgSpeedKmh,

        // Qué tan congestionada está la zona (0.0 = libre, 1.0 = colapso total)
        double congestionIndex,

        // Cuándo ocurrió el evento
        Instant timestamp

) {
    /**
     * Crea un evento con ID y timestamp generados automáticamente.
     * Así el código que crea eventos no tiene que preocuparse por esos detalles.
     */
    public static TrafficSensorEvent of(
            String zoneId,
            String sensorId,
            int vehicleCount,
            double avgSpeedKmh,
            double congestionIndex
    ) {
        return TrafficSensorEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .zoneId(zoneId)
                .sensorId(sensorId)
                .vehicleCount(vehicleCount)
                .avgSpeedKmh(avgSpeedKmh)
                .congestionIndex(congestionIndex)
                .timestamp(Instant.now())
                .build();
    }
}