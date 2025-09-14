package com.syansoft.school_management.service;

import com.syansoft.school_management.dtos.TeacherDto;

import java.util.List;

public interface TeacherService {

    TeacherDto createTeacher(TeacherDto dto);

    TeacherDto getTeacher(Long id);

    List<TeacherDto> listTeachers();

    TeacherDto updateTeacher(Long id, TeacherDto dto);

    void deleteTeacher(Long id);
}
