package com.movie_back.backend.repository;

import com.movie_back.backend.entity.VReviewDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VReviewDetailRepository extends JpaRepository<VReviewDetail, Long> {
}