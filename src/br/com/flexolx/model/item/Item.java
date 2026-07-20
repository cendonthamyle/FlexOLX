package br.com.flexolx.model.item;

import br.com.flexolx.model.enums.StatusAnuncio;
import br.com.flexolx.model.enums.TipoUsuario;
import br.com.flexolx.model.usuario.Usuario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

/**
 * Representa a estrutura base abstrata de um anúncio no sistema FlexOLX.
 *
 * <p>Esta classe concentra os dados e comportamentos comuns aos diferentes
 * tipos de itens anunciados no catálogo, incluindo gerenciamento de estados
 * (publicação, reserva, venda, pausa e encerramento) e validações de segurança
 * e de regra de negócio.</p>
 *
 * <p>Informações específicas (como endereço, quantidade de quartos e área total)
 * devem ser implementadas pelas subclasses concretas (ex: {@code Imovel}).</p>
 *
 * <p>A identidade do anúncio é definida por um {@link UUID} gerado na instanciação,
 * garantindo sua imutabilidade ao longo de todo o ciclo de vida.</p>
 *
 * @author Thamyle Cendon
 * @version 1.0
 */
public abstract class Item implements Serializable {
    private static final long serialVersionUID = 3L;
    private static final int TAMANHO_MAXIMO_TITULO = 150;
    private static final int TAMANHO_MAXIMO_DESCRICAO = 5_000;

    private final UUID id;
    private String titulo;
    private String descricao;
    private BigDecimal preco;
    private final Usuario vendedor;
    private StatusAnuncio status;
    private String interessadoId;
    private String compradorId;
    private String motivoEncerramento;
    private final Instant criadoEm;
    private Instant publicadoEm;
    private Instant reservadoEm;
    private Instant encerradoEm;
    private Instant atualizadoEm;

    /**
     * Construtor protegido para ser invocado pelas subclasses concretas.
     *
     * @param titulo    Título descritivo do anúncio (máximo 150 caracteres).
     * @param descricao Descrição detalhada do item (máximo 5000 caracteres).
     * @param preco     Valor monetário do item (deve ser maior que zero).
     * @param vendedor  Usuário que está anunciando (deve possuir perfil autorizado).
     * @throws IllegalArgumentException Se algum dos parâmetros for inválido ou se o vendedor não tiver permissão.
     */
    protected Item(String titulo, String descricao, BigDecimal preco,
                   Usuario vendedor) {
        this.id = UUID.randomUUID();
        this.titulo = normalizarTexto(titulo, "O título", TAMANHO_MAXIMO_TITULO);
        this.descricao = normalizarTexto(
                descricao, "A descrição", TAMANHO_MAXIMO_DESCRICAO);
        this.preco = normalizarPreco(preco);
        validarVendedor(vendedor);
        this.vendedor = vendedor;
        this.status = StatusAnuncio.RASCUNHO;
        this.criadoEm = Instant.now();
        this.atualizadoEm = criadoEm;
    }

    public UUID getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public BigDecimal getPreco() { return preco; }
    public Usuario getVendedor() { return vendedor; }
    public StatusAnuncio getStatus() { return status; }
    public String getInteressadoId() { return interessadoId; }
    public String getCompradorId() { return compradorId; }
    public String getMotivoEncerramento() { return motivoEncerramento; }
    public Instant getCriadoEm() { return criadoEm; }
    public Instant getPublicadoEm() { return publicadoEm; }
    public Instant getReservadoEm() { return reservadoEm; }
    public Instant getEncerradoEm() { return encerradoEm; }
    public Instant getAtualizadoEm() { return atualizadoEm; }

    /**
     * Publica um anúncio que se encontra no estado de rascunho.
     *
     * @param solicitante O usuário que está tentando publicar o anúncio (deve ser o proprietário).
     * @throws SecurityException Se o solicitante não for o vendedor.
     * @throws IllegalStateException Se o status do anúncio não for {@link StatusAnuncio#RASCUNHO}.
     */
    public void publicar(Usuario solicitante) {
        validarVendedorAtual();
        validarProprietario(solicitante);
        exigirStatus(StatusAnuncio.RASCUNHO);
        validarParaPublicacao();
        status = StatusAnuncio.ATIVO;
        publicadoEm = Instant.now();
        atualizarData();
    }

    /**
     * Pausa temporariamente a exibição de um anúncio ativo.
     *
     * @param solicitante O usuário que está solicitando a pausa (deve ser o proprietário).
     * @throws SecurityException Se o solicitante não for o vendedor.
     * @throws IllegalStateException Se o anúncio não estiver ativo.
     */
    public void pausar(Usuario solicitante) {
        validarProprietario(solicitante);
        exigirStatus(StatusAnuncio.ATIVO);
        status = StatusAnuncio.PAUSADO;
        atualizarData();
    }

    /**
     * Reativa um anúncio previamente pausado.
     *
     * @param solicitante O usuário que está solicitando a reativação (deve ser o proprietário).
     * @throws SecurityException Se o solicitante não for o vendedor.
     * @throws IllegalStateException Se o anúncio não estiver pausado.
     */
    public void reativar(Usuario solicitante) {
        validarVendedorAtual();
        validarProprietario(solicitante);
        exigirStatus(StatusAnuncio.PAUSADO);
        validarParaPublicacao();
        status = StatusAnuncio.ATIVO;
        atualizarData();
    }

    /**
     * Reserva o anúncio ativo para um determinado comprador interessado.
     *
     * @param interessado O usuário comprador que deseja reservar o item.
     * @throws IllegalStateException Se o anúncio não estiver no status {@link StatusAnuncio#ATIVO}.
     * @throws IllegalArgumentException Se o interessado for o próprio vendedor ou não possuir perfil de Cliente.
     */
    public void reservar(Usuario interessado) {
        exigirStatus(StatusAnuncio.ATIVO);
        validarInteressado(interessado);
        interessadoId = interessado.getId();
        reservadoEm = Instant.now();
        status = StatusAnuncio.RESERVADO;
        atualizarData();
    }

    /**
     * Cancela uma reserva existente e torna o anúncio ativo novamente.
     *
     * @param solicitante O usuário que está solicitando o cancelamento (deve ser o proprietário).
     * @throws SecurityException Se o solicitante não for o vendedor.
     * @throws IllegalStateException Se o anúncio não estiver reservado.
     */
    public void cancelarReserva(Usuario solicitante) {
        validarProprietario(solicitante);
        exigirStatus(StatusAnuncio.RESERVADO);
        interessadoId = null;
        reservadoEm = null;
        status = StatusAnuncio.ATIVO;
        atualizarData();
    }

    /**
     * Confirma a venda do anúncio para um comprador.
     *
     * @param solicitante O usuário responsável pelo anúncio (vendedor).
     * @param comprador   O usuário que está adquirindo o item.
     * @throws SecurityException Se o solicitante não for o vendedor do anúncio.
     * @throws IllegalStateException Se o anúncio não estiver ATIVO ou RESERVADO, ou se a venda for tentada para um comprador diferente do que reservou.
     */
    public void confirmarVenda(Usuario solicitante, Usuario comprador) {
        validarProprietario(solicitante);
        if (status != StatusAnuncio.ATIVO && status != StatusAnuncio.RESERVADO) {
            throw new IllegalStateException("Somente um anúncio ativo ou reservado pode ser vendido.");
        }
        validarInteressado(comprador);
        if (status == StatusAnuncio.RESERVADO
                && !interessadoId.equals(comprador.getId())) {
            throw new IllegalStateException("A venda deve ser confirmada para o interessado que reservou o anúncio.");
        }
        compradorId = comprador.getId();
        status = StatusAnuncio.VENDIDO;
        encerradoEm = Instant.now();
        atualizarData();
    }

    /**
     * Encerra definitivamente um anúncio que não foi vendido, registrando uma justificativa.
     *
     * @param solicitante O usuário que está encerrando o anúncio (vendedor).
     * @param motivo      O motivo pelo qual o anúncio está sendo encerrado (máximo 300 caracteres).
     */
    public void encerrar(Usuario solicitante, String motivo) {
        finalizarAnuncio(solicitante, motivo, StatusAnuncio.ENCERRADO);
    }

    /**
     * Encerra o anúncio registrando seu estado final como alugado. Método protegido para uso especializado em subclasses imobiliárias.
     *
     * @param solicitante O usuário solicitante (vendedor).
     * @param motivo      O motivo ou detalhes do aluguel.
     */
    protected final void encerrarComoAlugado(
            Usuario solicitante,
            String motivo
    ) {
        finalizarAnuncio(solicitante, motivo, StatusAnuncio.ALUGADO);
    }

    /**
     * Método interno auxiliar responsável por processar a transição para estados finais do anúncio.
     *
     * @param solicitante O usuário solicitante.
     * @param motivo      A justificativa de encerramento.
     * @param statusFinal O status final a ser atribuído.
     */
    private void finalizarAnuncio(
            Usuario solicitante,
            String motivo,
            StatusAnuncio statusFinal
    ) {
        validarProprietario(solicitante);

        if (status == StatusAnuncio.VENDIDO
                || status == StatusAnuncio.ALUGADO
                || status == StatusAnuncio.ENCERRADO) {
            throw new IllegalStateException(
                    "Um anúncio finalizado não pode ser encerrado novamente."
            );
        }

        motivoEncerramento = normalizarTexto(motivo, "O motivo", 300);
        interessadoId = null;
        reservadoEm = null;
        status = statusFinal;
        encerradoEm = Instant.now();
        atualizarData();
    }

    /**
     * Permite alterar os dados comerciais do anúncio apenas enquanto ele estiver em rascunho ou pausado.
     *
     * @param solicitante O usuário solicitante (vendedor).
     * @param titulo      O novo título para o anúncio.
     * @param descricao   A nova descrição do anúncio.
     * @param preco       O novo preço do item.
     * @throws SecurityException Se o solicitante não for o vendedor.
     * @throws IllegalStateException Se o anúncio estiver ATIVO, RESERVADO, VENDIDO, ALUGADO ou ENCERRADO.
     */
    public void alterarDadosBasicos(Usuario solicitante, String titulo,
                                    String descricao, BigDecimal preco) {
        validarProprietario(solicitante);
        if (status != StatusAnuncio.RASCUNHO && status != StatusAnuncio.PAUSADO) {
            throw new IllegalStateException("Pause o anúncio antes de alterar seus dados comerciais.");
        }
        this.titulo = normalizarTexto(titulo, "O título", TAMANHO_MAXIMO_TITULO);
        this.descricao = normalizarTexto(descricao, "A descrição", TAMANHO_MAXIMO_DESCRICAO);
        this.preco = normalizarPreco(preco);
        atualizarData();
    }

    /**
     * Hook/Gancho para ser sobrescrito por subclasses caso sejam necessárias validações
     * prévias adicionais (ex: endereço, fotos ou características específicas) antes da publicação.
     */
    protected void validarParaPublicacao() { }

    /** Valida se o usuário solicitante é o vendedor responsável do anúncio. */
    private void validarProprietario(Usuario solicitante) {
        if (solicitante == null || !vendedor.getId().equals(solicitante.getId())) {
            throw new SecurityException("Apenas o vendedor do anúncio pode executar esta ação.");
        }
    }

    /** Valida se o comprador/interessado cumpre os requisitos para reservar ou comprar o item. */
    private void validarInteressado(Usuario interessado) {
        if (interessado == null || interessado.getId() == null) {
            throw new IllegalArgumentException("O interessado é obrigatório.");
        }

        if (vendedor.getId().equals(interessado.getId())) {
            throw new IllegalArgumentException(
                    "O vendedor não pode reservar ou comprar o próprio anúncio."
            );
        }

        if (!interessado.possuiPerfil(TipoUsuario.CLIENTE)) {
            throw new IllegalArgumentException(
                    "O interessado deve possuir o perfil de cliente."
            );
        }
    }

    /** Remove espaços extras e verifica limites de tamanho do texto recebido. */
    private static String normalizarTexto(String valor, String campo, int maximo) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException(campo + " não pode estar vazio.");
        }
        String texto = valor.trim();
        if (texto.length() > maximo) {
            throw new IllegalArgumentException(campo + " deve ter no máximo " + maximo + " caracteres.");
        }
        return texto;
    }

    /** Arredonda o valor para duas casas decimais e garante que seja um valor positivo. */
    private static BigDecimal normalizarPreco(BigDecimal preco) {
        if (preco == null || preco.signum() <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        return preco.setScale(2, RoundingMode.HALF_UP);
    }

    /** Garante que o vendedor existe e possui perfil de anunciante (Imobiliária, Corretor ou Proprietário Direto). */
    private static void validarVendedor(Usuario vendedor) {
        if (vendedor == null || vendedor.getId() == null) {
            throw new IllegalArgumentException("O vendedor é obrigatório.");
        }
        boolean podeAnunciar = vendedor.possuiPerfil(TipoUsuario.IMOBILIARIA)
                || vendedor.possuiPerfil(TipoUsuario.CORRETOR)
                || vendedor.possuiPerfil(TipoUsuario.PROPRIETARIO_DIRETO);
        if (!podeAnunciar) {
            throw new IllegalArgumentException("O vendedor não possui perfil autorizado para anunciar.");
        }
    }

    /** Validação auxiliar do vendedor atual da instância. */
    private void validarVendedorAtual() { validarVendedor(vendedor); }

    /** Valida se o status do anúncio corresponde ao esperado para a execução de determinada ação. */
    private void exigirStatus(StatusAnuncio esperado) {
        if (status != esperado) {
            throw new IllegalStateException("Ação indisponível para anúncio com status " + status + ".");
        }
    }

    /** Atualiza o carimbo de data/hora {@code atualizadoEm} para o momento presente. */
    protected final void atualizarData() {
        atualizadoEm = Instant.now();
    }
    
    /**
     * Compara este anúncio com outro objeto para verificar sua igualdade.
     * A igualdade é definida estritamente com base no {@link UUID} e na classe do objeto.
     *
     * @param obj Objeto a ser comparado.
     * @return {@code true} se os objetos possuírem o mesmo UUID, {@code false} caso contrário.
     */
    @Override
    public final boolean equals(Object obj) {
        return this == obj || (obj != null && getClass() == obj.getClass()
                && id.equals(((Item) obj).id));
    }

    /**
     * Retorna o código Hash do anúncio.
     *
     * @return Hash code calculado a partir do {@link UUID} do anúncio.
     */
    @Override
    public final int hashCode() { return id.hashCode(); }

    /**
     * Retorna uma representação textual simplificada do anúncio para depuração.
     *
     * @return String contendo nome da subclasse, ID, título, preço e status.
     */
    @Override
    public String toString() {
        return getClass().getSimpleName() + "{id=" + id + ", titulo='" + titulo
                + "', preco=" + preco + ", status=" + status + "}";
    }
}
