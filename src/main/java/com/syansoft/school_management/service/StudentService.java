package com.syansoft.school_management.service;

import com.syansoft.school_management.dtos.StudentDto;

import java.util.List;

public interface StudentService {

    StudentDto createStudent(StudentDto dto);

    StudentDto getStudent(Long id);

    List<StudentDto> listStudents();

    StudentDto updateStudent(Long id, StudentDto dto);

    void deleteStudent(Long id);
}
