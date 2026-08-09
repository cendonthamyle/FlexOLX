package br.com.flexolx.model.usuario;

import java.io.Serializable;
import br.com.flexolx.model.enums.TipoUsuario;

/**
 * Define o contrato para os perfis de usuário do sistema.
 *
 * Cada implementação representa um tipo específico de perfil,
 * contendo suas regras de validação e sua identificação por meio
 * de um {@link TipoUsuario}.
 *
 * Todas as classes que implementam esta interface devem ser
 * serializáveis para permitir sua persistência ou transmissão,
 * quando necessário.
 */
public interface PerfilUsuario extends Serializable {
    TipoUsuario getTipo();

    /**
     * Valida os dados específicos do perfil.
     *
     * Caso sejam encontradas inconsistências, elas devem ser
     * registradas no objeto {@code resultado}, permitindo que
     * múltiplos erros sejam reportados em uma única validação.
     *
     * @param resultado objeto responsável por armazenar os erros
     * encontrados durante a validação.
     */
    void validar(ResultadoValidacao resultado);
}