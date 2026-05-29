package com.locflix.service.impl;

import com.locflix.constant.VideoStatus;
import com.locflix.entity.Video;
import com.locflix.repository.VideoRepository;
import com.locflix.service.FfmpegService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Service
@RequiredArgsConstructor
public class FfmpegServiceImpl implements FfmpegService {

    private final VideoRepository videoRepository;

    @Value("${locflix.storage.videos-path}")
    private String videosStoragePath;

    @Override
    @Async
    public void convertToHls(Video video) {
        log.info("Iniciando conversão HLS para video ID: {}", video.getId());

        video.setStatus(VideoStatus.PROCESSING);
        videoRepository.save(video);

        try {
            Path hlsDir = Paths.get(videosStoragePath, "hls", String.valueOf(video.getId()));
            Files.createDirectories(hlsDir);

            String masterPlaylist = hlsDir.resolve("master.m3u8").toString();

            // Gera as 3 qualidades em paralelo via ffmpeg
            boolean success = runFfmpeg(video.getOriginalPath(), hlsDir.toString(), masterPlaylist);

            if (success) {
                video.setHlsPath(masterPlaylist);
                video.setStatus(VideoStatus.READY);
                video.setDurationSeconds(extractDuration(video.getOriginalPath()));
                log.info("Conversão HLS concluída para video ID: {}", video.getId());
            } else {
                video.setStatus(VideoStatus.ERROR);
                video.setErrorMessage("ffmpeg retornou erro durante a conversão");
                log.error("Falha na conversão HLS para video ID: {}", video.getId());
            }

        } catch (Exception e) {
            video.setStatus(VideoStatus.ERROR);
            video.setErrorMessage(e.getMessage());
            log.error("Erro inesperado na conversão HLS para video ID: {}", video.getId(), e);
        }

        videoRepository.save(video);
    }

    @Override
    public boolean isFfmpegAvailable() {
        try {
            Process process = new ProcessBuilder("ffmpeg", "-version")
                    .redirectErrorStream(true)
                    .start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    // ========== Private Helpers ==========

    private boolean runFfmpeg(String inputPath, String hlsDir, String masterPlaylist) throws IOException, InterruptedException {
        // Gera 3 renditions: 360p, 720p, 1080p com master playlist
        String[] cmd = {
            "ffmpeg", "-i", inputPath,
            // 360p
            "-map", "0:v", "-map", "0:a",
            "-c:v", "libx264", "-c:a", "aac",
            "-b:v:0", "800k", "-s:v:0", "640x360", "-profile:v:0", "baseline",
            // 720p
            "-map", "0:v", "-map", "0:a",
            "-b:v:1", "2800k", "-s:v:1", "1280x720", "-profile:v:1", "main",
            // 1080p
            "-map", "0:v", "-map", "0:a",
            "-b:v:2", "5000k", "-s:v:2", "1920x1080", "-profile:v:2", "high",
            // HLS settings
            "-var_stream_map", "v:0,a:0 v:1,a:1 v:2,a:2",
            "-master_pl_name", "master.m3u8",
            "-f", "hls",
            "-hls_time", "6",
            "-hls_list_size", "0",
            "-hls_segment_filename", hlsDir + "/stream_%v_%03d.ts",
            hlsDir + "/stream_%v.m3u8"
        };

        Process process = new ProcessBuilder(cmd)
                .redirectErrorStream(true)
                .start();

        int exitCode = process.waitFor();
        return exitCode == 0;
    }

    private Integer extractDuration(String inputPath) {
        try {
            Process process = new ProcessBuilder(
                "ffprobe", "-v", "error",
                "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1",
                inputPath
            ).redirectErrorStream(true).start();

            String output = new String(process.getInputStream().readAllBytes()).trim();
            process.waitFor();
            return (int) Double.parseDouble(output);
        } catch (Exception e) {
            log.warn("Não foi possível extrair duração do vídeo: {}", e.getMessage());
            return null;
        }
    }
}
