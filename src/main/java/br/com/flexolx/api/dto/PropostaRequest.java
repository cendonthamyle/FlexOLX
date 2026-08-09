package br.com.flexolx.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

/** Corpo de {@code POST /api/propostas}. */
public record PropostaRequest(
        @NotBlank(message = "O id do imóvel é obrigatório.") String imovelId,
        @NotBlank(message = "O e-mail do comprador é obrigatório.") String email,
        @NotNull(message = "O valor ofertado é obrigatório.")
        @Positive(message = "O valor ofertado deve ser maior que zero.") BigDecimal valor,
        String formaPagamento,
        String mensagem
) {
}
