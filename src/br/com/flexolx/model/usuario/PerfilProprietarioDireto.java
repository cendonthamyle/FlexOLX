package br.com.flexolx.model.usuario;

/**
 * Representa o perfil de um proprietário direto.
 *
 * O proprietário direto é a pessoa física que anuncia e gerencia
 * seus próprios imóveis na plataforma, sem o intermédio de uma
 * imobiliária.
 *
 * Esta implementação não possui atributos específicos além da
 * identificação do tipo de perfil.
 */
public class PerfilProprietarioDireto implements PerfilUsuario {
    private static final long serialVersionUID = 1L;

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.PROPRIETARIO_DIRETO;
    }

    @Override
    public void validar(ResultadoValidacao resultado) {
        // Não há validações específicas para este perfil, pois ele
        // utiliza apenas os dados básicos do usuário.
        //
        // Regras de negócio, como limites de anúncios ou permissões
        // de utilização, são tratadas na camada de serviços.
    }

    @Override
    public String toString() {
        return "Proprietário Direto (Anunciante Autônomo)";
    }
}