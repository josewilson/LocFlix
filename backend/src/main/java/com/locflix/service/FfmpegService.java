package com.locflix.service;

import com.locflix.entity.Video;

/**
 * Serviço responsável pela conversão de vídeos para HLS usando ffmpeg.
 */
public interface FfmpegService {

    /**
     * Converte o vídeo original para formato HLS com múltiplas qualidades.
     * A conversão é assíncrona: atualiza o status da entidade Video diretamente.
     *
     * @param video entidade Video com o caminho do arquivo original
     */
    void convertToHls(Video video);

    /**
     * Verifica se o ffmpeg está disponível no sistema.
     *
     * @return true se o ffmpeg foi encontrado
     */
    boolean isFfmpegAvailable();
}
