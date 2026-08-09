package br.com.flexolx.api.controller;

import br.com.flexolx.api.RecursoNaoEncontradoException;
import br.com.flexolx.api.dto.PropostaRequest;
import br.com.flexolx.api.dto.PropostaResponse;
import br.com.flexolx.api.dto.PropostasDoUsuarioResponse;
import br.com.flexolx.controller.GerenciadorCatalogo;
import br.com.flexolx.controller.GerenciadorPropostas;
import br.com.flexolx.controller.GerenciadorUsuarios;
import br.com.flexolx.model.enums.FormaPagamento;
import br.com.flexolx.model.imovel.Imovel;
import br.com.flexolx.model.proposta.Proposta;
import br.com.flexolx.model.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Endpoints de propostas — equivalentes a {@code GET /api/propostas?email=} e
 * {@code POST /api/propostas} da versão HttpServer.
 */
@RestController
@RequestMapping("/api/propostas")
public class PropostaController {

    private final GerenciadorCatalogo catalogo;
    private final GerenciadorUsuarios usuarios;
    private final GerenciadorPropostas propostas;

    public PropostaController(GerenciadorCatalogo catalogo, GerenciadorUsuarios usuarios,
                               GerenciadorPropostas propostas) {
        this.catalogo = catalogo;
        this.usuarios = usuarios;
        this.propostas = propostas;
    }

    @GetMapping
    public PropostasDoUsuarioResponse listarPorUsuario(@RequestParam String email) {
        Usuario u = usuarios.buscarPorEmail(email);
        if (u == null) {
            throw new RecursoNaoEncontradoException("Usuário não encontrado.");
        }

        var enviadas = propostas.listarPropostasEnviadas(u).stream().map(PropostaResponse::de).toList();
        var recebidas = propostas.listarPropostasRecebidas(u).stream().map(PropostaResponse::de).toList();
        return new PropostasDoUsuarioResponse(enviadas, recebidas);
    }

    @PostMapping
    public ResponseEntity<PropostaResponse> enviar(@Valid @RequestBody PropostaRequest body) {
        Imovel imovel = catalogo.buscarPorId(UUID.fromString(body.imovelId()));
        Usuario comprador = usuarios.buscarPorEmail(body.email());
        if (imovel == null || comprador == null) {
            throw new RecursoNaoEncontradoException("Imóvel ou usuário não encontrado.");
        }

        FormaPagamento forma = FormaPagamento.valueOf(
                (body.formaPagamento() == null ? "A_VISTA" : body.formaPagamento()).toUpperCase());
        LocalDateTime validade = LocalDateTime.now().plusDays(7);
        String mensagem = body.mensagem() == null ? "" : body.mensagem();

        // O construtor de Proposta valida o imóvel (avaliação aprovada, disponível
        // e anúncio ATIVO) e lança IllegalStateException/IllegalArgumentException
        // se algo não estiver certo — o GlobalExceptionHandler converte em 400.
        Proposta p = new Proposta(imovel, comprador, body.valor(), forma, mensagem, validade);
        propostas.enviarProposta(p);

        return ResponseEntity.status(HttpStatus.CREATED).body(PropostaResponse.de(p));
    }
}
