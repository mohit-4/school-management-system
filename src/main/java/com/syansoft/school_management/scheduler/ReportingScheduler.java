package com.syansoft.school_management.scheduler;

import com.syansoft.school_management.kafka.KafkaProducerService;
import com.syansoft.school_management.repository.StudentRepository;
import com.syansoft.school_management.repository.TeacherRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReportingScheduler {

    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final KafkaProducerService kafkaProducer;

    public ReportingScheduler(StudentRepository studentRepository,
                              TeacherRepository teacherRepository,
                              KafkaProducerService kafkaProducer) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Scheduled(fixedRateString = "${reporting.fixed-rate-ms:60000}")
    public void runSummary() {
        long students = studentRepository.count();
        long teachers = teacherRepository.count();
        String msg = "SUMMARY: students=" + students + ", teachers=" + teachers + ", time=" + java.time.Instant.now();
        kafkaProducer.sendAsync("student-events", "summary", msg);
        System.out.println("[Scheduler] Published summary: " + msg);
    }
}
