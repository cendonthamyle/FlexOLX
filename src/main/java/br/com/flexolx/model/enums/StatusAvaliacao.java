package br.com.flexolx.model.enums;

/**
 * Enumeração que representa os possíveis estados do processo de avaliação de um anúncio ou imóvel.
 * * <p>Utilizado pelos avaliadores do sistema para controlar o fluxo de aprovação
 * antes que uma publicação seja ativada na plataforma.</p>
 * * @author Thamyle Cendon
 * @version 1.0
 */
public enum StatusAvaliacao {

    /**
     * Indica que o anúncio ou imóvel está aguardando a análise de um avaliador.
     */
    PENDENTE,

    /**
     * Indica que a avaliação foi concluída e o anúncio/imóvel atende a todos os requisitos para publicação.
     */
    APROVADA,

    /**
     * Indica que o anúncio/imóvel foi reprovado na avaliação por não cumprir as diretrizes do sistema.
     */
    REPROVADA
}
