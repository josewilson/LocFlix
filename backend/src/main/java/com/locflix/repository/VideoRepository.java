package com.locflix.repository;

import com.locflix.constant.VideoStatus;
import com.locflix.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

    Optional<Video> findByMovieId(Long movieId);

    boolean existsByMovieId(Long movieId);

    List<Video> findAllByStatus(VideoStatus status);
}
