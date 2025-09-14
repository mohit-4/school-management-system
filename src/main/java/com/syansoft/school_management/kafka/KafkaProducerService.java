package com.syansoft.school_management.kafka;

import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendAsync(String topic, String key, String message) {
        CompletableFuture<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                System.err.println("[KafkaProducer] Failed to send message to topic " + topic + ": " + ex.getMessage());
            } else {
                RecordMetadata meta = result.getRecordMetadata();
                System.out.println("[KafkaProducer] Sent message to topic=" + meta.topic()
                        + ", partition=" + meta.partition()
                        + ", offset=" + meta.offset());
            }
        });
    }
}
