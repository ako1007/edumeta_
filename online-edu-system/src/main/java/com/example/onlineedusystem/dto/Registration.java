package com.example.onlineedusystem.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Registration {

    private String name;
    private String email;
    private String password;
    private String repassword;
}
