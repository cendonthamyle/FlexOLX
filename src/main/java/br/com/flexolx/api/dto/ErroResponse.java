package br.com.flexolx.api.dto;

/** Corpo de resposta padrão para erros: {@code {"erro": "mensagem"}}. */
public record ErroResponse(String erro) {
}
