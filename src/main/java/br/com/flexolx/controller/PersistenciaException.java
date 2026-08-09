package br.com.flexolx.controller;

/**
 * Exceção lançada quando ocorre um erro durante o salvamento ou
 * carregamento de dados persistidos em arquivo.
 *
 * @author Luca Borges
 * @version 1.0
 */
public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa){
        super(mensagem, causa);
    }
}
