package br.com.flexolx.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import br.com.flexolx.model.proposta.Proposta;
import br.com.flexolx.model.usuario.Usuario;

public class GerenciadorPropostas {

    private List<Proposta> listaPropostas;
    
    // Instância do gerenciador, tipado para <Proposta>
    private final GerenciadorArquivos<Proposta> gerenciadorArquivos;

    public GerenciadorPropostas() {
        // Inicializa passando o nome do arquivo que vai guardar as propostas
        this.gerenciadorArquivos = new GerenciadorArquivos<>("propostas.dat");
        
        // Ao iniciar, já puxa todos os dados salvos anteriormente no arquivo
        this.listaPropostas = gerenciadorArquivos.carregar();
    }

    // 1. Enviar proposta
    public void enviarProposta(Proposta p) {
        if (p != null) {
            this.listaPropostas.add(p);
            
            // SALVA as alterações no arquivo
            this.gerenciadorArquivos.salvar(this.listaPropostas);
            
            System.out.println("Proposta enviada e salva no arquivo com sucesso!");
        }
    }

    // 2. Listar propostas recebidas por um anunciante/vendedor
    public List<Proposta> listarPropostasRecebidas(Usuario anunciante) {
        List<Proposta> recebidas = new ArrayList<>();
        if (anunciante == null || anunciante.getId() == null) return recebidas;

        for (Proposta p : listaPropostas) {
            if (p.getImovel().getVendedor().getId().equals(anunciante.getId())) {
                recebidas.add(p);
            }
        }
        return recebidas;
    }

    // 3. Listar propostas enviadas por um comprador
    public List<Proposta> listarPropostasEnviadas(Usuario comprador) {
        List<Proposta> enviadas = new ArrayList<>();
        if (comprador == null || comprador.getId() == null) return enviadas;

        for (Proposta p : listaPropostas) {
            if (p.getProponente().getId().equals(comprador.getId())) {
                enviadas.add(p);
            }
        }
        return enviadas;
    }

    // 4. Buscar proposta por ID
    public Proposta buscarPorId(UUID id) {
        if (id == null) return null;
        for (Proposta p : listaPropostas) {
            if (p.getId().equals(id)) {
                return p;
            }
        }
        return null;
    }

    // 5. Aceitar Proposta
    public boolean aceitarProposta(UUID id, Usuario anunciante) {
        Proposta p = buscarPorId(id);
        if (p != null) {
            p.aceitar(anunciante);
            
            // SALVA as alterações no arquivo (pois o status da proposta mudou)
            this.gerenciadorArquivos.salvar(this.listaPropostas);
            
            return true;
        }
        return false;
    }

    // 6. Recusar Proposta
    public boolean recusarProposta(UUID id, Usuario anunciante, String motivo) {
        Proposta p = buscarPorId(id);
        if (p != null) {
            p.recusar(anunciante, motivo);
            
            // SALVA as alterações no arquivo (pois o status da proposta mudou)
            this.gerenciadorArquivos.salvar(this.listaPropostas);
            
            return true;
        }
        return false;
    }
}
