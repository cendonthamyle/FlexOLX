package br.com.flexolx.api.dto;

import br.com.flexolx.model.proposta.Proposta;

import java.math.BigDecimal;

/** Representação JSON de uma {@link Proposta}. */
public record PropostaResponse(
        String id,
        String imovel,
        String proponente,
        BigDecimal valorOfertado,
        String formaPagamento,
        String mensagem,
        String status
) {

    public static PropostaResponse de(Proposta p) {
        return new PropostaResponse(
                p.getId().toString(),
                p.getImovel().getTitulo(),
                p.getProponente().getNome(),
                p.getValorOfertado(),
                p.getFormaPagamento().name(),
                p.getMensagem(),
                p.getStatus().name()
        );
    }
}
