package com.syansoft.school_management.repository;

import com.syansoft.school_management.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeacherRepository extends JpaRepository<Teacher, Long> {
}
