package br.com.flexolx.controller;

import java.util.*;

import br.com.flexolx.model.usuario.Usuario;
/**
 * Responsável pelo gerenciamento de usuarios cadastrados no sistema FlexOLX.
 * <p>
 * Centraliza as operações de cadastro, remoção e busca de usuários, mantendo a lista em memória e servindo como ponto de acesso
 * para as demais camadas do sistema (view e propostas).
 * </p>
 *
 * @author Luca Borges
 * @version 1.0
 */
public class GerenciadorUsuarios {
    private List<Usuario> usuarios = new ArrayList<>();
    private final GerenciadorArquivos<Usuario> gerenciadorArquivos;

    /**
     * Constrói o gerenciador de Usuários, carregando os usuários previamente
     * salvos no arquivo local (ou criando um arquivo vazio, caso seja a
     * primeira execução).
     */
    public GerenciadorUsuarios(){
        this.gerenciadorArquivos = new GerenciadorArquivos<>("usuario.dat");
        this.usuarios = gerenciadorArquivos.carregar();
    }

    /**
     * Adiciona um novo Usuario ao catálogo e persiste a alteração em arquivo.
     *
     * @param usuario Usuário a ser adicionado; não pode ser {@code null}.
     * @throws IllegalArgumentException se {@code usuario} for {@code null}.
     * @throws PersistenciaException se ocorrer um erro ao salvar em arquivo.
     */
    public void adicionar(Usuario usuario){
        validarUsuario(usuario);
        usuarios.add(usuario);
        gerenciadorArquivos.salvar(usuarios);

    }

    /**
     * Remove um Usuário do catálogo e persiste a alteração em arquivo.
     *
     * @param usuario Usuário a ser removido; não pode ser {@code null}.
     * @throws IllegalArgumentException se {@code usuario} for {@code null}.
     * @throws PersistenciaException se ocorrer um erro ao salvar em arquivo.
     */
    public void remover(Usuario usuario){
        validarUsuario(usuario);
        usuarios.remove(usuario);
        gerenciadorArquivos.salvar(usuarios);

    }

    /**
     * Busca um Usuário pelo seu emal, mais útil para login.
     *
     * @param email String do emal do Usuário a ser buscado.
     * @return o usuário correspondente, ou {@code null} caso nenhum seja encontrado.
     */
    public Usuario buscarPorEmail(String email){
        for(Usuario user : usuarios){
            if(user.getEmail().equalsIgnoreCase(email)){
                return user;
            }
        }
        return null;
    }

    /**
     * Busca um Usuário pelo seu identificador único.
     *
     * @param id Identificador do Usuário a ser buscado.
     * @return o usuário correspondente, ou {@code null} caso nenhum seja encontrado.
     */
    public Usuario buscarPorId(String id){
        for(Usuario user : usuarios){
            if(user.getId().equals(id)){
                return user;
            }
        }
        return null;
    }

    /**
     * Retorna a lista completa de Usuários cadastrados no catálogo.
     * <p>
     * O retorno é uma cópia da lista interna, garantindo que alterações
     * feitas na lista devolvida não afetem o estado do catálogo.
     * </p>
     *
     * @return lista com todos os usuários cadastrados.
     */
    public List<Usuario> listarTodos(){
        List<Usuario> users = new ArrayList<>();
        for(Usuario user : usuarios){
            users.add(user);
        }
        return users;
    }
    private void validarUsuario(Usuario usuario){
        if(usuario == null){
            throw new IllegalArgumentException("É obrigatório colocar um Usuário");
        }
    }
}
