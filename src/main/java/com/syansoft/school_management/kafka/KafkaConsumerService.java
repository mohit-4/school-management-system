package com.syansoft.school_management.kafka;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

//@Service
//public class KafkaConsumerService {
//
//    @KafkaListener(topics = "student-events", groupId = "school-group")
//    public void listenStudentEvents(String message) {
//        System.out.println("[KafkaConsumer] student-events -> " + message);
//
//        System.out.println("[Audit] Student event logged at " + LocalDateTime.now() + " -> " + message);
//
//        sendNotification("STUDENT", message);
//    }
//
//    @KafkaListener(topics = "teacher-events", groupId = "school-group")
//    public void listenTeacherEvents(String message) {
//        System.out.println("[KafkaConsumer] teacher-events -> " + message);
//
//        System.out.println("[Audit] Teacher event logged at " + LocalDateTime.now() + " -> " + message);
//
//        sendNotification("TEACHER", message);
//    }
//
//    private void sendNotification(String userType, String message) {
//        System.out.println("[Notification] Sending " + userType + " notification: " + message);
//    }
//}

@Service
public class KafkaConsumerService {
    private final StringRedisTemplate redisTemplate;

    public KafkaConsumerService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @KafkaListener(topics = "student-events", groupId = "school-group")
    public void listenStudentEvents(String message) {
        System.out.println("[KafkaConsumer] student-events -> " + message);

        String redisKey = "student:event:" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(redisKey, message);

        System.out.println("[Audit] Student event logged at " + LocalDateTime.now() + " -> " + message);

        sendNotification("STUDENT", message);
    }

    @KafkaListener(topics = "teacher-events", groupId = "school-group")
    public void listenTeacherEvents(String message) {
        System.out.println("[KafkaConsumer] teacher-events -> " + message);

        String redisKey = "teacher:event:" + System.currentTimeMillis();
        redisTemplate.opsForValue().set(redisKey, message);

        System.out.println("[Audit] Teacher event logged at " + LocalDateTime.now() + " -> " + message);

        sendNotification("TEACHER", message);
    }

    private void sendNotification(String userType, String message) {
        System.out.println("[Notification] Sending " + userType + " notification: " + message);
    }
}

