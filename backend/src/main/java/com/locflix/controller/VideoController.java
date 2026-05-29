package com.locflix.controller;

import com.locflix.constant.ApiConstant;
import com.locflix.dto.response.VideoResponse;
import com.locflix.service.VideoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;

@Slf4j
@RestController
@RequestMapping(ApiConstant.VIDEOS_BASE_PATH)
@RequiredArgsConstructor
@Tag(name = "Vídeos", description = "Upload, processamento e streaming HLS de vídeos")
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload/{movieId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Upload de vídeo", description = "Faz upload de um arquivo de vídeo e inicia a conversão HLS (Admin)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Upload recebido e processamento iniciado"),
            @ApiResponse(responseCode = "400", description = "Arquivo inválido"),
            @ApiResponse(responseCode = "404", description = "Filme não encontrado"),
            @ApiResponse(responseCode = "409", description = "Filme já possui vídeo")
    })
    public ResponseEntity<VideoResponse> upload(
            @PathVariable Long movieId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(videoService.upload(movieId, file));
    }

    @GetMapping("/{movieId}/status")
    @Operation(summary = "Status do vídeo", description = "Retorna o status do processamento HLS de um filme")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Status retornado"),
            @ApiResponse(responseCode = "404", description = "Vídeo não encontrado para este filme")
    })
    public ResponseEntity<VideoResponse> getStatus(@PathVariable Long movieId) {
        return ResponseEntity.ok(videoService.getStatusByMovieId(movieId));
    }

    @GetMapping("/stream/{videoId}/{segment:.+}")
    @Operation(summary = "Streaming HLS", description = "Serve arquivos .m3u8 e .ts para o player de vídeo")
    public ResponseEntity<Resource> stream(
            @PathVariable Long videoId,
            @PathVariable String segment) {

        String filePath = videoService.resolveSegmentPath(videoId, segment);
        Resource resource = new FileSystemResource(filePath);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        MediaType contentType = segment.endsWith(".m3u8")
                ? MediaType.parseMediaType("application/vnd.apple.mpegurl")
                : MediaType.parseMediaType("video/mp2t");

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-cache")
                .header(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "*")
                .contentType(contentType)
                .body(resource);
    }
}
