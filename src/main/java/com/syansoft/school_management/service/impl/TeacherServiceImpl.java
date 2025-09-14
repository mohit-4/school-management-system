package com.syansoft.school_management.service.impl;

import com.syansoft.school_management.dtos.TeacherDto;
import com.syansoft.school_management.entity.Student;
import com.syansoft.school_management.entity.Teacher;
import com.syansoft.school_management.exception.BadRequestException;
import com.syansoft.school_management.exception.NotFoundException;
import com.syansoft.school_management.kafka.KafkaProducerService;
import com.syansoft.school_management.repository.StudentRepository;
import com.syansoft.school_management.repository.TeacherRepository;
import com.syansoft.school_management.service.TeacherService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeacherServiceImpl implements TeacherService {
    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final KafkaProducerService kafkaProducer;

    public TeacherServiceImpl(TeacherRepository teacherRepository,
                              StudentRepository studentRepository,
                              KafkaProducerService kafkaProducer) {
        this.teacherRepository = teacherRepository;
        this.studentRepository = studentRepository;
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public TeacherDto createTeacher(TeacherDto dto) {
        if (dto == null || dto.getName() == null || dto.getName().isBlank())
            throw new BadRequestException("ERROR_INVALID_PAYLOAD", "Teacher name required");

        Teacher t = Teacher.builder().name(dto.getName()).subject(dto.getSubject()).build();
        Teacher saved = teacherRepository.save(t);
        kafkaProducer.sendAsync("teacher-events", String.valueOf(saved.getId()), "CREATED:" + saved.getId());
        return toDto(saved);
    }

    @Override
    @CacheEvict(value = "teacher", key = "#id")
    public TeacherDto getTeacher(Long id) {
        Teacher t = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERROR_TEACHER_NOT_FOUND", "Teacher not found"));
        return toDto(t);
    }

    @Override
    @CacheEvict("teachersAll")
    public List<TeacherDto> listTeachers() {
        return teacherRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    @CacheEvict(value = {"teacher", "teachersAll"}, allEntries = true)
    public TeacherDto updateTeacher(Long id, TeacherDto dto) {
        Teacher t = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERROR_TEACHER_NOT_FOUND", "Teacher not found"));
        if (dto.getName() != null && !dto.getName().isBlank()) t.setName(dto.getName());
        if (dto.getSubject() != null) t.setSubject(dto.getSubject());
        Teacher saved = teacherRepository.save(t);
        kafkaProducer.sendAsync("teacher-events", String.valueOf(saved.getId()), "UPDATED:" + saved.getId());
        return toDto(saved);
    }

    @Override
    @CacheEvict(value = {"teacher", "teachersAll"}, allEntries = true)
    public void deleteTeacher(Long id) {
        Teacher t = teacherRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("ERROR_TEACHER_NOT_FOUND", "Teacher not found"));
        List<Student> copy = new ArrayList<>(t.getStudents());
        for (Student s : copy) {
            s.setTeacher(null);
            studentRepository.save(s);
        }
        teacherRepository.deleteById(id);
        kafkaProducer.sendAsync("teacher-events", String.valueOf(id), "DELETED:" + id);
    }

    private TeacherDto toDto(Teacher t) {
        List<Long> studentIds = t.getStudents().stream().map(Student::getId).collect(Collectors.toList());
        return TeacherDto.builder()
                .id(t.getId())
                .name(t.getName())
                .subject(t.getSubject())
                .studentIds(studentIds)
                .build();
    }
}
