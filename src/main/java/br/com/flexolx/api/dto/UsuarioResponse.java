package br.com.flexolx.api.dto;

import br.com.flexolx.model.usuario.PerfilUsuario;
import br.com.flexolx.model.usuario.Usuario;

import java.util.List;

/** Representação JSON de um {@link Usuario}, sem expor a senha/hash. */
public record UsuarioResponse(
        String id,
        String nome,
        String email,
        String telefone,
        List<String> perfis
) {

    public static UsuarioResponse de(Usuario u) {
        List<String> perfis = u.getPerfis().stream()
                .map(PerfilUsuario::getTipo)
                .map(Enum::name)
                .toList();
        return new UsuarioResponse(u.getId(), u.getNome(), u.getEmail(), u.getTelefone(), perfis);
    }
}
