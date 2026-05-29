package com.locflix.service.impl;

import com.locflix.dto.response.VideoResponse;
import com.locflix.entity.Video;
import com.locflix.exception.BusinessLogicException;
import com.locflix.exception.ResourceNotFoundException;
import com.locflix.mapper.VideoMapper;
import com.locflix.repository.MovieRepository;
import com.locflix.repository.VideoRepository;
import com.locflix.service.FfmpegService;
import com.locflix.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final MovieRepository movieRepository;
    private final FfmpegService ffmpegService;
    private final VideoMapper videoMapper;

    @Value("${locflix.storage.videos-path}")
    private String videosStoragePath;

    @Override
    public VideoResponse upload(Long movieId, MultipartFile file) {
        log.info("Upload de vídeo para filme ID: {}", movieId);

        var movie = movieRepository.findById(movieId)
                .orElseThrow(() -> ResourceNotFoundException.of("Filme", "ID", movieId));

        if (videoRepository.existsByMovieId(movieId)) {
            throw new BusinessLogicException("Este filme já possui um vídeo associado. Delete-o antes de enviar um novo.");
        }

        if (file.isEmpty()) {
            throw new BusinessLogicException("O arquivo de vídeo não pode estar vazio.");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf('.'))
                : ".mp4";

        String storedFilename = UUID.randomUUID() + extension;
        Path uploadDir = Paths.get(videosStoragePath, "originals");

        try {
            Files.createDirectories(uploadDir);
            Path destination = uploadDir.resolve(storedFilename);
            file.transferTo(destination);

            Video video = Video.builder()
                    .movie(movie)
                    .originalPath(destination.toString())
                    .fileSizeBytes(file.getSize())
                    .build();

            video = videoRepository.save(video);

            // Dispara conversão HLS de forma assíncrona
            ffmpegService.convertToHls(video);

            log.info("Vídeo salvo e conversão iniciada. Video ID: {}", video.getId());
            return videoMapper.toResponse(video);

        } catch (IOException e) {
            throw new BusinessLogicException("Erro ao salvar o arquivo de vídeo: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public VideoResponse getStatusByMovieId(Long movieId) {
        var video = videoRepository.findByMovieId(movieId)
                .orElseThrow(() -> ResourceNotFoundException.of("Vídeo", "movieId", movieId));
        return videoMapper.toResponse(video);
    }

    @Override
    @Transactional(readOnly = true)
    public String resolveSegmentPath(Long videoId, String segment) {
        var video = videoRepository.findById(videoId)
                .orElseThrow(() -> ResourceNotFoundException.of("Vídeo", "ID", videoId));

        Path hlsDir = Paths.get(videosStoragePath, "hls", String.valueOf(videoId));
        Path segmentPath = hlsDir.resolve(segment).normalize();

        // Garante que o segmento não sai do diretório HLS (path traversal prevention)
        if (!segmentPath.startsWith(hlsDir)) {
            throw new BusinessLogicException("Caminho de segmento inválido.");
        }

        return segmentPath.toString();
    }
}
