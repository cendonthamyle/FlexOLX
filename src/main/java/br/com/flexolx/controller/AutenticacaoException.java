package br.com.flexolx.controller;

/**
 * Exceção lançada quando uma tentativa de login falha por e-mail
 * ou senha incorretos.
 *
 * @author Luca Borges
 * @version 1.0
 */
public class AutenticacaoException extends RuntimeException{

    public AutenticacaoException(String mensagem){
        super(mensagem);
    }
}
