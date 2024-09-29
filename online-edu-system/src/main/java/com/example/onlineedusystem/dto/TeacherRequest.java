package com.example.onlineedusystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherRequest {

    private long teacherId;
    private String fistName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String science;
    private String experience;
    private String bio;
    private MultipartFile image;

}
