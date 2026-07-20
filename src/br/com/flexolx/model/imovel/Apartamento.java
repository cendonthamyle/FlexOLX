package br.com.flexolx.model.imovel;

import br.com.flexolx.model.enums.TipoImovel;
import br.com.flexolx.model.usuario.Usuario;

import java.math.BigDecimal;

/**
 * Representa um imóvel do tipo Apartamento no ecossistema FlexOLX.
 * <p>
 * Esta classe é uma subclasse concreta de {@link Imovel} e adiciona atributos específicos
 * de edifícios residenciais, tais como andar, vagas de garagem, presença de elevador
 * e valor do condomínio.
 * </p>
 * * @author Thamyle Cendon
 * @version 1.0
 */
public class Apartamento extends Imovel {
    private static final long serialVersionUID = 1L;

    private final int andar;
    private final int quantidadeBanheiros;
    private final int vagasGaragem;
    private final boolean possuiElevador;
    private final BigDecimal valorCondominio;

    /**
     * Constrói uma nova instância de {@code Apartamento} realizando as validações
     * necessárias dos campos específicos de um apartamento.
     *
     * @param titulo              Título descritivo do anúncio do apartamento.
     * @param descricao           Descrição detalhada do imóvel.
     * @param preco               Preço de venda/aluguel do apartamento.
     * @param vendedor            O usuário responsável pela publicação do anúncio.
     * @param areaTotal           Área total do imóvel em metros quadrados (m²).
     * @param quantidadeQuartos   Quantidade de quartos do apartamento.
     * @param endereco            Endereço completo da localização do imóvel.
     * @param andar               Andar do apartamento (deve ser maior ou igual a 0).
     * @param quantidadeBanheiros Número de banheiros (deve ser maior ou igual a 0).
     * @param vagasGaragem        Número de vagas de garagem (deve ser maior ou igual a 0).
     * @param possuiElevador      {@code true} se o prédio possui elevador, {@code false} caso contrário.
     * @param valorCondominio     Valor mensal do condomínio (não pode ser nulo ou negativo).
     * @throws IllegalArgumentException Se {@code andar}, {@code quantidadeBanheiros}, {@code vagasGaragem}
     * ou {@code valorCondominio} forem negativos ou inválidos.
     */
    public Apartamento(
            String titulo,
            String descricao,
            BigDecimal preco,
            Usuario vendedor,
            double areaTotal,
            int quantidadeQuartos,
            String endereco,
            int andar,
            int quantidadeBanheiros,
            int vagasGaragem,
            boolean possuiElevador,
            BigDecimal valorCondominio
    ) {
        super(
                titulo, descricao, preco, vendedor,
                areaTotal, quantidadeQuartos, endereco,
                TipoImovel.APARTAMENTO
        );

        if (andar < 0) {
            throw new IllegalArgumentException(
                    "O andar não pode ser negativo."
            );
        }

        if (quantidadeBanheiros < 0 || vagasGaragem < 0) {
            throw new IllegalArgumentException(
                    "Banheiros e vagas de garagem não podem ser negativos."
            );
        }

        if (valorCondominio == null || valorCondominio.signum() < 0) {
            throw new IllegalArgumentException(
                    "O valor do condomínio não pode ser negativo."
            );
        }

        this.andar = andar;
        this.quantidadeBanheiros = quantidadeBanheiros;
        this.vagasGaragem = vagasGaragem;
        this.possuiElevador = possuiElevador;
        this.valorCondominio = valorCondominio;
    }

    public int getAndar() {
        return andar;
    }

    public int getQuantidadeBanheiros() {
        return quantidadeBanheiros;
    }

    public int getVagasGaragem() {
        return vagasGaragem;
    }

    public boolean isPossuiElevador() {
        return possuiElevador;
    }

    public BigDecimal getValorCondominio() {
        return valorCondominio;
    }
}