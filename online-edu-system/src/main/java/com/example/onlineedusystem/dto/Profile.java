package com.example.onlineedusystem.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Profile {

    private String fistName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private MultipartFile profilePicture;
    private Long postcode;


}
