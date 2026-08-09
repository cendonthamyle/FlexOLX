package br.com.flexolx.model.proposta;

import br.com.flexolx.model.enums.FormaPagamento;
import br.com.flexolx.model.enums.StatusAnuncio;
import br.com.flexolx.model.enums.StatusAvaliacao;
import br.com.flexolx.model.enums.StatusImovel;
import br.com.flexolx.model.enums.StatusProposta;
import br.com.flexolx.model.imovel.Imovel;
import br.com.flexolx.model.usuario.Usuario;
import br.com.flexolx.model.enums.TipoUsuario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representa uma proposta de compra realizada para um imóvel no sistema FlexOLX.
 *
 * <p>A classe controla todo o ciclo de negociação, incluindo criação,
 * contraproposta, aceitação, recusa, cancelamento, expiração e conclusão
 * da venda.</p>
 *
 * <p>Cada proposta possui um identificador único e somente pode ser criada
 * para um imóvel avaliado, disponível e com anúncio ativo.</p>
 *
 * @author Thamyle Cendon
 * @version 1.0
 */
public class Proposta implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final int TAMANHO_MAXIMO_MENSAGEM = 1_000;
    private static final int TAMANHO_MAXIMO_MOTIVO_RECUSA = 300;

    private final UUID id;
    private final Imovel imovel;
    private final Usuario proponente;
    private final BigDecimal valorOfertado;
    private BigDecimal valorContraproposta;
    private BigDecimal valorAcordado;
    private final FormaPagamento formaPagamento;
    private final String mensagem;

    private StatusProposta status;
    private String motivoRecusa;

    private final LocalDateTime criadaEm;
    private final LocalDateTime validadeEm;
    private LocalDateTime respondidaEm;
    private LocalDateTime canceladaEm;
    private LocalDateTime concluidaEm;

    /**
     * Cria uma proposta de compra para um imóvel.
     *
     * @param imovel imóvel ao qual a proposta se destina.
     * @param proponente cliente responsável pela proposta.
     * @param valorOfertado valor oferecido pelo imóvel.
     * @param formaPagamento forma de pagamento escolhida.
     * @param mensagem mensagem opcional enviada ao vendedor.
     * @param validadeEm data e hora de validade da proposta.
     * @throws IllegalArgumentException se algum argumento for inválido.
     * @throws IllegalStateException se o imóvel não estiver disponível,
     *                               avaliado e com anúncio ativo.
     */
    public Proposta(
            Imovel imovel,
            Usuario proponente,
            BigDecimal valorOfertado,
            FormaPagamento formaPagamento,
            String mensagem,
            LocalDateTime validadeEm
    ) {
        validarImovel(imovel);
        validarProponente(imovel, proponente);
        validarValor(valorOfertado, "O valor ofertado");
        validarFormaPagamento(formaPagamento);
        validarValidade(validadeEm);

        this.id = UUID.randomUUID();
        this.imovel = imovel;
        this.proponente = proponente;
        this.valorOfertado = normalizarValor(valorOfertado);
        this.formaPagamento = formaPagamento;
        this.mensagem = normalizarMensagem(mensagem);
        this.status = StatusProposta.PENDENTE;
        this.criadaEm = LocalDateTime.now();
        this.validadeEm = validadeEm;
    }

    public UUID getId() {
        return id;
    }

    public Imovel getImovel() {
        return imovel;
    }

    public Usuario getProponente() {
        return proponente;
    }

    public BigDecimal getValorOfertado() {
        return valorOfertado;
    }

    public BigDecimal getValorContraproposta() {
        return valorContraproposta;
    }

    public BigDecimal getValorAcordado() {
        return valorAcordado;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public String getMensagem() {
        return mensagem;
    }

    public StatusProposta getStatus() {
        return status;
    }

    public String getMotivoRecusa() {
        return motivoRecusa;
    }

    public LocalDateTime getCriadaEm() {
        return criadaEm;
    }

    public LocalDateTime getValidadeEm() {
        return validadeEm;
    }

    public LocalDateTime getRespondidaEm() {
        return respondidaEm;
    }

    /**
     * Retorna a data e hora em que a proposta foi cancelada.
     *
     * @return data do cancelamento, ou {@code null} se não foi cancelada.
     */
    public LocalDateTime getCanceladaEm() {
        return canceladaEm;
    }

    public LocalDateTime getConcluidaEm() {
        return concluidaEm;
    }

    /**
     * Aceita o valor originalmente oferecido pelo proponente.
     *
     * @param vendedor vendedor responsável pelo imóvel.
     * @throws SecurityException se o usuário não for o vendedor.
     * @throws IllegalStateException se a proposta não estiver pendente
     *                               ou estiver expirada.
     */
    public void aceitar(Usuario vendedor) {
        validarVendedor(vendedor);
        exigirStatus(StatusProposta.PENDENTE);
        verificarValidade();

        imovel.reservar(proponente);

        valorAcordado = valorOfertado;
        status = StatusProposta.ACEITA;
        respondidaEm = LocalDateTime.now();
    }

    /**
     * Envia uma contraproposta ao interessado.
     *
     * @param vendedor vendedor responsável pelo imóvel.
     * @param novoValor novo valor proposto para a negociação.
     * @throws SecurityException se o usuário não for o vendedor.
     * @throws IllegalArgumentException se o valor for inválido.
     * @throws IllegalStateException se a proposta não estiver pendente
     *                               ou estiver expirada.
     */
    public void enviarContraproposta(
            Usuario vendedor,
            BigDecimal novoValor
    ) {
        validarVendedor(vendedor);
        exigirStatus(StatusProposta.PENDENTE);
        verificarValidade();
        validarValor(novoValor, "O valor da contraproposta");

        valorContraproposta = normalizarValor(novoValor);
        status = StatusProposta.CONTRAPROPOSTA_ENVIADA;
        respondidaEm = LocalDateTime.now();
    }

    public void aceitarContraproposta(Usuario solicitante) {
        validarProponente(solicitante);
        exigirStatus(StatusProposta.CONTRAPROPOSTA_ENVIADA);
        verificarValidade();

        imovel.reservar(proponente);

        valorAcordado = valorContraproposta;
        status = StatusProposta.ACEITA;
        respondidaEm = LocalDateTime.now();
    }

    public void recusar(Usuario vendedor, String motivo) {
        validarVendedor(vendedor);

        if (status != StatusProposta.PENDENTE
                && status != StatusProposta.CONTRAPROPOSTA_ENVIADA) {
            throw new IllegalStateException(
                    "Somente propostas pendentes podem ser recusadas."
            );
        }

        verificarValidade();

        motivoRecusa = normalizarMotivoRecusa(motivo);
        status = StatusProposta.RECUSADA;
        respondidaEm = LocalDateTime.now();
    }

    public void recusarContraproposta(Usuario solicitante) {
        validarProponente(solicitante);
        exigirStatus(StatusProposta.CONTRAPROPOSTA_ENVIADA);
        verificarValidade();

        status = StatusProposta.RECUSADA;
        motivoRecusa = "Contraproposta recusada pelo proponente.";
        respondidaEm = LocalDateTime.now();
    }

    /**
     * Cancela uma proposta que esteja em negociação ou já tenha sido aceita.
     *
     * <p>Quando uma proposta aceita é cancelada, a reserva do imóvel é
     * liberada, desde que pertença ao proponente desta proposta.</p>
     *
     * @param solicitante participante que solicitou o cancelamento.
     * @throws SecurityException se o usuário não possuir autorização.
     * @throws IllegalStateException se a proposta não puder ser cancelada.
     */
    public void cancelar(Usuario solicitante) {
        boolean propostaAceita = status == StatusProposta.ACEITA;

        if (propostaAceita) {
            validarParticipante(solicitante);
        } else {
            validarProponente(solicitante);
        }

        if (status != StatusProposta.PENDENTE
                && status != StatusProposta.CONTRAPROPOSTA_ENVIADA
                && status != StatusProposta.ACEITA) {
            throw new IllegalStateException(
                    "A proposta não pode ser cancelada no estado atual."
            );
        }

        if (propostaAceita) {
            liberarReservaDaProposta();
        } else {
            verificarValidade();
        }

        LocalDateTime agora = LocalDateTime.now();

        status = StatusProposta.CANCELADA;
        canceladaEm = agora;

        if (!propostaAceita) {
            respondidaEm = agora;
        }
    }

    /**
     * Encerra a proposta quando o imóvel deixa de estar disponível.
     *
     * <p>A proposta será marcada como expirada caso sua validade já tenha
     * terminado. Caso contrário, será cancelada.</p>
     */
    public void encerrarPorIndisponibilidadeDoImovel() {
        if (status != StatusProposta.PENDENTE
                && status != StatusProposta.CONTRAPROPOSTA_ENVIADA) {
            return;
        }

        respondidaEm = LocalDateTime.now();

        if (!respondidaEm.isBefore(validadeEm)) {
            status = StatusProposta.EXPIRADA;
            return;
        }

        status = StatusProposta.CANCELADA;
        motivoRecusa = "Imóvel reservado por outra proposta aceita.";
    }

    public void expirar() {
        if (status != StatusProposta.PENDENTE
                && status != StatusProposta.CONTRAPROPOSTA_ENVIADA) {
            throw new IllegalStateException(
                    "Somente propostas pendentes podem expirar."
            );
        }

        if (LocalDateTime.now().isBefore(validadeEm)) {
            throw new IllegalStateException(
                    "A proposta ainda não atingiu sua data de validade."
            );
        }

        status = StatusProposta.EXPIRADA;
        respondidaEm = LocalDateTime.now();
    }

    /**
     * Conclui a negociação e confirma a venda do imóvel.
     *
     * @param vendedor vendedor responsável pelo imóvel.
     * @throws SecurityException se o usuário não for o vendedor.
     * @throws IllegalStateException se a proposta não estiver aceita.
     */
    public void concluirVenda(Usuario vendedor) {
        validarVendedor(vendedor);
        exigirStatus(StatusProposta.ACEITA);

        imovel.confirmarVenda(vendedor, proponente);
        status = StatusProposta.CONCLUIDA;
        concluidaEm = LocalDateTime.now();
    }

    /**
     * Libera a reserva caso ela pertença ao proponente desta proposta.
     *
     * A verificação impede que o cancelamento de uma proposta antiga
     * libere uma reserva feita posteriormente por outro interessado.
     */
    private void liberarReservaDaProposta() {
        boolean anuncioReservado =
                imovel.getStatus() == StatusAnuncio.RESERVADO;

        boolean reservaPertenceAoProponente =
                proponente.getId().equals(imovel.getInteressadoId());

        if (anuncioReservado && reservaPertenceAoProponente) {
            imovel.cancelarReserva(imovel.getVendedor());
        }   
    }

    private void verificarValidade() {
        if (!LocalDateTime.now().isBefore(validadeEm)) {
            status = StatusProposta.EXPIRADA;
            respondidaEm = LocalDateTime.now();

            throw new IllegalStateException(
                    "A proposta expirou e não pode mais ser respondida."
            );
        }
    }

    private void validarVendedor(Usuario vendedor) {
        if (vendedor == null
                || !imovel.getVendedor().getId().equals(vendedor.getId())) {
            throw new SecurityException(
                    "Apenas o vendedor pode responder à proposta."
            );
        }
    }

    private void validarProponente(Usuario solicitante) {
        if (solicitante == null
                || !proponente.getId().equals(solicitante.getId())) {
            throw new SecurityException(
                    "Apenas o proponente pode executar esta ação."
            );
        }

        if (!proponente.possuiPerfil(TipoUsuario.CLIENTE)) {
            throw new IllegalArgumentException(
                "O proponente deve possuir o perfil de cliente."
            );
        }
    }

    /**
     * Verifica se o solicitante participa da proposta como proponente
     * ou vendedor do imóvel.
     *
     * @param solicitante usuário que solicitou a operação.
     * @throws SecurityException se o usuário não participar da proposta.
     */
    private void validarParticipante(Usuario solicitante) {
        if (solicitante == null || solicitante.getId() == null) {
            throw new SecurityException(
                    "Apenas os participantes da proposta podem executar esta ação."
            );
        }

        boolean ehProponente =
                proponente.getId().equals(solicitante.getId());

        boolean ehVendedor =
                imovel.getVendedor().getId().equals(solicitante.getId());

        if (!ehProponente && !ehVendedor) {
            throw new SecurityException(
                    "Apenas os participantes da proposta podem executar esta ação."
            );
        }
    }

    private void exigirStatus(StatusProposta statusEsperado) {
        if (status != statusEsperado) {
            throw new IllegalStateException(
                    "Ação indisponível para proposta com status " + status + "."
            );
        }
    }

    private static void validarImovel(Imovel imovel) {
        if (imovel == null) {
            throw new IllegalArgumentException(
                    "O imóvel da proposta é obrigatório."
            );
        }

        if (imovel.getStatusAvaliacao() != StatusAvaliacao.APROVADA
                || imovel.getStatusConservacao()
                        != StatusImovel.DISPONIVEL
                || imovel.getStatus() != StatusAnuncio.ATIVO) {
            throw new IllegalStateException(
                    "Só é possível criar propostas para imóveis avaliados, "
                            + "disponíveis e com anúncio ativo."
            );
        }
    }

    private static void validarProponente(
            Imovel imovel,
            Usuario proponente
    ) {
        if (proponente == null || proponente.getId() == null) {
            throw new IllegalArgumentException(
                    "O autor da proposta é obrigatório."
            );
        }

        if (!proponente.possuiPerfil(TipoUsuario.CLIENTE)) {
            throw new IllegalArgumentException(
                "O proponente deve possuir o perfil de cliente."
            );
        }

        if (imovel.getVendedor().getId().equals(proponente.getId())) {
            throw new IllegalArgumentException(
                    "O vendedor não pode propor a compra do próprio imóvel."
            );
        }
    }

    private static void validarValor(BigDecimal valor, String campo) {
        if (valor == null || valor.signum() <= 0) {
            throw new IllegalArgumentException(
                    campo + " deve ser maior que zero."
            );
        }
    }

    private static void validarFormaPagamento(
            FormaPagamento formaPagamento
    ) {
        if (formaPagamento == null) {
            throw new IllegalArgumentException(
                    "A forma de pagamento é obrigatória."
            );
        }
    }

    private static void validarValidade(LocalDateTime validadeEm) {
        if (validadeEm == null
                || !validadeEm.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "A validade da proposta deve estar no futuro."
            );
        }
    }

    private static BigDecimal normalizarValor(BigDecimal valor) {
        return valor.setScale(2, RoundingMode.HALF_UP);
    }

    private static String normalizarMensagem(String mensagem) {
        if (mensagem == null) {
            return "";
        }

        String texto = mensagem.trim();

        if (texto.length() > TAMANHO_MAXIMO_MENSAGEM) {
            throw new IllegalArgumentException(
                    "A mensagem pode ter no máximo "
                            + TAMANHO_MAXIMO_MENSAGEM + " caracteres."
            );
        }

        return texto;
    }

    private static String normalizarMotivoRecusa(String motivo) {
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "O motivo da recusa é obrigatório."
            );
        }

        String texto = motivo.trim();

        if (texto.length() > TAMANHO_MAXIMO_MOTIVO_RECUSA) {
            throw new IllegalArgumentException(
                    "O motivo da recusa pode ter no máximo "
                            + TAMANHO_MAXIMO_MOTIVO_RECUSA + " caracteres."
            );
        }

        return texto;
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || (
                obj instanceof Proposta
                        && id.equals(((Proposta) obj).id)
        );
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}