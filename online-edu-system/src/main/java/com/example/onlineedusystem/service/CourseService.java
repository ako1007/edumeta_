package com.example.onlineedusystem.service;

import com.example.onlineedusystem.dto.*;
import com.example.onlineedusystem.entity.Course;
import com.example.onlineedusystem.entity.Teacher;
import com.example.onlineedusystem.entity.Token;
import com.example.onlineedusystem.entity.enums.Role;
import com.example.onlineedusystem.repository.CourseRepository;
import com.example.onlineedusystem.repository.TeacherRepository;
import com.example.onlineedusystem.repository.TokenRepository;
import com.example.onlineedusystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final UserRepository userRepository;
    private final TeacherRepository teacherRepository;
    private final CourseRepository courseRepository;
    private final TokenRepository tokenRepository;
    private final FileService fileService;

    public ApiResponse addCourse(CourseRequest course, JwtAuthenticationRequest token) throws IOException {
        foundToken(token);
        Course course1 = new Course();
        course1.setName(course.getCourseName());
        Period months = Period.ofMonths(course.getDuration());
        course1.setDuration(months);
        course1.setLanguages(course.getLanguage());
        course1.setSubtitles(course.getSubtitles());
        course1.setSession(course.getSessions());
        if (course.getResources() != null) {
            List<String> resources = new ArrayList<>();
            for (int i = 0; i < course.getResources().size(); i++)
                resources.add(fileService.uploadFile(course.getResources().get(i)));
            course1.setResourcesId(resources);
        }
        course1.setCountResources(course.getCountResources());
        Optional<Teacher> teacher = teacherRepository.findByEmail(course.getTeacherEmail());
        course1.setTeacher(teacher.get());
        course1.setInfo(course.getInfo());
        course1.setPrice(course.getPrice());
        courseRepository.save(course1);
        return new ApiResponse(true, "Course added successfully");
    }

    public ApiResponse addResources(Long courseId, MultipartFile resourse, JwtAuthenticationRequest token) throws IOException {
        foundToken(token);
        if (tokenRepository.findByToken(token.toString()).get().getUser().getRole().equals(Role.ROLE_TEACHER))
            throw new RuntimeException("Token not found");
        String resourseId = fileService.uploadFile(resourse);
        Course course = courseRepository.findById(courseId).get();
        if (course == null) throw new RuntimeException("Course not found");
        course.getResourcesId().add(resourseId);
        course.setResourcesId(course.getResourcesId());
        courseRepository.save(course);
        return new ApiResponse(true, "Resource added successfully");
    }

    public CourseResponse getCourse(Long courseId) throws IOException {
        Course course = courseRepository.findById(courseId).get();
        if (course == null) throw new RuntimeException("Course not found");
        return getCourseResponse(course);
    }

    public List<CourseResponse> getAllCourses(JwtAuthenticationRequest tokenRequest) throws IOException {
        Token token = tokenRepository.findByToken(tokenRequest.toString()).get();
        if (token == null) throw new RuntimeException("Token not found");
        List<CourseResponse> courseResponsesList = new ArrayList<>();
        for (int i = 0; i < courseRepository.findAll().size(); i++) {
            Course course = new Course();
            courseResponsesList.add(getCourseResponse(course));
        }
        return courseResponsesList;
    }

    public ApiResponse editCourse(Long courseId, CourseRequest courseRequest, JwtAuthenticationRequest token) throws IOException {
        foundToken(token);
        Course course = courseRepository.findById(courseId).get();
        if (course == null) throw new RuntimeException("Course not found");
        if (courseRequest.getCourseName() != null) course.setName(courseRequest.getCourseName());
        if (courseRequest.getDuration() != null) course.setDuration(Period.ofMonths(courseRequest.getDuration()));
        if (courseRequest.getLanguage() != null) course.setLanguages(courseRequest.getLanguage());
        if (courseRequest.getSubtitles() != null) course.setSubtitles(courseRequest.getSubtitles());
        if (courseRequest.getSessions() != null) course.setSession(courseRequest.getSessions());
        if (courseRequest.getCountResources() != null) course.setCountResources(courseRequest.getCountResources());
        if (courseRequest.getInfo() != null) course.setInfo(courseRequest.getInfo());
        if (courseRequest.getTeacherEmail() != null) {
            Teacher teacher = teacherRepository.findByEmail(course.getTeacher().getEmail()).get();
            course.setTeacher(teacher);
        }
        if (courseRequest.getPrice() != null) course.setPrice(courseRequest.getPrice());
        courseRepository.save(course);
        return new ApiResponse(true, "Course updated successfully");
    }

    public ApiResponse deleteCourse(Long courseId, JwtAuthenticationRequest token) throws IOException {
        foundToken(token);
        Course course = courseRepository.findById(courseId).get();
        if (course == null) throw new RuntimeException("Course not found");
        courseRepository.delete(course);
        return new ApiResponse(true, "Course deleted successfully");
    }

    public void foundToken(JwtAuthenticationRequest token) {
        if (tokenRepository.findByToken(token.toString()).isEmpty() && !tokenRepository.findByToken(token.toString()).get().getUser().getRole().equals(Role.ROLE_ADMIN) || !tokenRepository.findByToken(token.toString()).get().getUser().getRole().equals(Role.ROLE_SUPER_ADMIN))
            throw new RuntimeException("Token not found");
    }

    public CourseResponse getCourseResponse(Course course) throws IOException {
        CourseResponse courseResponse = new CourseResponse();
        Teacher teacher = teacherRepository.findByEmail(course.getTeacher().getEmail()).get();
        courseResponse.setId(course.getId());
        courseResponse.setCourseName(course.getName());
        courseResponse.setDuration(course.getDuration().getMonths());
        courseResponse.setLanguage(course.getLanguages());
        courseResponse.setSubtitles(course.getSubtitles());
        courseResponse.setSessions(course.getSession());
        List<ResponseEntity<Resource>> resourses = new ArrayList<>();
        for (int i = 0; i < course.getResourcesId().size(); i++) {
            resourses.add(fileService.getFile(course.getResourcesId().get(i)));
        }
        courseResponse.setResources(resourses);
        courseResponse.setCountResources(course.getCountResources());
        if (teacher == null) throw new RuntimeException("Teacher not found");
        List<String> teacherCourses = new ArrayList<>();
        for (int i1 = 0; i1 < teacher.getCourse().size(); i1++)
            teacherCourses.add(teacher.getCourse().get(i1).getName());
        TeacherResponse teacherResponse = new TeacherResponse(teacher.getId(), teacher.getFirstName(), teacher.getLastName(), teacher.getEmail(), teacher.getPhoneNumber(), teacher.getScience(), teacher.getExperience(), teacher.getBio(), fileService.getFile(teacher.getPicture()), teacherCourses);
        courseResponse.setTeacher(teacherResponse);
        courseResponse.setInfo(course.getInfo());
        courseResponse.setPrice(course.getPrice());
        return courseResponse;
    }
}
