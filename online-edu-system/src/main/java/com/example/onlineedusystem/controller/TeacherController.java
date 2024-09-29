package com.example.onlineedusystem.controller;

import com.example.onlineedusystem.dto.*;
import com.example.onlineedusystem.service.TeacherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v2/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    @GetMapping("/get/{token}/{teacherId}")
   public TeacherResponse getTeacher(@PathVariable JwtAuthenticationRequest token, @PathVariable Long teacherId) throws IOException {
       return teacherService.getTeacher(token,teacherId);
   }

   @GetMapping("/get/{token}")
    public List<TeacherResponse> getAllTeachers(@PathVariable JwtAuthenticationRequest token) throws IOException {
        return teacherService.getAllTeachers(token);
    }

    @PutMapping("/edit/{token}")
    public ApiResponse editTeacher(@PathVariable JwtAuthenticationRequest token, @RequestBody TeacherRequest request) throws IOException {
        return teacherService.editTeacher(token,request);

    }

    @DeleteMapping("/delete/{token}/{teacherId}")
    public ApiResponse deleteTeacher(@PathVariable JwtAuthenticationRequest token, @PathVariable Long teacherId) throws IOException {
        return teacherService.deleteTeacher(token,teacherId );
    }


}
