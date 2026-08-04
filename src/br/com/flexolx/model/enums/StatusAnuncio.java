package br.com.flexolx.model.enums;

/**
 * Representa os estados possíveis do ciclo de vida de um anúncio no sistema FlexOLX.
 * <p>
 * Este enum é utilizado para controlar a visibilidade e o fluxo de negociação
 * dos itens cadastrados pelos anunciantes.
 * </p>
 * * @author Thamyle Cendon
 * @version 1.0
 */
public enum StatusAnuncio {

    /**
     * O anúncio está em rascunho, visível apenas para o anunciante.
     * Não aparece em buscas nem aceita propostas.
     */
    RASCUNHO,

    /**
     * O anúncio está ativo e publicado no catálogo.
     * Visível para todos os usuários e pronto para receber buscas e propostas.
     */
    ATIVO,

    /**
     * O anúncio foi temporariamente ocultado pelo anunciante.
     * Não aparece nos resultados de busca enquanto estiver neste estado.
     */
    PAUSADO,

    /**
     * O imóvel/item possui uma negociação em andamento ou proposta aceita.
     * Continua visível, mas sinalizado aos demais interessados.
     */
    RESERVADO,

    /**
     * O processo de venda foi concluído com sucesso.
     * O anúncio deixa de aceitar novas ofertas.
     */
    VENDIDO,

    /**
     * O imóvel foi alugado com sucesso.
     * Aplicável a anúncios da categoria de locação.
     */
    ALUGADO,

    /**
     * O anúncio foi cancelado ou finalizado pelo anunciante sem conclusão de negócio.
     */
    ENCERRADO
}