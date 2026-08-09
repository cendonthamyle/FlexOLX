package br.com.flexolx.tools;

import br.com.flexolx.controller.GerenciadorCatalogo;
import br.com.flexolx.controller.GerenciadorUsuarios;
import br.com.flexolx.model.imovel.Imovel;
import br.com.flexolx.model.usuario.Usuario;

import java.util.List;

/**
 * Utilitário de linha de comando que lê usuarios.dat e imoveis.dat
 * (via os próprios Gerenciadores) e imprime o conteúdo em texto legível,
 * para inspecionar a persistência sem precisar subir a API.
 *
 * Uso: java -cp target/classes br.com.flexolx.tools.VisualizarDados
 * (executar a partir da raiz do projeto, onde ficam os arquivos .dat)
 */
public class VisualizarDados {

    public static void main(String[] args) {
        List<Usuario> usuarios = new GerenciadorUsuarios().listarTodos();
        System.out.println("=== usuarios.dat — " + usuarios.size() + " usuário(s) ===");
        for (Usuario u : usuarios) {
            System.out.println(u);
        }

        List<Imovel> imoveis = new GerenciadorCatalogo().listarTodos();
        System.out.println();
        System.out.println("=== imoveis.dat — " + imoveis.size() + " imóvel(is) ===");
        for (Imovel i : imoveis) {
            System.out.printf(
                "ID: %s | Titulo: %s | Preco: %s | Status: %s | Endereco: %s | Tipo: %s | Vendedor: %s%n",
                i.getId(), i.getTitulo(), i.getPreco(), i.getStatus(), i.getEndereco(),
                i.getTipoImovel(), i.getVendedor().getNome()
            );
        }
    }
}
