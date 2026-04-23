package com.smartcity.urban_platform.traffic.repository;

import com.smartcity.urban_platform.traffic.entity.TrafficEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring genera automáticamente el SQL para todos estos métodos.
 * No necesitás escribir ninguna query a mano.
 */
@Repository
public interface TrafficEventRepository
        extends JpaRepository<TrafficEventEntity, Long> {

    // Busca todos los eventos de una zona ordenados por fecha
    List<TrafficEventEntity> findByZoneIdOrderByRecordedAtDesc(String zoneId);

    // Busca los últimos eventos críticos
    List<TrafficEventEntity> findByStatusOrderByRecordedAtDesc(String status);

    // Busca los últimos N eventos de una zona
    List<TrafficEventEntity> findTop10ByZoneIdOrderByRecordedAtDesc(String zoneId);
}