package com.example.onlineedusystem.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CommentResponse {
    private long id;
    private ProfileResponse user;
    private String comment;
    private LocalDate date;
}
