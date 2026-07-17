package br.com.flexolx.model.usuario;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/*
 * A utilização desta classe evita que múltiplas exceções sejam
 * lançadas durante a validação de um objeto.
 *
 * Em vez disso, todos os erros encontrados são reunidos em uma
 * única estrutura, permitindo que o usuário corrija todas as
 * inconsistências de uma só vez.
 */
public class ResultadoValidacao implements Serializable {
    /**
     * Identificador utilizado pelo mecanismo de serialização do Java.
     */
    private static final long serialVersionUID = 1L;
    
    /**
     * Lista que armazena todas as mensagens de erro encontradas
     * durante o processo de validação.
     */
    private final List<String> erros = new ArrayList<>();

    /**
     * Adiciona uma nova mensagem de erro ao resultado da validação.
     *
     * @param erro descrição do erro encontrado.
     */
    public void adicionarErro(String erro) { this.erros.add(erro); }
    
    /**
     * Verifica se nenhuma inconsistência foi registrada.
     *
     * @return true caso não existam erros; false caso contrário.
     */
    public boolean ehValido() { return erros.isEmpty(); }

    /**
     * Retorna uma cópia da lista de erros.
     *
     * Uma cópia é retornada para preservar o encapsulamento,
     * impedindo que outras classes modifiquem diretamente
     * a lista interna da classe.
     *
     * @return lista contendo todas as mensagens de erro.
     */
    public List<String> getErros() { return new ArrayList<>(erros); }
}