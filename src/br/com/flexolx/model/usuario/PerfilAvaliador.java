package br.com.flexolx.model.usuario;

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
     * Atualiza o número de registro no CNAI.
     *
     * Apenas os caracteres numéricos são armazenados.
     *
     * @param cnai novo registro do avaliador.
     */
    public void setCnai(String cnai) { this.cnai = cnai.replaceAll("\\D", ""); }

    @Override
    public String toString() {
        return "Avaliador Técnico | CNAI: " + cnai;
    }
}