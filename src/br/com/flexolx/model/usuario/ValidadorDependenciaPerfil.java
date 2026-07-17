package br.com.flexolx.model.usuario;

import br.com.flexolx.model.enums.TipoUsuario;

/**
 * Interface responsável por validar se um perfil de usuário pode ser removido.
 *
 * Seu principal objetivo é desacoplar a regra de negócio da classe {@link Usuario},
 * delegando a verificação de dependências para outra camada da aplicação.
 *
 * Dessa forma, a classe Usuario permanece responsável apenas pelo gerenciamento
 * dos perfis, enquanto regras específicas — como verificar anúncios ativos,
 * contratos vinculados ou outras dependências — podem ser implementadas por
 * diferentes componentes do sistema.
 *
 * Por ser uma interface funcional, pode ser implementada tanto por uma classe
 * quanto por expressões lambda ou referências de método.
 */
@FunctionalInterface
public interface ValidadorDependenciaPerfil {
    /**
     * Verifica se o perfil informado pode ser removido do usuário.
     *
     * Caso existam dependências que impeçam a remoção (por exemplo,
     * imóveis cadastrados, anúncios ativos ou contratos associados),
     * a implementação deve lançar uma {@link IllegalStateException}.
     *
     * @param usuarioId identificador único do usuário.
     * @param tipo tipo do perfil que será removido.
     *
     * @throws IllegalStateException caso existam dependências que impeçam
     *                               a remoção do perfil.
     */
    void verificarDependencias(String usuarioId, TipoUsuario tipo) throws IllegalStateException;
}