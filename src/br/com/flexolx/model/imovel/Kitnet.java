package br.com.flexolx.model.imovel;

import br.com.flexolx.model.enums.TipoImovel;
import br.com.flexolx.model.usuario.Usuario;

import java.math.BigDecimal;

/**
 * Representa um imóvel do tipo Kitnet (ou estúdio/conjugado) no ecossistema FlexOLX.
 * <p>
 * Por definição conceitual deste modelo de negócios, uma {@code Kitnet} possui por padrão
 * 0 (zero) quartos separados na classe base, e não permite a alteração dessa quantidade.
 * </p>
 *
 * @author Thamyle Cendon
 * @version 1.0
 */
public class Kitnet extends Imovel {
    private static final long serialVersionUID = 1L;

    private final int quantidadeBanheiros;
    private final boolean mobiliada;

    /**
     * Constrói uma nova instância de {@code Kitnet}.
     *
     * @param titulo              Título do anúncio do imóvel.
     * @param descricao           Descrição do imóvel.
     * @param preco               Preço da kitnet.
     * @param vendedor            Usuário anunciante.
     * @param areaTotal           Área em metros quadrados (m²).
     * @param endereco            Endereço completo da kitnet.
     * @param quantidadeBanheiros Quantidade de banheiros (deve ser maior ou igual a 0).
     * @param mobiliada           {@code true} se o imóvel está mobiliado; {@code false} caso contrário.
     * @throws IllegalArgumentException Se {@code quantidadeBanheiros} for um número negativo.
     */
    public Kitnet(
            String titulo,
            String descricao,
            BigDecimal preco,
            Usuario vendedor,
            double areaTotal,
            String endereco,
            int quantidadeBanheiros,
            boolean mobiliada
    ) {
        super(
                titulo, descricao, preco, vendedor,
                areaTotal, 0, endereco,
                TipoImovel.KITNET
        );

        if (quantidadeBanheiros < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de banheiros não pode ser negativa."
            );
        }

        this.quantidadeBanheiros = quantidadeBanheiros;
        this.mobiliada = mobiliada;
    }

    /**
     * Altera as informações cadastrais da kitnet.
     * <p>
     * Sobrescreve o método herdado de {@link Imovel} para garantir a invariante de que
     * uma Kitnet obrigatoriamente mantém a quantidade de quartos zerada.
     * </p>
     *
     * @param solicitante       O usuário que está requisitando a edição (usualmente o proprietário).
     * @param titulo            Novo título do anúncio.
     * @param descricao         Nova descrição do imóvel.
     * @param preco             Novo preço.
     * @param areaTotal         Nova área total.
     * @param quantidadeQuartos Quantidade de quartos (deve obrigatoriamente ser 0).
     * @param endereco          Novo endereço.
     * @throws IllegalArgumentException Se a {@code quantidadeQuartos} fornecida for diferente de 0.
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
        if (quantidadeQuartos != 0) {
            throw new IllegalArgumentException(
                    "Uma kitnet não pode possuir quantidade de quartos diferente de zero."
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

    public int getQuantidadeBanheiros() {
        return quantidadeBanheiros;
    }

    public boolean isMobiliada() {
        return mobiliada;
    }
}