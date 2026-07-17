package br.com.flexolx.model.usuario;

/**
 * Representa o perfil de um cliente da plataforma.
 *
 * O cliente é o usuário que utiliza o sistema para pesquisar,
 * favoritar, visitar e negociar imóveis disponíveis na plataforma.
 *
 * Esta implementação não possui atributos específicos além da
 * identificação do tipo de perfil.
 */
public class PerfilCliente implements PerfilUsuario {
    private static final long serialVersionUID = 1L;

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.CLIENTE; }

    @Override
    public void validar(ResultadoValidacao resultado) {
        // Clientes comuns não exigem documentação extra profissional no MVP.
    }

    @Override
    public String toString() { return "Cliente Comum"; }
}