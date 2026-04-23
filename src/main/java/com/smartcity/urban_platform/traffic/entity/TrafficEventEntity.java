package com.smartcity.urban_platform.traffic.entity;

import jakarta.persistence.*;
import java.time.Instant;

/**
 * Representa una fila en la tabla traffic_events de PostgreSQL.
 * Cada vez que el consumidor procesa un evento de Kafka,
 * crea un objeto de este tipo y lo guarda en la base de datos.
 */
@Entity
@Table(name = "traffic_events")
public class TrafficEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false, unique = true)
    private String eventId;

    @Column(name = "zone_id", nullable = false)
    private String zoneId;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(name = "vehicle_count", nullable = false)
    private int vehicleCount;

    @Column(name = "avg_speed_kmh", nullable = false)
    private double avgSpeedKmh;

    @Column(name = "congestion_index", nullable = false)
    private double congestionIndex;

    @Column(name = "status", nullable = false)
    private String status;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt;

    // Constructor vacío requerido por JPA
    protected TrafficEventEntity() {}

    // Constructor que usamos nosotros para crear objetos
    public TrafficEventEntity(
            String eventId,
            String zoneId,
            String sensorId,
            int vehicleCount,
            double avgSpeedKmh,
            double congestionIndex,
            String status,
            Instant recordedAt
    ) {
        this.eventId = eventId;
        this.zoneId = zoneId;
        this.sensorId = sensorId;
        this.vehicleCount = vehicleCount;
        this.avgSpeedKmh = avgSpeedKmh;
        this.congestionIndex = congestionIndex;
        this.status = status;
        this.recordedAt = recordedAt;
    }

    // Getters
    public Long getId()                  { return id; }
    public String getEventId()           { return eventId; }
    public String getZoneId()            { return zoneId; }
    public String getSensorId()          { return sensorId; }
    public int getVehicleCount()         { return vehicleCount; }
    public double getAvgSpeedKmh()       { return avgSpeedKmh; }
    public double getCongestionIndex()   { return congestionIndex; }
    public String getStatus()            { return status; }
    public Instant getRecordedAt()       { return recordedAt; }
}