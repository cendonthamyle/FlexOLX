package br.com.flexolx.model.usuario;

/**
 * Representa o perfil de uma imobiliária.
 *
 * A imobiliária é a empresa responsável pelo gerenciamento e pela
 * divulgação de imóveis na plataforma, devendo possuir um Cadastro
 * Nacional da Pessoa Jurídica (CNPJ) válido.
 */
public class PerfilImobiliaria implements PerfilUsuario {
    private static final long serialVersionUID = 1L;
    
    private String cnpj;

    public PerfilImobiliaria(String cnpj) {
        this.cnpj = cnpj != null ? cnpj.replaceAll("\\D", "") : "";
    }

    @Override
    public TipoUsuario getTipo() {
        return TipoUsuario.IMOBILIARIA;
    }

    @Override
    public void validar(ResultadoValidacao resultado) {
        if (!validarCNPJ(cnpj)) {
            resultado.adicionarErro("CNPJ inválido de acordo com o cálculo de dígitos verificadores.");
        }
    }

    /**
     * Valida um número de CNPJ.
     *
     * A validação considera a quantidade de dígitos, elimina sequências
     * numéricas inválidas e verifica os dígitos verificadores conforme
     * o algoritmo oficial do CNPJ.
     *
     * @param cnpj número de CNPJ contendo apenas caracteres numéricos.
     * @return {@code true} caso o CNPJ seja válido; {@code false} caso
     * contrário.
     */
    private boolean validarCNPJ(String cnpj) {
        if (cnpj == null || cnpj.length() != 14) return false;

        // Elimina CNPJs com todos os números iguais conhecidos
        if (cnpj.matches("(\\d)\\1{13}")) return false;

        try {
            // Validação do primeiro dígito verificador
            int[] peso1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            int soma = 0;
            for (int i = 0; i < 12; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * peso1[i];
            }
            int r = soma % 11;
            int digito1 = (r < 2) ? 0 : 11 - r;

            // Validação do segundo dígito verificador
            int[] peso2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
            soma = 0;
            for (int i = 0; i < 13; i++) {
                soma += Character.getNumericValue(cnpj.charAt(i)) * peso2[i];
            }
            r = soma % 11;
            int digito2 = (r < 2) ? 0 : 11 - r;

            return digito1 == Character.getNumericValue(cnpj.charAt(12)) && 
                   digito2 == Character.getNumericValue(cnpj.charAt(13));
        } catch (Exception e) {
            return false;
        }
    }

    public String getCnpj() { return cnpj; }
    public void setCnpj(String cnpj) { this.cnpj = cnpj.replaceAll("\\D", ""); }

    @Override
    public String toString() { return "CNPJ: " + cnpj; }
}