package br.com.flexolx.api.dto;

import java.util.List;

/** Corpo de resposta de {@code GET /api/propostas?email=}. */
public record PropostasDoUsuarioResponse(
        List<PropostaResponse> enviadas,
        List<PropostaResponse> recebidas
) {
}
