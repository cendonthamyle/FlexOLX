package br.com.flexolx.model.usuario;

import br.com.flexolx.model.enums.TipoUsuario;

/**
 * Representa o perfil de um corretor de imóveis.
 *
 * O corretor é o profissional responsável por intermediar negociações
 * entre compradores, vendedores e imobiliárias, devendo possuir um
 * registro válido no Conselho Regional de Corretores de Imóveis (CRECI).
 */
public class PerfilCorretor implements PerfilUsuario {
    private static final long serialVersionUID = 1L;
    
    /**
     * Número de registro do profissional no Conselho Regional de
     * Corretores de Imóveis (CRECI).
     */
    private String creci;

    public PerfilCorretor(String creci) {
        this.creci = creci != null ? creci.trim() : "";
    }

    @Override
    public TipoUsuario getTipo() { return TipoUsuario.CORRETOR; }

    @Override
    public void validar(ResultadoValidacao resultado) {
        if (creci == null || creci.isEmpty()) {
            resultado.adicionarErro("O registro CRECI é obrigatório para corretores.");
        }
    }

    public String getCreci() { return creci; }
    public void setCreci(String creci) {
        if (creci == null) {
            throw new IllegalArgumentException("O registro CRECI não pode ser nulo.");
        }

        this.creci = creci.trim();
    }

    @Override
    public String toString() { return "CRECI: " + creci; }
}