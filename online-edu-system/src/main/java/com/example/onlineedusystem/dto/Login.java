package com.example.onlineedusystem.dto;

import com.example.onlineedusystem.entity.enums.Role;
import jakarta.persistence.Enumerated;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Login {

    private String email;
    private String password;
}
