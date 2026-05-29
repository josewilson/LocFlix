package com.locflix.mapper;

import com.locflix.constant.VideoStatus;
import com.locflix.dto.response.VideoResponse;
import com.locflix.entity.Video;
import org.springframework.stereotype.Component;

@Component
public class VideoMapper {

    private static final String STREAM_URL_TEMPLATE = "/api/v1/videos/stream/%d/master.m3u8";

    public VideoResponse toResponse(Video video) {
        String streamUrl = video.getStatus() == VideoStatus.READY
                ? String.format(STREAM_URL_TEMPLATE, video.getId())
                : null;

        return VideoResponse.builder()
                .id(video.getId())
                .movieId(video.getMovie().getId())
                .movieTitle(video.getMovie().getTitle())
                .status(video.getStatus().name())
                .statusDisplay(video.getStatus().getDisplayName())
                .streamUrl(streamUrl)
                .durationSeconds(video.getDurationSeconds())
                .fileSizeBytes(video.getFileSizeBytes())
                .errorMessage(video.getErrorMessage())
                .createdAt(video.getCreatedAt())
                .updatedAt(video.getUpdatedAt())
                .build();
    }
}
