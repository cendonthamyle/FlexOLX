package br.com.flexolx.model.usuario;

import br.com.flexolx.model.enums.TipoUsuario;

/**
 * Representa o perfil de administrador do sistema.
 *
 * O administrador possui privilégios para acessar funcionalidades
 * de gerenciamento, configuração e manutenção da plataforma.
 *
 * Esta implementação não possui atributos específicos além do tipo
 * de perfil, sendo responsável apenas por identificar o usuário
 * como administrador.
 */
public class PerfilAdministrador implements PerfilUsuario {
    private static final long serialVersionUID = 1L;

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.ADMINISTRADOR;
    }

    /**
     * Realiza a validação dos dados específicos do perfil.
     *
     * Nesta implementação não existem informações adicionais a serem
     * validadas, pois o perfil de administrador não possui atributos
     * próprios no MVP.
     *
     * Em ambientes reais, este método pode incluir regras como
     * autorização para criação de administradores ou outras
     * validações de segurança.
     *
     * @param resultado objeto responsável por armazenar possíveis
     * erros de validação.
     */
    @Override
    public void validar(ResultadoValidacao resultado) {
        // Não há validações específicas para o perfil de administrador,
        // pois ele não possui atributos próprios no MVP.
        //
        // Em uma implementação completa, este método poderá validar regras
        // de negócio relacionadas à criação e ao gerenciamento de usuários
        // com privilégios administrativos.
    }

    @Override
    public String toString() {
        return "Administrador do Sistema (Superuser)";
    }
}