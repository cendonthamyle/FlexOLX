package br.com.flexolx.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Corpo de {@code POST /api/auth/register}.
 * {@code tipo} aceita: {@code CLIENTE} (padrão), {@code CORRETOR} ou
 * {@code PROPRIETARIO_DIRETO}. {@code creci} só é obrigatório para
 * {@code CORRETOR} (validado pela própria classe {@code PerfilCorretor}).
 */
public record RegisterRequest(
        @NotBlank(message = "O nome é obrigatório.") String nome,
        @NotBlank(message = "O e-mail é obrigatório.") String email,
        @NotBlank(message = "A senha é obrigatória.") String senha,
        String telefone,
        String tipo,
        String creci
) {
}
