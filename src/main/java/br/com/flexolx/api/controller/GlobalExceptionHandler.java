package br.com.flexolx.api.controller;

import br.com.flexolx.api.RecursoNaoEncontradoException;
import br.com.flexolx.api.dto.ErroResponse;
import br.com.flexolx.controller.AutenticacaoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Converte as exceções lançadas pelo domínio (classes {@code model.*} e
 * {@code controller.Gerenciador*}, todas mantidas sem alteração) em respostas
 * HTTP com corpo {@code {"erro": "mensagem"}}, no mesmo formato que
 * {@code ApiServer.erro(...)} produzia na versão HttpServer.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /** Credenciais inválidas em {@code GerenciadorUsuarios.login(...)}. */
    @ExceptionHandler(AutenticacaoException.class)
    public ResponseEntity<ErroResponse> tratarAutenticacao(AutenticacaoException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErroResponse(e.getMessage()));
    }

    /** Imóvel/usuário/proposta não encontrado. */
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErroResponse(e.getMessage()));
    }

    /**
     * Erros de validação do domínio: argumento inválido (ex.: UUID malformado,
     * FormaPagamento desconhecida, campos obrigatórios das classes de modelo)
     * ou estado inválido (ex.: proposta para imóvel não ATIVO/avaliado).
     */
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ResponseEntity<ErroResponse> tratarRequisicaoInvalida(RuntimeException e) {
        return ResponseEntity.badRequest().body(new ErroResponse(e.getMessage()));
    }

    /** Regras de autorização do domínio (ex.: proprietário tentando avaliar o próprio imóvel). */
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErroResponse> tratarProibido(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErroResponse(e.getMessage()));
    }

    /** Falha de validação Bean Validation (@NotBlank, @Positive etc. nos DTOs). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException e) {
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(erro -> erro.getDefaultMessage())
                .orElse("Dados inválidos.");
        return ResponseEntity.badRequest().body(new ErroResponse(mensagem));
    }

    /** Corpo da requisição ausente ou com JSON malformado. */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarCorpoInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest().body(new ErroResponse("Corpo da requisição inválido."));
    }

    /** Rede de segurança — qualquer outra falha vira 500 sem vazar detalhes internos. */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarErroInesperado(Exception e) {
        log.error("Erro inesperado", e);
        return ResponseEntity.internalServerError().body(new ErroResponse("Erro interno no servidor."));
    }
}
