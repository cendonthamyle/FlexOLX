package br.com.flexolx.controller;

public class PersistenciaException extends RuntimeException {

    public PersistenciaException(String mensagem, Throwable causa){
        super(mensagem, causa);
    }
}
