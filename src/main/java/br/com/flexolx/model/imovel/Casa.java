package br.com.flexolx.model.imovel;

import br.com.flexolx.model.enums.TipoImovel;
import br.com.flexolx.model.usuario.Usuario;

import java.math.BigDecimal;

/**
 * Representa um imóvel do tipo Casa no ecossistema FlexOLX.
 * <p>
 * Esta classe é uma subclasse concreta de {@link Imovel} e adiciona características
 * próprias de residências térreas ou sobrados, tais como presença de quintal,
 * quantidade de banheiros e vagas de garagem.
 * </p>
 *
 * @author Thamyle Cendon
 * @version 1.0
 */
public class Casa extends Imovel {
    private static final long serialVersionUID = 1L;

    private final int quantidadeBanheiros;
    private final int vagasGaragem;
    private final boolean possuiQuintal;

    /**
     * Constrói uma nova instância de {@code Casa} efetuando a validação de não-negatividade
     * para os campos numéricos informados.
     *
     * @param titulo              Título do anúncio da casa.
     * @param descricao           Descrição detalhada do imóvel.
     * @param preco               Preço do imóvel.
     * @param vendedor            Usuário proprietário ou anunciante responsável.
     * @param areaTotal           Área construída ou total do terreno em metros quadrados (m²).
     * @param quantidadeQuartos   Número de quartos da casa.
     * @param endereco            Endereço completo onde a casa está situada.
     * @param quantidadeBanheiros Número de banheiros (deve ser maior ou igual a 0).
     * @param vagasGaragem        Número de vagas na garagem (deve ser maior ou igual a 0).
     * @param possuiQuintal       {@code true} se a casa tem quintal, {@code false} caso contrário.
     * @throws IllegalArgumentException Se a quantidade de banheiros ou vagas for negativa.
     */
    public Casa(
            String titulo,
            String descricao,
            BigDecimal preco,
            Usuario vendedor,
            double areaTotal,
            int quantidadeQuartos,
            String endereco,
            int quantidadeBanheiros,
            int vagasGaragem,
            boolean possuiQuintal
    ) {
        super(
                titulo, descricao, preco, vendedor,
                areaTotal, quantidadeQuartos, endereco,
                TipoImovel.CASA
        );

        validarNaoNegativo(quantidadeBanheiros, "A quantidade de banheiros");
        validarNaoNegativo(vagasGaragem, "A quantidade de vagas na garagem");

        this.quantidadeBanheiros = quantidadeBanheiros;
        this.vagasGaragem = vagasGaragem;
        this.possuiQuintal = possuiQuintal;
    }

    public int getQuantidadeBanheiros() {
        return quantidadeBanheiros;
    }

    public int getVagasGaragem() {
        return vagasGaragem;
    }

    public boolean isPossuiQuintal() {
        return possuiQuintal;
    }

    private static void validarNaoNegativo(int valor, String campo) {
        if (valor < 0) {
            throw new IllegalArgumentException(
                    campo + " não pode ser negativa."
            );
        }
    }
}