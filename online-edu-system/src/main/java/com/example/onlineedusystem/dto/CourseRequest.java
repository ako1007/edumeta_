package com.example.onlineedusystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CourseRequest {

    private String courseName;
    private Integer  duration;
    private String language;
    private String subtitles;
    private String sessions;
    private List<MultipartFile> resources;
    private Integer countResources;
    private String teacherEmail;
    private String info;
    private Double price;


}
