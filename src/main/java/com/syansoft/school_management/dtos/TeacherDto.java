package com.syansoft.school_management.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TeacherDto {

    private Long id;
    private String name;
    private String subject;
    private List<Long> studentIds;
}
