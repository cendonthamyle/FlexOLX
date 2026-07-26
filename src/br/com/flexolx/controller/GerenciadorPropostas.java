package br.com.flexolx.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import br.com.flexolx.model.proposta.Proposta;
import br.com.flexolx.model.usuario.Usuario;

public class GerenciadorPropostas {

    private final List<Proposta> listaPropostas;

    public GerenciadorPropostas() {
        this.listaPropostas = new ArrayList<>();
    }

    // 1. Enviar proposta
    public void enviarProposta(Proposta p) {
        if (p != null) {
            this.listaPropostas.add(p);
            System.out.println("Proposta enviada com sucesso!");
        }
    }

    // 2. Listar propostas recebidas por um anunciante/vendedor
    public List<Proposta> listarPropostasRecebidas(Usuario anunciante) {
        List<Proposta> recebidas = new ArrayList<>();
        if (anunciante == null || anunciante.getId() == null) return recebidas;

        for (Proposta p : listaPropostas) {
            // Verifica se o anunciante do imóvel é o usuário passado
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
            return true;
        }
        return false;
    }

    // 6. Recusar Proposta
    public boolean recusarProposta(UUID id, Usuario anunciante, String motivo) {
        Proposta p = buscarPorId(id);
        if (p != null) {
            p.recusar(anunciante, motivo);
            return true;
        }
        return false;
    }
}


