package com.example.onlineedusystem.controller;

import com.example.onlineedusystem.dto.ApiResponse;
import com.example.onlineedusystem.dto.CommentCourseRequest;
import com.example.onlineedusystem.dto.CommentResponse;
import com.example.onlineedusystem.dto.CommentTeacherRequest;
import com.example.onlineedusystem.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v2/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/add/course/{token}")
    public CommentResponse addCommentToCourse(@PathVariable String token, @RequestBody CommentCourseRequest commentCourseRequest) throws IOException {
        return commentService.addCommentToCourse(token,commentCourseRequest);
    }

    @GetMapping("/get/{id}/{token}")
    public CommentResponse getComment(@PathVariable String token,@PathVariable long id) throws IOException {
        return commentService.getComment(token,id);
    }

    @GetMapping("/get/all/course/{id}/{token}")
    public List<CommentResponse> getAllCommentsToCourse(@PathVariable String token,@PathVariable long id) throws IOException {
        return commentService.getAllCommentsToCourse(token,id);
    }

    @PostMapping("/add/teacher/{token}")
    public CommentResponse addCommentToTeacher(@PathVariable String token, @RequestBody CommentTeacherRequest commentTeacherRequest) throws IOException {
        return commentService.addCommentToTeacher(token,commentTeacherRequest);
    }

    @GetMapping("/get/all/teacher/{id}/{token}")
    public List<CommentResponse> getAllCommentsToTeacher(@PathVariable String token,@PathVariable long id) throws IOException {
        return commentService.getAllCommentsToTeacher(token,id);
    }

    @DeleteMapping("/delete/{id}/{token}")
    public ApiResponse deleteComment(@PathVariable String token, @PathVariable long id) throws IOException {
        return commentService.delete(token,id);
    }
}
