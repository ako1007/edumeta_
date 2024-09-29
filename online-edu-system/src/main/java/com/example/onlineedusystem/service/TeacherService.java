package com.example.onlineedusystem.service;

import com.example.onlineedusystem.dto.ApiResponse;
import com.example.onlineedusystem.dto.JwtAuthenticationRequest;
import com.example.onlineedusystem.dto.TeacherRequest;
import com.example.onlineedusystem.dto.TeacherResponse;
import com.example.onlineedusystem.entity.Teacher;
import com.example.onlineedusystem.entity.enums.Role;
import com.example.onlineedusystem.repository.TeacherRepository;
import com.example.onlineedusystem.repository.TokenRepository;
import com.example.onlineedusystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class TeacherService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private TokenRepository tokenRepository;
    private final FileService fileService;

    public TeacherResponse getTeacher(JwtAuthenticationRequest jwtAuthenticationRequest, Long teacherId) throws IOException {
        if (tokenRepository.findByToken(jwtAuthenticationRequest.toString()).isEmpty())
            throw new RuntimeException("Token not found");
        Teacher teacher = teacherRepository.findById(teacherId).get();
        if (teacher == null) throw new RuntimeException("Teacher not found");
        return teacherCourses(teacher);
    }

    public List<TeacherResponse> getAllTeachers(JwtAuthenticationRequest tokenRequest) throws IOException {
        if (tokenRepository.findByToken(tokenRequest.toString()).isEmpty())
            throw new RuntimeException("Token not found");
        List<Teacher> teachers = teacherRepository.findAll();
        List<TeacherResponse> teacherResponses = new ArrayList<>();
        for (Teacher teacher : teachers) {
            teacherResponses.add(teacherCourses(teacher));
        }
        return teacherResponses;
    }

    public ApiResponse editTeacher(JwtAuthenticationRequest jwtAuthenticationRequest, TeacherRequest teacherRequest) throws IOException {
        if (tokenRepository.findByToken(jwtAuthenticationRequest.toString()).isEmpty() &&
                tokenRepository.findByToken(teacherRequest.toString()).get().getUser().getRole().equals(Role.ROLE_ADMIN) ||
                tokenRepository.findByToken(teacherRequest.toString()).get().getUser().getRole().equals(Role.ROLE_SUPER_ADMIN) ||
                tokenRepository.findByToken(teacherRequest.toString()).get().getUser().getRole().equals(Role.ROLE_TEACHER))
            throw new RuntimeException("Token not found");
        Teacher teacher = teacherRepository.findById(teacherRequest.getTeacherId()).get();
        if (teacher == null) throw new RuntimeException("Teacher not found");
        if (teacherRequest.getFistName() != null) teacher.setFirstName(teacherRequest.getFistName());
        if (teacherRequest.getLastName() != null) teacher.setLastName(teacherRequest.getLastName());
        if (teacherRequest.getEmail() != null) teacher.setEmail(teacherRequest.getEmail());
        if (teacherRequest.getPhoneNumber() != null) teacher.setPhoneNumber(teacherRequest.getPhoneNumber());
        if (teacherRequest.getScience() != null) teacher.setScience(teacherRequest.getScience());
        if (teacherRequest.getExperience() != null) teacher.setExperience(teacherRequest.getExperience());
        if (teacherRequest.getBio() != null) teacher.setBio(teacherRequest.getBio());
        if (teacherRequest.getImage() != null) teacher.setPicture(fileService.uploadFile(teacherRequest.getImage()));
        teacherRepository.save(teacher);
        return new ApiResponse(true, "Teacher Edited");
    }

    public ApiResponse deleteTeacher(JwtAuthenticationRequest teacherRequest, Long teacherId) throws IOException {
        if (tokenRepository.findByToken(teacherRequest.toString()).isEmpty() &&
                tokenRepository.findByToken(teacherRequest.toString()).get().getUser().getRole().equals(Role.ROLE_ADMIN) ||
                tokenRepository.findByToken(teacherRequest.toString()).get().getUser().getRole().equals(Role.ROLE_SUPER_ADMIN) ||
                tokenRepository.findByToken(teacherRequest.toString()).get().getUser().getRole().equals(Role.ROLE_TEACHER))
            throw new RuntimeException("Token not found");
        if (teacherRepository.findById(teacherId).isEmpty()) throw new RuntimeException("Teacher not found");
        teacherRepository.deleteById(teacherId);
        return new ApiResponse(true, "Teacher Deleted");
    }

    public  TeacherResponse teacherCourses(Teacher teacher) throws IOException {
        TeacherResponse teacherResponse = new TeacherResponse();
        teacherResponse.setId(teacher.getId());
        teacherResponse.setFistName(teacher.getFirstName());
        teacherResponse.setLastName(teacher.getLastName());
        teacherResponse.setEmail(teacher.getEmail());
        teacherResponse.setPhoneNumber(teacher.getPhoneNumber());
        teacherResponse.setScience(teacher.getScience());
        teacherResponse.setExperience(teacher.getExperience());
        teacherResponse.setBio(teacher.getBio());
        teacherResponse.setProfilePicture(fileService.getFile(teacher.getPicture()));
        List<String> teacherCourses = new ArrayList<>();
        for (int i = 0; i < teacher.getCourse().size(); i++)
            teacherCourses.add(teacher.getCourse().get(i).toString());
        teacherResponse.setCourses(teacherCourses);
        return teacherResponse;
    }

}
