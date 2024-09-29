package com.example.onlineedusystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CourseResponse {
    private long id;
    private String courseName;
    private int duration;
    private String language;
    private String subtitles;
    private String sessions;
    private List<ResponseEntity<Resource>> resources;
    private int countResources;
    private TeacherResponse teacher;
    private String info;
    private Double price;
}
