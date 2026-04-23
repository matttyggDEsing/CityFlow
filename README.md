# 🚀 Urban Platform – Smart City Traffic System

<p align="center">
  <img src="https://img.shields.io/badge/Java-21-orange">
  <img src="https://img.shields.io/badge/Spring_Boot-3.x-brightgreen">
  <img src="https://img.shields.io/badge/Kafka-EventDriven-black">
  <img src="https://img.shields.io/badge/PostgreSQL-DB-blue">
  <img src="https://img.shields.io/badge/Docker-Ready-2496ED">
</p>

---

## 🌆 ¿Qué es Urban Platform?

**Urban Platform** es un sistema backend inspirado en arquitecturas reales de Smart Cities, diseñado para procesar eventos de tráfico en tiempo real utilizando un enfoque **event-driven**.

Simula sensores urbanos que envían datos constantemente, los cuales son procesados, evaluados y almacenados para análisis y toma de decisiones.

---

## ⚡ Características principales

✔️ Procesamiento en tiempo real con Kafka  
✔️ Arquitectura desacoplada y escalable  
✔️ Persistencia en PostgreSQL  
✔️ API REST para simulación y consultas  
✔️ Métricas y observabilidad integradas  
✔️ Migraciones automáticas con Flyway  

---

## 🧠 Arquitectura

```
[Sensor/API]
     ↓
[Kafka Producer]
     ↓
[Kafka Topic: urban.raw.traffic]
     ↓
[Kafka Consumer]
     ↓
[PostgreSQL]
```

---

## 🔄 Flujo del sistema

1. Se genera un evento de tráfico
2. El producer lo envía a Kafka
3. Kafka lo distribuye
4. El consumer lo procesa
5. Se calcula el estado de la zona:
   - 🟢 NORMAL
   - 🟡 ALERTA
   - 🔴 CRÍTICO
6. Se guarda en la base de datos
7. Se consulta vía API

---

## 🛠️ Tecnologías

- Java 21
- Spring Boot
- Spring Web / WebFlux
- Apache Kafka
- PostgreSQL
- Flyway
- Docker Compose
- Resilience4j
- Micrometer

---

## 🚀 Cómo ejecutar

### 1. Levantar infraestructura

```bash
docker-compose up -d
```

### 2. Ejecutar aplicación

```bash
./mvnw spring-boot:run
```

---

## 🌐 Endpoints

### 🔹 Simular evento

POST /api/v1/traffic/simulate

### 🔹 Historial por zona

GET /api/v1/traffic/historial?zona=ZONA-CENTRO

### 🔹 Eventos críticos

GET /api/v1/traffic/criticos

---

## 👨‍💻 Autor

Maty Anderegg

---

## 📜 Licencia

© 2026 Maty Anderegg. Todos los derechos reservados.

Este software fue desarrollado de manera independiente.  
No está permitido copiar, modificar ni distribuir este proyecto sin autorización explícita del autor.
