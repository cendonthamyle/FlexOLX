package br.com.flexolx.model.imovel;

import br.com.flexolx.model.enums.StatusAnuncio;
import br.com.flexolx.model.enums.StatusAvaliacao;
import br.com.flexolx.model.enums.StatusImovel;
import br.com.flexolx.model.enums.TipoImovel;
import br.com.flexolx.model.enums.TipoUsuario;
import br.com.flexolx.model.item.Item;
import br.com.flexolx.model.usuario.Usuario;

import java.math.BigDecimal;

/**
 * Representa a estrutura abstrata especializada para anúncios do segmento imobiliário.
 * <p>
 * Esta classe estende {@link Item} e adiciona propriedades e comportamentos específicos
 * do domínio imobiliário, tais como medição de área, quantitativo de quartos, localização,
 * tipo do imóvel, controle do fluxo de avaliação por peritos e gerenciamento do ciclo de reforma.
 * </p>
 * * @author Thamyle Cendon
 * @version 1.0
 */
public abstract class Imovel extends Item {
    private static final long serialVersionUID = 2L;

    private double areaTotal;
    private int quantidadeQuartos;
    private String endereco;
    private final TipoImovel tipoImovel;

    private StatusAvaliacao statusAvaliacao;
    private StatusImovel statusConservacao;

    /**
     * Construtor protegido para inicialização de instâncias das subclasses concretas de imóvel.
     *
     * @param titulo            Título chamativo e explicativo do anúncio.
     * @param descricao         Descrição detalhada contendo especificações adicionais do imóvel.
     * @param preco             Valor financeiro inicial estipulado para a negociação do imóvel.
     * @param vendedor          Usuário responsável pela publicação e propriedade do anúncio.
     * @param areaTotal         Área total construída/útil em metros quadrados (deve ser estritamente positiva).
     * @param quantidadeQuartos Número de quartos do imóvel (não pode ser negativo).
     * @param endereco          Endereço do imóvel (não pode ser nulo nem composto apenas por espaços).
     * @param tipoImovel        Classificação do imóvel segundo o {@link TipoImovel}.
     * @throws IllegalArgumentException Se qualquer parâmetro fornecido violar as regras de validação estipuladas.
     */
    protected Imovel(
            String titulo,
            String descricao,
            BigDecimal preco,
            Usuario vendedor,
            double areaTotal,
            int quantidadeQuartos,
            String endereco,
            TipoImovel tipoImovel
    ) {
        super(titulo, descricao, preco, vendedor);

        validarArea(areaTotal);
        validarQuartos(quantidadeQuartos);
        validarEndereco(endereco);

        if (tipoImovel == null) {
            throw new IllegalArgumentException(
                    "O tipo do imóvel é obrigatório."
            );
        }

        this.areaTotal = areaTotal;
        this.quantidadeQuartos = quantidadeQuartos;
        this.endereco = endereco.trim();
        this.tipoImovel = tipoImovel;

        this.statusAvaliacao = StatusAvaliacao.PENDENTE;
        this.statusConservacao = StatusImovel.DISPONIVEL;
    }

    public double getAreaTotal() {
        return areaTotal;
    }

    public int getQuantidadeQuartos() {
        return quantidadeQuartos;
    }

    public String getEndereco() {
        return endereco;
    }

    public TipoImovel getTipoImovel() {
        return tipoImovel;
    }

    public StatusAvaliacao getStatusAvaliacao() {
        return statusAvaliacao;
    }

    public StatusImovel getStatusConservacao() {
        return statusConservacao;
    }

    /**
     * Aprova a avaliação técnica do imóvel por meio de um usuário credenciado.
     *
     * @param avaliador O usuário com perfil de avaliador responsável pela aprovação.
     * @throws IllegalArgumentException Se o avaliador for nulo ou inválido.
     * @throws SecurityException        Se o usuário não possuir perfil de avaliador ou for o próprio proprietário do imóvel.
     * @throws IllegalStateException     Se a avaliação não estiver pendente ou se o imóvel estiver em reforma.
     */
    public void aprovarAvaliacao(Usuario avaliador) {
        validarAvaliador(avaliador);

        if (statusAvaliacao != StatusAvaliacao.PENDENTE) {
            throw new IllegalStateException(
                    "Somente imóveis com avaliação pendente podem ser aprovados."
            );
        }

        if (statusConservacao == StatusImovel.EM_REFORMA) {
            throw new IllegalStateException(
                    "Um imóvel em reforma não pode ser avaliado."
            );
        }

        statusAvaliacao = StatusAvaliacao.APROVADA;
        atualizarData();
    }

    /**
     * Reprova a avaliação técnica do imóvel por meio de um usuário credenciado.
     *
     * @param avaliador O usuário com perfil de avaliador responsável pela reprovação.
     * @throws IllegalArgumentException Se o avaliador for nulo ou inválido.
     * @throws SecurityException        Se o usuário não possuir perfil de avaliador ou for o próprio proprietário do imóvel.
     * @throws IllegalStateException     Se a avaliação não estiver pendente ou se o imóvel estiver em reforma.
     */
    public void reprovarAvaliacao(Usuario avaliador) {
        validarAvaliador(avaliador);

        if (statusAvaliacao != StatusAvaliacao.PENDENTE) {
            throw new IllegalStateException(
                    "Somente imóveis com avaliação pendente podem ser reprovados."
            );
        }

        if (statusConservacao == StatusImovel.EM_REFORMA) {
            throw new IllegalStateException(
                    "Um imóvel em reforma não pode ser avaliado."
            );
        }

        statusAvaliacao = StatusAvaliacao.REPROVADA;
        atualizarData();
    }

    /**
     * Inicia o processo de reforma do imóvel solicitada pelo proprietário.
     * <p>
     * Se o anúncio estiver ativo, ele será automaticamente pausado. O estado do imóvel é
     * alterado para {@link StatusImovel#EM_REFORMA} e o status da avaliação retorna para {@link StatusAvaliacao#PENDENTE}.
     * </p>
     *
     * @param solicitante O usuário solicitante da reforma (deve ser o proprietário do imóvel).
     * @throws SecurityException    Se o solicitante não for o proprietário do imóvel.
     * @throws IllegalStateException Se o imóvel estiver em estados incompatíveis (Reservado, Vendido, Alugado ou Encerrado) ou já em reforma.
     */
    public void iniciarReforma(Usuario solicitante) {
        validarProprietarioDoImovel(solicitante);

        if (getStatus() == StatusAnuncio.RESERVADO
                || getStatus() == StatusAnuncio.VENDIDO
                || getStatus() == StatusAnuncio.ALUGADO
                || getStatus() == StatusAnuncio.ENCERRADO) {
            throw new IllegalStateException(
                    "O imóvel não pode entrar em reforma no estado atual."
            );
        }

        if (statusConservacao == StatusImovel.EM_REFORMA) {
            throw new IllegalStateException(
                    "O imóvel já está em reforma."
            );
        }

        pausarAnuncioSeAtivo(solicitante);
        statusConservacao = StatusImovel.EM_REFORMA;
        statusAvaliacao = StatusAvaliacao.PENDENTE;
        atualizarData();
    }

    /**
     * Conclui as reformas do imóvel, marcando-o novamente como disponível para novas avaliações.
     *
     * @param solicitante O usuário solicitante (deve ser o proprietário do imóvel).
     * @throws SecurityException    Se o solicitante não for o proprietário do imóvel.
     * @throws IllegalStateException Se o imóvel não estiver marcadamente no estado de reforma.
     */
    public void concluirReforma(Usuario solicitante) {
        validarProprietarioDoImovel(solicitante);

        if (statusConservacao != StatusImovel.EM_REFORMA) {
            throw new IllegalStateException(
                    "Apenas imóveis em reforma podem ter a reforma concluída."
            );
        }

        statusConservacao = StatusImovel.DISPONIVEL;
        statusAvaliacao = StatusAvaliacao.PENDENTE;
        atualizarData();
    }

    /**
     * Altera dados cadastrais e características do imóvel, reiniciando o ciclo de avaliação técnica.
     *
     * @param solicitante       O usuário responsável pela solicitação de alteração (deve ser o proprietário).
     * @param titulo            Novo título do anúncio.
     * @param descricao         Nova descrição detalhada.
     * @param preco             Novo valor do imóvel.
     * @param areaTotal         Nova medição de área em m².
     * @param quantidadeQuartos Nova quantidade de quartos.
     * @param endereco          Novo endereço do imóvel.
     * @throws SecurityException        Se o solicitante não for o proprietário do imóvel.
     * @throws IllegalArgumentException Se as novas informações não atenderem aos critérios de validação.
     */
    public void alterarInformacoes(
            Usuario solicitante,
            String titulo,
            String descricao,
            BigDecimal preco,
            double areaTotal,
            int quantidadeQuartos,
            String endereco
    ) {
        validarProprietarioDoImovel(solicitante);

        validarArea(areaTotal);
        validarQuartos(quantidadeQuartos);
        validarEndereco(endereco);

        alterarDadosBasicos(solicitante, titulo, descricao, preco);

        this.areaTotal = areaTotal;
        this.quantidadeQuartos = quantidadeQuartos;
        this.endereco = endereco.trim();

        statusAvaliacao = StatusAvaliacao.PENDENTE;
        atualizarData();
    }

    @Override
    public void reservar(Usuario interessado) {
        super.reservar(interessado);
    }

    @Override
    public void cancelarReserva(Usuario solicitante) {
        super.cancelarReserva(solicitante);
    }

    /**
     * Confirma a venda definitiva do imóvel.
     *
     * @param solicitante O usuário solicitante da transação (vendedor/proprietário).
     * @param comprador   O usuário adquirente do imóvel.
     * @throws IllegalStateException Se o imóvel não se encontrar previamente no estado {@link StatusAnuncio#RESERVADO}.
     */
    @Override
    public void confirmarVenda(Usuario solicitante, Usuario comprador) {
        if (getStatus() != StatusAnuncio.RESERVADO) {
            throw new IllegalStateException(
                    "O imóvel precisa estar reservado antes de ser vendido."
            );
        }

        super.confirmarVenda(solicitante, comprador);
    }

    /**
     * Registra o imóvel como alugado no sistema, encerrando seu ciclo de divulgação.
     *
     * @param solicitante O usuário solicitante da operação (deve ser o proprietário do imóvel).
     * @throws SecurityException    Se o solicitante não for o proprietário.
     * @throws IllegalStateException Se o anúncio não estiver Ativo ou Reservado.
     */
    public void marcarComoAlugado(Usuario solicitante) {
        validarProprietarioDoImovel(solicitante);

        if (getStatus() != StatusAnuncio.ATIVO
                && getStatus() != StatusAnuncio.RESERVADO) {
            throw new IllegalStateException(
                    "Somente anúncios ativos ou reservados podem ser alugados."
            );
        }

        if (getStatus() == StatusAnuncio.RESERVADO) {
            super.cancelarReserva(solicitante);
        }

        super.encerrarComoAlugado(solicitante, "Imóvel alugado.");
    }

    /**
     * Valida os pré-requisitos necessários para publicação do anúncio do imóvel.
     * <p>
     * Garante que a avaliação técnica esteja aprovada e o imóvel não esteja em reforma.
     * </p>
     *
     * @throws IllegalStateException Se a avaliação técnica estiver pendente/reprovada ou se o imóvel estiver em reforma.
     */
    @Override
    protected void validarParaPublicacao() {
        if (statusAvaliacao != StatusAvaliacao.APROVADA) {
            throw new IllegalStateException(
                    "O imóvel precisa ter avaliação aprovada para ser publicado."
            );
        }

        if (statusConservacao != StatusImovel.DISPONIVEL) {
            throw new IllegalStateException(
                    "Um imóvel em reforma não pode ser publicado."
            );
        }
    }

    private void pausarAnuncioSeAtivo(Usuario solicitante) {
        if (getStatus() == StatusAnuncio.ATIVO) {
            super.pausar(solicitante);
        }
    }

    /**
     * Garante que o usuário solicitante da operação seja o mesmo cadastrado como vendedor/proprietário do imóvel.
     *
     * @param solicitante O usuário a ser validado.
     * @throws SecurityException Se o solicitante for nulo ou possuir ID divergente do vendedor.
     */
    private void validarProprietarioDoImovel(Usuario solicitante) {
        if (solicitante == null
                || !getVendedor().getId().equals(solicitante.getId())) {
            throw new SecurityException(
                    "Apenas o vendedor do imóvel pode executar esta ação."
            );
        }
    }

    /**
     * Valida se o usuário fornecido possui perfil e permissão legal para atuar como avaliador deste imóvel.
     *
     * @param avaliador O usuário designado para a avaliação técnica.
     * @throws IllegalArgumentException Se o avaliador for nulo ou não possuir identificador.
     * @throws SecurityException        Se o usuário não tiver o papel de {@link TipoUsuario#AVALIADOR} ou for o dono do imóvel.
     */
    private void validarAvaliador(Usuario avaliador) {
        if (avaliador == null || avaliador.getId() == null) {
            throw new IllegalArgumentException(
                    "O avaliador é obrigatório."
            );
        }

        if (!avaliador.possuiPerfil(TipoUsuario.AVALIADOR)) {
            throw new SecurityException(
                    "A avaliação deve ser realizada por um usuário avaliador."
            );
        }

        if (getVendedor().getId().equals(avaliador.getId())) {
            throw new SecurityException(
                    "O proprietário não pode avaliar o próprio imóvel."
            );
        }
    }

    private static void validarArea(double area) {
        if (!Double.isFinite(area) || area <= 0) {
            throw new IllegalArgumentException(
                    "A área total deve ser maior que zero."
            );
        }
    }

    private static void validarQuartos(int quartos) {
        if (quartos < 0) {
            throw new IllegalArgumentException(
                    "A quantidade de quartos não pode ser negativa."
            );
        }
    }

    /**
     * Valida o formato textual e existência do endereço do imóvel.
     *
     * @param endereco Texto descritivo do endereço.
     * @throws IllegalArgumentException Se o texto do endereço for nulo, em branco ou composto unicamente de espaços.
     */
    private static void validarEndereco(String endereco) {
        if (endereco == null || endereco.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O endereço não pode estar vazio."
            );
        }
    }
}