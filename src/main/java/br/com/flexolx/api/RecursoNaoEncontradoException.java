package br.com.flexolx.api;

/**
 * Lançada quando um recurso (imóvel, usuário, etc.) não é encontrado.
 * Convertida em {@code 404} com corpo {@code {"erro": "..."}} pelo
 * {@link br.com.flexolx.api.controller.GlobalExceptionHandler}.
 */
public class RecursoNaoEncontradoException extends RuntimeException {
    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }
}
