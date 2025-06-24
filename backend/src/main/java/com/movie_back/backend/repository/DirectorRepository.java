package com.movie_back.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.movie_back.backend.entity.Director;
import java.util.List; // 引入 List

public interface DirectorRepository extends JpaRepository<Director, Long> {
    // 新增：根据姓名模糊查询导演，忽略大小写
    List<Director> findByNameContainingIgnoreCase(String name);
}