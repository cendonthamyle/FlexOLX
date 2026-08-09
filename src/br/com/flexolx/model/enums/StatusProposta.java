package br.com.flexolx.model.enums;

/**
 * Representa os possíveis estados do ciclo de vida de uma proposta
 * de compra no sistema FlexOLX.
 *
 * @author Thamyle Cendon
 * @version 1.0
 */
public enum StatusProposta {
    PENDENTE,
    CONTRAPROPOSTA_ENVIADA,
    ACEITA,
    RECUSADA,
    CANCELADA,
    EXPIRADA,
    CONCLUIDA
}