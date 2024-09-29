package com.example.onlineedusystem.controller;

import com.example.onlineedusystem.dto.ApiResponse;
import com.example.onlineedusystem.dto.CourseRequest;
import com.example.onlineedusystem.dto.CourseResponse;
import com.example.onlineedusystem.dto.JwtAuthenticationRequest;
import com.example.onlineedusystem.entity.Course;
import com.example.onlineedusystem.service.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v2/course")
public class CourseController {

    private final CourseService courseService;

    @PostMapping("/add/{token}")
    public ApiResponse addCourse(@RequestBody CourseRequest course, @PathVariable JwtAuthenticationRequest token) throws IOException {
        return courseService.addCourse(course, token);
    }

    @PostMapping("/add/resource/{courseId}/{token}")
    public ApiResponse addCourseResource(@RequestBody MultipartFile resourse,@PathVariable long courseId, @PathVariable JwtAuthenticationRequest token) throws IOException {
        return courseService.addResources(courseId,resourse,token);
    }

    @GetMapping("/get/{courseId}")
    public CourseResponse getCourse(@PathVariable long courseId) throws IOException {
        return courseService.getCourse(courseId);
    }

    @GetMapping("/get/all/{token}")
    public List<CourseResponse> getCourseAll(@PathVariable JwtAuthenticationRequest token) throws IOException {
        return courseService.getAllCourses(token);
    }

    @PutMapping("/edit/{courseId}/{token}")
    public ApiResponse ediitCourse(@RequestBody CourseRequest course, @PathVariable long courseId, @PathVariable JwtAuthenticationRequest token) throws IOException {
        return courseService.editCourse(courseId,course,token);
    }

    @DeleteMapping("/delete/{courseId}/{token}")
    public ApiResponse deleteCourse(@PathVariable long courseId, @PathVariable JwtAuthenticationRequest token) throws IOException {
        return courseService.deleteCourse(courseId,token);
    }
}
