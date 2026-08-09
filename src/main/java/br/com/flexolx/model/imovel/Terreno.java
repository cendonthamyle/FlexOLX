package br.com.flexolx.model.imovel;

import br.com.flexolx.model.enums.TipoImovel;
import br.com.flexolx.model.usuario.Usuario;

import java.math.BigDecimal;

/**
 * Representa um imóvel do tipo Terreno no ecossistema FlexOLX.
 * <p>
 * Ao contrário dos imóveis edifiados, o terreno possui dimensões de largura e comprimento,
 * e sua área total é automaticamente calculada a partir do produto dessas duas medidas
 * (largura × comprimento).
 * </p>
 *
 * @author Thamyle Cendon
 * @version 1.0
 */
public class Terreno extends Imovel {
    private static final long serialVersionUID = 1L;

    private final double largura;
    private final double comprimento;
    private final boolean plano;

    /**
     * Constrói uma nova instância de {@code Terreno}, validando as dimensões e
     * calculando automaticamente a área total do imóvel.
     *
     * @param titulo      Título do anúncio do terreno.
     * @param descricao   Descrição do terreno.
     * @param preco       Preço de venda.
     * @param vendedor    Usuário anunciante.
     * @param endereco    Endereço ou localização do lote/terreno.
     * @param largura     Largura em metros (deve ser maior que zero).
     * @param comprimento Comprimento em metros (deve ser maior que zero).
     * @param plano       {@code true} se o relevo do terreno é plano, {@code false} caso contrário.
     * @throws IllegalArgumentException Se {@code largura} ou {@code comprimento} forem menores
     * ou iguais a zero, ou não forem valores finitos.
     */
    public Terreno(
            String titulo,
            String descricao,
            BigDecimal preco,
            Usuario vendedor,
            String endereco,
            double largura,
            double comprimento,
            boolean plano
    ) {
        super(
                titulo, descricao, preco, vendedor,
                calcularArea(largura, comprimento),
                0,
                endereco,
                TipoImovel.TERRENO
        );

        this.largura = validarMedida(largura, "A largura");
        this.comprimento = validarMedida(comprimento, "O comprimento");
        this.plano = plano;
    }

    /**
     * Altera as informações gerais do terreno.
     * <p>
     * Sobrescreve o método herdado de {@link Imovel} para garantir a integridade dos dados:
     * a área total do terreno deve bater exatamente com a multiplicação da largura
     * pelo comprimento armazenados no objeto.
     * </p>
     *
     * @param solicitante       O usuário que solicita a alteração.
     * @param titulo            Novo título do anúncio.
     * @param descricao         Nova descrição.
     * @param preco             Novo preço.
     * @param areaTotal         Nova área total informada (deve coincidir com largura × comprimento).
     * @param quantidadeQuartos Número de quartos (deve ser zero).
     * @param endereco          Novo endereço.
     * @throws IllegalArgumentException Se a {@code areaTotal} informada divergir do cálculo
     * da área real do terreno.
     */
    @Override
    public void alterarInformacoes(
            Usuario solicitante,
            String titulo,
            String descricao,
            BigDecimal preco,
            double areaTotal,
            int quantidadeQuartos,
            String endereco
    ) {
        double areaCalculada = calcularArea(largura, comprimento);

        if (Double.compare(areaTotal, areaCalculada) != 0) {
            throw new IllegalArgumentException(
                    "A área total do terreno deve corresponder a largura × comprimento."
            );
        }

        super.alterarInformacoes(
                solicitante,
                titulo,
                descricao,
                preco,
                areaTotal,
                quantidadeQuartos,
                endereco
        );
    }

    public double getLargura() {
        return largura;
    }

    public double getComprimento() {
        return comprimento;
    }

    public boolean isPlano() {
        return plano;
    }

    private static double calcularArea(double largura, double comprimento) {
        return validarMedida(largura, "A largura")
                * validarMedida(comprimento, "O comprimento");
    }

    private static double validarMedida(double medida, String campo) {
        if (!Double.isFinite(medida) || medida <= 0) {
            throw new IllegalArgumentException(
                    campo + " deve ser maior que zero."
            );
        }
        return medida;
    }
}