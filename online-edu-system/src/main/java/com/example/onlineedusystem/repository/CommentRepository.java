package com.example.onlineedusystem.repository;

import com.example.onlineedusystem.entity.Comments;
import com.example.onlineedusystem.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {


}
