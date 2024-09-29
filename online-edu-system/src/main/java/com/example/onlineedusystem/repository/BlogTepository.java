package com.example.onlineedusystem.repository;

import com.example.onlineedusystem.entity.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogTepository extends JpaRepository<Blog, Long> {
}
