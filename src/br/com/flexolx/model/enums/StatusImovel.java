package br.com.flexolx.model.enums;

/**
 * Enumeração que define a condição física ou operacional do imóvel no sistema.
 * * <p>Utilizado para sinalizar aos usuários e compradores a disponibilidade do bem
 * para negociação ou habitabilidade.</p>
 * * @author Thamyle Cendon
 * @version 1.0
 */
public enum StatusImovel {

    /**
     * O imóvel está apto e pronto para ser negociado, visitado ou ocupado.
     */
    DISPONIVEL,

    /**
     * O imóvel encontra-se em processo de reforma ou manutenção, podendo limitar negociações imediatas.
     */
    EM_REFORMA
}