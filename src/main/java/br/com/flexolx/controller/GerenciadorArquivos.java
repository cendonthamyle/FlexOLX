package br.com.flexolx.controller;

import java.io.Serializable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

/**
 * Responsável pela persistência de dados em arquivo binário local,
 * através da serialização de objetos Java.
 * <p>
 * Classe genérica que pode ser reutilizada para salvar e carregar
 * qualquer lista de objetos serializáveis, mantendo cada tipo de dado
 * (imóveis, usuários, propostas, etc) em seu próprio arquivo.
 * </p>
 *
 * @param <T> tipo dos objetos armazenados, deve implementar {@link Serializable}.
 * @author Luca Borges
 * @version 1.0
 */
public class GerenciadorArquivos<T extends Serializable> {
    private final String caminhoArquivo;

    /**
     * Constrói um gerenciador de arquivos vinculado a um caminho específico.
     *
     * @param caminhoArquivo caminho do arquivo {@code .dat} onde os dados
     *                       serão salvos e carregados.
     */
    public GerenciadorArquivos(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    /**
     * Salva a lista informada no arquivo, sobrescrevendo qualquer
     * conteúdo anterior.
     *
     * @param lista lista de objetos a serem persistidos.
     * @throws PersistenciaException se ocorrer um erro de escrita no arquivo.
     */
    public void salvar(List<T> lista){
        try (FileOutputStream fs = new FileOutputStream(caminhoArquivo);
            ObjectOutputStream os = new ObjectOutputStream(fs)) {
            os.writeObject(lista);
        } catch (IOException e) {
            throw new PersistenciaException("Não foi possivel salvar dados em "+caminhoArquivo, e);
        }
    }

    /**
     * Carrega a lista de objetos salva no arquivo.
     * <p>
     * Caso o arquivo ainda não exista (ex: primeira execução do sistema),
     * um arquivo vazio é criado automaticamente e uma lista vazia é retornada.
     * </p>
     *
     * @return lista de objetos carregada do arquivo.
     * @throws PersistenciaException se ocorrer um erro de leitura do arquivo.
     */
    @SuppressWarnings("unchecked")
    public List<T> carregar(){
        File arquivo = new File(caminhoArquivo);
        if(!arquivo.exists()){
            salvar(new ArrayList<>());
            return new ArrayList<>();
        }

        try (FileInputStream fs = new FileInputStream(caminhoArquivo); 
            ObjectInputStream is = new ObjectInputStream(fs)) {
                return (List<T>) is.readObject();
            
        } catch (IOException | ClassNotFoundException e) {
            throw new PersistenciaException("Não foi possivel carregar dados de "+caminhoArquivo, e);
        }
    }
}
