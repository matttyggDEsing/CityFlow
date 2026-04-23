package com.smartcity.urban_platform.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Servicio que envía alertas al bot de Telegram configurado.
 * Las credenciales se leen del archivo .env y nunca
 * están hardcodeadas en el código ni en Git.
 */
@Service
public class TelegramNotificationService {

    private static final Logger log = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final WebClient webClient;
    private final String chatId;

    public TelegramNotificationService(
            @Value("${TELEGRAM_BOT_TOKEN}") String botToken,
            @Value("${TELEGRAM_CHAT_ID}") String chatId
    ) {
        this.chatId = chatId;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.telegram.org/bot" + botToken)
                .build();
    }

    /**
     * Envía un mensaje de alerta a Telegram.
     * Si falla el envío, solo loguea el error
     * para no interrumpir el flujo principal del sistema.
     */
    public void sendAlert(String message) {
        webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/sendMessage")
                        .queryParam("chat_id", chatId)
                        .queryParam("text", message)
                        .queryParam("parse_mode", "HTML")
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .doOnSuccess(response ->
                        log.info("Alerta enviada a Telegram correctamente"))
                .doOnError(error ->
                        log.error("Error enviando alerta a Telegram: {}", error.getMessage()))
                .subscribe();
    }

    /**
     * Alerta específica para cuando un sensor falla
     * y el circuit breaker se abre.
     */
    public void sendCircuitBreakerAlert(String sensorId, int failureCount) {
        String message = String.format(
                "⚡ <b>ALERTA CITYFLOW</b>\n\n" +
                        "🔴 <b>Circuit Breaker ABIERTO</b>\n" +
                        "📡 Sensor: <code>%s</code>\n" +
                        "❌ Fallos consecutivos: %d\n\n" +
                        "El sistema dejó de intentar conectarse a este sensor.\n" +
                        "Verificar funcionamiento del dispositivo.",
                sensorId, failureCount
        );
        sendAlert(message);
    }

    /**
     * Alerta cuando el circuit breaker se cierra
     * y el sensor vuelve a funcionar.
     */
    public void sendCircuitBreakerRecoveryAlert(String sensorId) {
        String message = String.format(
                "✅ <b>ALERTA CITYFLOW</b>\n\n" +
                        "🟢 <b>Sensor recuperado</b>\n" +
                        "📡 Sensor: <code>%s</code>\n\n" +
                        "El circuit breaker se cerró correctamente.\n" +
                        "El sensor volvió a funcionar con normalidad.",
                sensorId
        );
        sendAlert(message);
    }

    /**
     * Alerta cuando una zona entra en estado crítico.
     */
    public void sendZoneCriticalAlert(String zoneId, String domain, double value) {
        String message = String.format(
                "🚨 <b>ALERTA CITYFLOW</b>\n\n" +
                        "⚠️ <b>Zona en estado CRÍTICO</b>\n" +
                        "📍 Zona: <code>%s</code>\n" +
                        "🏙️ Dominio: <b>%s</b>\n" +
                        "📊 Valor: <b>%.2f</b>\n\n" +
                        "Se requiere atención inmediata.",
                zoneId, domain, value
        );
        sendAlert(message);
    }
}