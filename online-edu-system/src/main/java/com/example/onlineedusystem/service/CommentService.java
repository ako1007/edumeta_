package com.example.onlineedusystem.service;

import com.example.onlineedusystem.dto.*;
import com.example.onlineedusystem.entity.Comments;
import com.example.onlineedusystem.entity.User;
import com.example.onlineedusystem.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final CourseRepository courseRepository;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final FileService fileService;
    private final TeacherRepository teacherRepository;

    public CommentResponse addCommentToCourse(String token, CommentCourseRequest commentCourseRequest) throws IOException {
        if (tokenRepository.findByToken(token).isEmpty()) throw new RuntimeException("Token not found");
        Comments comments = new Comments();
        comments.setUser(userRepository.findById(commentCourseRequest.getUserId()).get());
        comments.setContent(commentCourseRequest.getContent());
        comments.setCourse(courseRepository.findById(commentCourseRequest.getCourseId()).get());
        comments.setDate(LocalDate.now());
        return saveComment(comments);
    }

    public CommentResponse getComment(String token, long id) throws IOException {
        if (tokenRepository.findByToken(token).isEmpty()) throw new RuntimeException("Token not found");
        Comments comments1 = commentRepository.findById(id).get();
        CommentResponse commentCourseResponse = new CommentResponse();
        commentCourseResponse.setId(comments1.getId());
        User user = comments1.getUser();
        commentCourseResponse.setUser(getProfile(user));
        commentCourseResponse.setComment(comments1.getContent());
        commentCourseResponse.setDate(comments1.getDate());
        return commentCourseResponse;
    }

    public List<CommentResponse> getAllCommentsToCourse(String token, long id) throws IOException {
        if (tokenRepository.findByToken(token).isEmpty()) throw new RuntimeException("Token not found");
        List<Comments> all = commentRepository.findAll();
        List<Comments> comments = new ArrayList<>();
        for (int i = 0; i < all.size(); i++)
            if (all.get(i).getCourse().getId().equals(id)) comments.add(all.get(i));
        return getAll(comments);
    }

    public CommentResponse addCommentToTeacher(String token, CommentTeacherRequest commentTeacherRequest) throws IOException {
        if (tokenRepository.findByToken(token).isEmpty()) throw new RuntimeException("Token not found");
        Comments comments = new Comments();
        comments.setUser(userRepository.findById(commentTeacherRequest.getUserId()).get());
        comments.setContent(commentTeacherRequest.getContent());
        comments.setDate(LocalDate.now());
        comments.setTeacher(teacherRepository.findById(commentTeacherRequest.getTeacherId()).get());
        return saveComment(comments);
    }

    public List<CommentResponse> getAllCommentsToTeacher(String token, long id) throws IOException {
        if (tokenRepository.findByToken(token).isEmpty()) throw new RuntimeException("Token not found");
        List<Comments> all = commentRepository.findAll();
        List<Comments> comments = new ArrayList<>();
        for (int i = 0; i < all.size(); i++)
            if (all.get(i).getTeacher().getId().equals(id)) comments.add(all.get(i));
        return getAll(comments);
    }

    public List<CommentResponse> getAll(List<Comments> comments) throws IOException {
        List<CommentResponse> commentCourseResponses = new ArrayList<>();
        for (int i = 0; i < comments.size(); i++) {
            commentCourseResponses.add(saveComment(comments.get(i)));
        }
        return commentCourseResponses;
    }

    public CommentResponse saveComment(Comments comments) throws IOException {
        Comments comments1 = commentRepository.save(comments);
        CommentResponse commentCourseResponse = new CommentResponse();
        commentCourseResponse.setId(comments1.getId());
        User user = comments1.getUser();
        commentCourseResponse.setUser(getProfile(user));
        commentCourseResponse.setComment(comments1.getContent());
        commentCourseResponse.setDate(comments1.getDate());
        return commentCourseResponse;
    }

    public ProfileResponse getProfile(User user) throws IOException {
        ProfileResponse profile = new ProfileResponse();
        profile.setFistName(user.getFirstName());
        profile.setLastName(user.getLastName());
        profile.setEmail(user.getEmail());
        profile.setPhoneNumber(user.getPhoneNumber());
        profile.setProfilePicture(fileService.getFile(user.getMediaId()));
        profile.setPostcode(user.getPostcode());
        return profile;
    }


    public ApiResponse delete(String token, long id) {
        if (tokenRepository.findByToken(token).isEmpty()) throw new RuntimeException("Token not found");
        if (commentRepository.findById(id).isEmpty()) throw new RuntimeException("Comment not found");
        commentRepository.deleteById(id);
        return new ApiResponse(true,"Comment successfily deleted!");
    }
}
