package br.com.flexolx.model.usuario;

import br.com.flexolx.model.enums.TipoUsuario;

/**
 * Representa o perfil de um avaliador de imóveis.
 *
 * O avaliador é o profissional responsável pela realização de
 * avaliações técnicas de imóveis e deve possuir um registro
 * válido no Cadastro Nacional de Avaliadores Imobiliários (CNAI).
 */
public class PerfilAvaliador implements PerfilUsuario {
    private static final long serialVersionUID = 1L;
    
    /**
     * Número de registro do profissional no Cadastro Nacional de
     * Avaliadores Imobiliários (CNAI).
     *
     * Apenas caracteres numéricos são armazenados.
     */
    private String cnai;

    public PerfilAvaliador(String cnai) {
        this.cnai = cnai != null ? cnai.replaceAll("\\D", "") : "";
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.AVALIADOR;
    }

    @Override
    public void validar(ResultadoValidacao resultado) {
        // Exemplo simples: CNAI geralmente possui até 5-6 dígitos numéricos
        if (cnai == null || cnai.trim().isEmpty()) {
            resultado.adicionarErro("O registro CNAI é obrigatório para o perfil de Avaliador.");
        }
    }

    public String getCnai() { return cnai; }
    
    /**
     * Atualiza o registro CNAI do avaliador.
     *
     * Apenas dígitos são armazenados. O registro não pode ser nulo
     * nem ficar vazio após a normalização.
     *
     * @param cnai novo registro CNAI.
     * @throws IllegalArgumentException caso o registro seja nulo ou vazio.
     */
    public void setCnai(String cnai) {
        if (cnai == null) {
            throw new IllegalArgumentException(
                    "O registro CNAI não pode ser nulo."
            );
        }

        String cnaiNormalizado = cnai.replaceAll("\\D", "");

        if (cnaiNormalizado.isEmpty()) {
            throw new IllegalArgumentException(
                    "O registro CNAI é obrigatório."
            );
        }

        this.cnai = cnaiNormalizado;
    }

    @Override
    public String toString() {
        return "Avaliador Técnico | CNAI: " + cnai;
    }
}