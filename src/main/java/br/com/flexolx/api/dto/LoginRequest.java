package br.com.flexolx.api.dto;

import jakarta.validation.constraints.NotBlank;

/** Corpo de {@code POST /api/auth/login}. */
public record LoginRequest(
        @NotBlank(message = "O e-mail é obrigatório.") String email,
        @NotBlank(message = "A senha é obrigatória.") String senha
) {
}
