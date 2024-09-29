package com.example.onlineedusystem.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentTeacherRequest {
    private long userId;
    private String content;
    private long teacherId;
}
