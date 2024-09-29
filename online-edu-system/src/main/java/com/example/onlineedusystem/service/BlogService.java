package com.example.onlineedusystem.service;

import com.example.onlineedusystem.dto.ApiResponse;
import com.example.onlineedusystem.dto.BlogRequest;
import com.example.onlineedusystem.dto.JwtAuthenticationRequest;
import com.example.onlineedusystem.entity.Blog;
import com.example.onlineedusystem.entity.enums.Role;
import com.example.onlineedusystem.repository.BlogTepository;
import com.example.onlineedusystem.repository.CourseRepository;
import com.example.onlineedusystem.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogTepository blogTepository;
    private final TokenRepository tokenRepository;
    private final FileService fileService;
    private final CourseRepository courseRepository;

    public Blog addBlog(BlogRequest blogRequest, JwtAuthenticationRequest token) throws IOException {
        if (tokenRepository.findByToken(token.toString()).isEmpty() &&
                tokenRepository.findByToken(token.toString()).get().getUser().getRole().equals(Role.ROLE_ADMIN) ||
                tokenRepository.findByToken(token.toString()).get().getUser().getRole().equals(Role.ROLE_SUPER_ADMIN))
            throw new RuntimeException("Token not found");
        Blog blog = new Blog();
        blog.setTitle(blogRequest.getTitle());
        blog.setContent(blogRequest.getContent());
        List<String> resourses = new ArrayList<>();
        for (int i = 0; i < blogRequest.getImages().size(); i++)
            resourses.add(fileService.uploadFile(blogRequest.getImages().get(i)));
        blog.setResourcesId(resourses);
        blog.setDate(LocalDate.now());
        blog.setCourse(courseRepository.findById(blogRequest.getCourseId()).get());
        return  blogTepository.save(blog);
    }

    public Blog getBlog(JwtAuthenticationRequest request,Long blogId) {
        if (tokenRepository.findByToken(request.toString()).isEmpty()) throw new RuntimeException("Token not found");
        return blogTepository.findById(blogId).get();
    }

    public List<Blog> getAllBlogs(JwtAuthenticationRequest request) {
        if (tokenRepository.findByToken(request.toString()).isEmpty()) throw new RuntimeException("Token not found");
        return blogTepository.findAll();
    }

    public ApiResponse delete(JwtAuthenticationRequest token, Long blogId) {
        if (tokenRepository.findByToken(token.toString()).isEmpty() &&
                tokenRepository.findByToken(token.toString()).get().getUser().getRole().equals(Role.ROLE_ADMIN) ||
                tokenRepository.findByToken(token.toString()).get().getUser().getRole().equals(Role.ROLE_SUPER_ADMIN))
            throw new RuntimeException("Token not found");
        if (!blogTepository.existsById(blogId)) throw new RuntimeException("Blog not found");
        blogTepository.deleteById(blogId);
        return new ApiResponse(true, "Blog deleted successfully");
    }
}
