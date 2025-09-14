package com.syansoft.school_management.service.impl;

import com.syansoft.school_management.dtos.StudentDto;
import com.syansoft.school_management.entity.Student;
import com.syansoft.school_management.entity.Teacher;
import com.syansoft.school_management.exception.BadRequestException;
import com.syansoft.school_management.exception.ConflictException;
import com.syansoft.school_management.exception.NotFoundException;
import com.syansoft.school_management.kafka.KafkaProducerService;
import com.syansoft.school_management.repository.StudentRepository;
import com.syansoft.school_management.repository.TeacherRepository;
import com.syansoft.school_management.service.StudentService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StudentServiceImpl implements StudentService {
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final KafkaProducerService kafkaProducer;

    public StudentServiceImpl(StudentRepository studentRepository,
                              TeacherRepository teacherRepository,
                              KafkaProducerService kafkaProducer) {
        this.studentRepository = studentRepository;
        this.teacherRepository = teacherRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public StudentDto createStudent(StudentDto dto) {
        if (dto == null) throw new BadRequestException("ERROR_INVALID_PAYLOAD", "Student payload required");
        if (dto.getName() == null || dto.getName().isBlank())
            throw new BadRequestException("ERROR_NAME_REQUIRED", "Student name required");
        if (dto.getEmail() == null || dto.getEmail().isBlank())
            throw new BadRequestException("ERROR_EMAIL_REQUIRED", "Student email required");

        studentRepository.findByEmail(dto.getEmail()).ifPresent(s -> {
            throw new ConflictException("ERROR_EMAIL_EXISTS", "Email already used");
        });

        Student s = Student.builder()
                .name(dto.getName())
                .email(dto.getEmail())
                .age(dto.getAge())
                .grade(dto.getGrade())
                .build();

        if (dto.getTeacherId() != null) {
            Teacher t = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new NotFoundException("ERROR_TEACHER_NOT_FOUND", "Teacher not found"));
            s.setTeacher(t);
        }
        Student saved = studentRepository.save(s);
        kafkaProducer.sendAsync("student-events", String.valueOf(saved.getId()), "CREATED:" + saved.getId());
        return toDto(saved);
    }

    @Override
    @Cacheable(value = "student", key = "#id")
    public StudentDto getStudent(Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERROR_STUDENT_NOT_FOUND", "Student not found"));
        return toDto(s);
    }

    @Override
    @CacheEvict(value = "studentsAll",allEntries = true)
    public List<StudentDto> listStudents() {
        return studentRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"student", "studentsAll"}, allEntries = true)
    public StudentDto updateStudent(Long id, StudentDto dto) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERROR_STUDENT_NOT_FOUND", "Student not found"));
        if (dto.getName() != null && !dto.getName().isBlank()) s.setName(dto.getName());
        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            studentRepository.findByEmail(dto.getEmail()).ifPresent(existing -> {
                if (!existing.getId().equals(id))
                    throw new ConflictException("ERROR_EMAIL_EXISTS", "Email used by another student");
            });
            s.setEmail(dto.getEmail());
        }
        if (dto.getAge() != null) s.setAge(dto.getAge());
        if (dto.getGrade() != null) s.setGrade(dto.getGrade());
        if (dto.getTeacherId() != null) {
            Teacher t = teacherRepository.findById(dto.getTeacherId())
                    .orElseThrow(() -> new NotFoundException("ERROR_TEACHER_NOT_FOUND", "Teacher not found"));
            s.setTeacher(t);
        }
        Student saved = studentRepository.save(s);
        kafkaProducer.sendAsync("student-events", String.valueOf(saved.getId()), "UPDATED:" + saved.getId());
        return toDto(saved);
    }

    @Override
    @CacheEvict(value = {"student", "studentsAll"}, allEntries = true)
    public void deleteStudent(Long id) {
        Student s = studentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERROR_STUDENT_NOT_FOUND", "Student not found"));
        studentRepository.deleteById(id);
        kafkaProducer.sendAsync("student-events", String.valueOf(id), "DELETED:" + id);
    }

    private StudentDto toDto(Student s) {
        Long teacherId = s.getTeacher() != null ? s.getTeacher().getId() : null;
        return StudentDto.builder()
                .id(s.getId())
                .name(s.getName())
                .email(s.getEmail())
                .age(s.getAge())
                .grade(s.getGrade())
                .teacherId(teacherId)
                .build();
    }
}
