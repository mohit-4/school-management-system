package com.syansoft.school_management.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic studentEventsTopic() {
        return new NewTopic("student-events", 1, (short) 1);
    }

    @Bean
    public NewTopic teacherEventTopic() {
        return new NewTopic("teacher-events", 1, (short) 1);
    }
}
