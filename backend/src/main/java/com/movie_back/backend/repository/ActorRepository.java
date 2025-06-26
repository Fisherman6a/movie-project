package com.movie_back.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.movie_back.backend.entity.Actor;
import java.util.List;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    // 新增：根据姓名模糊查询演员，忽略大小写
    List<Actor> findByNameContainingIgnoreCase(String name);
}