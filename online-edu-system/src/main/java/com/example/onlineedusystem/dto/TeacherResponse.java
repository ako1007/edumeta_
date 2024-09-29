package com.example.onlineedusystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TeacherResponse {

    private long id;
    private String fistName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private String science;
    private String experience;
    private String bio;
    private ResponseEntity<Resource> profilePicture;
    private List<String> courses;
}
