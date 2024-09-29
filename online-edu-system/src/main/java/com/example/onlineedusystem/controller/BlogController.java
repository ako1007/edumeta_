package com.example.onlineedusystem.controller;

import com.example.onlineedusystem.dto.ApiResponse;
import com.example.onlineedusystem.dto.BlogRequest;
import com.example.onlineedusystem.dto.JwtAuthenticationRequest;
import com.example.onlineedusystem.entity.Blog;
import com.example.onlineedusystem.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/api/v1/blog")
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/add/{token}")
    public Blog addBlog(@RequestBody BlogRequest blogRequest, @PathVariable JwtAuthenticationRequest token) throws IOException {
        return blogService.addBlog(blogRequest,token);
    }

    @GetMapping("/get/{blogId}/{token}")
    public Blog getBlog(@PathVariable long blogId, @PathVariable JwtAuthenticationRequest token) throws IOException {
        return blogService.getBlog(token,blogId);
    }

    @GetMapping("/get/all/{token}")
    public List<Blog> getAllBlog(@PathVariable JwtAuthenticationRequest token) throws IOException {
        return blogService.getAllBlogs(token);
    }

    @DeleteMapping("/delete/{blogId}/{token}")
    public ApiResponse deleteBlog(@PathVariable long blogId, @PathVariable JwtAuthenticationRequest token) throws IOException {
        return blogService.delete(token, blogId);
    }
}
