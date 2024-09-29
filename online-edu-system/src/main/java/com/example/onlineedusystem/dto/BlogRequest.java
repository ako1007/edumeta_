package com.example.onlineedusystem.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class BlogRequest {


    private String title;
    private String content;
    private List<MultipartFile> images;
    private long courseId;

}
