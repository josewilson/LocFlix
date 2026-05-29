package com.locflix.service;

import com.locflix.dto.response.VideoResponse;
import org.springframework.web.multipart.MultipartFile;

/**
 * Serviço para gerenciamento de vídeos e streaming HLS.
 */
public interface VideoService {

    /**
     * Recebe o upload de um arquivo de vídeo e inicia o processamento HLS.
     *
     * @param movieId ID do filme
     * @param file    arquivo de vídeo
     * @return dados do vídeo criado
     */
    VideoResponse upload(Long movieId, MultipartFile file);

    /**
     * Retorna o status atual do processamento de um vídeo.
     *
     * @param movieId ID do filme
     * @return dados do vídeo com status atual
     */
    VideoResponse getStatusByMovieId(Long movieId);

    /**
     * Retorna o caminho absoluto do arquivo de segmento HLS para streaming.
     *
     * @param videoId ID do vídeo
     * @param segment nome do segmento (ex: master.m3u8, stream_0.m3u8, stream_0_001.ts)
     * @return caminho absoluto do arquivo
     */
    String resolveSegmentPath(Long videoId, String segment);
}
