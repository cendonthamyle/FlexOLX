package br.com.flexolx.controller;
import java.util.*;

import br.com.flexolx.model.imovel.Imovel;
import br.com.flexolx.model.enums.TipoImovel;
import br.com.flexolx.model.enums.StatusAnuncio;

import java.math.BigDecimal;
/**
 * Responsável pelo gerenciamento do catálogo de imóveis disponíveis no sistema FlexOLX.
 * <p>
 * Centraliza as operações de cadastro, remoção, busca e filtragem dos imóveis
 * anunciados, mantendo a lista em memória e servindo como ponto de acesso
 * para as demais camadas do sistema (view e propostas).
 * </p>
 *
 * @author Luca Borges
 * @version 1.0
 */
public class GerenciadorCatalogo {
    private List<Imovel> imoveis;
    private final GerenciadorArquivos<Imovel> gerenciadorArquivos;

    /**
     * Constrói o gerenciador de catálogo, carregando os imóveis previamente
     * salvos no arquivo local (ou criando um arquivo vazio, caso seja a
     * primeira execução).
     */
    public GerenciadorCatalogo(){
        this.gerenciadorArquivos = new GerenciadorArquivos<>("imoveis.dat");
        this.imoveis = gerenciadorArquivos.carregar();
    }
    /**
     * Adiciona um novo imóvel ao catálogo e persiste a alteração em arquivo.
     *
     * @param imovel imóvel a ser adicionado; não pode ser {@code null}.
     * @throws IllegalArgumentException se {@code imovel} for {@code null}.
     * @throws PersistenciaException se ocorrer um erro ao salvar em arquivo.
     */
    public void adicionar(Imovel imovel){
        validarImovel(imovel);
        imoveis.add(imovel);
        gerenciadorArquivos.salvar(imoveis);
    }
    /**
     * Remove um imóvel do catálogo e persiste a alteração em arquivo.
     *
     * @param imovel imóvel a ser removido; não pode ser {@code null}.
     * @throws IllegalArgumentException se {@code imovel} for {@code null}.
     * @throws PersistenciaException se ocorrer um erro ao salvar em arquivo.
     */
    public void remover(Imovel imovel){
        validarImovel(imovel);
        imoveis.remove(imovel);
        gerenciadorArquivos.salvar(imoveis);
    }
    /**
     * Filtra a lista de imóveis cadastrados de acordo com os critérios informados.
     * <p>
     * Critérios não informados (nulos) são ignorados durante a filtragem.
     * </p>
     *
     * @param endereco  trecho do endereço a ser buscado (verifica se o endereço
     *                  do imóvel contém esse texto, não precisa ser igual); pode ser {@code null}.
     * @param tipo      tipo do imóvel desejado (ex: {@link TipoImovel#CASA}); pode ser {@code null}.
     * @param precoMax  valor máximo de preço aceito; pode ser {@code null}.
     * @param status    status do anúncio desejado (ex: {@link StatusAnuncio#ATIVO}); pode ser {@code null}.
     * @return lista de imóveis que atendem a todos os critérios informados.
     */
    public List<Imovel> filtrar(String endereco, TipoImovel tipo, BigDecimal precoMax, StatusAnuncio status){
        // caso o item não cumpra alguma das verificações necessarias, ele é pulado
        List<Imovel> l = new ArrayList<>();
        for(Imovel imovel : imoveis){
            if(precoMax != null && imovel.getPreco().compareTo(precoMax) > 0) continue;
            if(tipo != null && imovel.getTipoImovel() != tipo) continue;
            if(endereco != null && !imovel.getEndereco().toLowerCase().contains(endereco)){
                continue;
            }
            if(status != null && imovel.getStatus() != status) continue;
            // se der tudo certo, adiciona o item na lista
            l.add(imovel);
        }
        return l;
    }
    /**
     * Busca um imóvel pelo seu identificador único.
     *
     * @param id {@link UUID} do imóvel a ser buscado.
     * @return o imóvel correspondente, ou {@code null} caso nenhum seja encontrado.
     */
    public Imovel buscarPorId(UUID id){
        for(Imovel imovel : imoveis){
            if(imovel.getId().equals(id)){
                return imovel;
            }
        }
        return null;
    }
    /**
     * Retorna a lista completa de imóveis cadastrados no catálogo.
     * <p>
     * O retorno é uma cópia da lista interna, garantindo que alterações
     * feitas na lista devolvida não afetem o estado do catálogo.
     * </p>
     *
     * @return lista com todos os imóveis cadastrados.
     */
    public List<Imovel> listarTodos(){
        List<Imovel> l = new ArrayList<>();
        for(Imovel imovel : imoveis){
            l.add(imovel);
        }
        return l;
    }
    private void validarImovel(Imovel imovel){
        if(imovel == null){
            throw new IllegalArgumentException("É obrigatório colocar um imovel");
        }
    }
}
