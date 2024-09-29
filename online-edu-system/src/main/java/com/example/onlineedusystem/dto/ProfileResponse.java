package com.example.onlineedusystem.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileResponse {

    private String fistName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private ResponseEntity<Resource> profilePicture;
    private Long postcode;


}
