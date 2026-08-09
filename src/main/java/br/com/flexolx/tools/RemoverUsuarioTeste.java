package br.com.flexolx.tools;

import br.com.flexolx.controller.GerenciadorArquivos;
import br.com.flexolx.model.usuario.Usuario;

import java.util.List;

/**
 * Script pontual de limpeza: remove um usuário de teste de usuarios.dat
 * pelo e-mail. Não faz parte da API, roda direto contra o arquivo.
 *
 * Usa GerenciadorArquivos diretamente (não GerenciadorUsuarios.remover()),
 * porque essa remover() reaproveita a validação de "e-mail duplicado" do
 * adicionar() e por isso rejeita qualquer usuário existente — bug do
 * próprio projeto, não alterado aqui de propósito.
 *
 * Uso: java -cp target/classes br.com.flexolx.tools.RemoverUsuarioTeste <email>
 */
public class RemoverUsuarioTeste {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Uso: RemoverUsuarioTeste <email>");
            return;
        }
        GerenciadorArquivos<Usuario> arquivos = new GerenciadorArquivos<>("usuarios.dat");
        List<Usuario> usuarios = arquivos.carregar();
        Usuario alvo = usuarios.stream()
            .filter(u -> u.getEmail().equalsIgnoreCase(args[0]))
            .findFirst()
            .orElse(null);

        if (alvo == null) {
            System.out.println("Nenhum usuário encontrado com o e-mail: " + args[0]);
            return;
        }
        usuarios.remove(alvo);
        arquivos.salvar(usuarios);
        System.out.println("Removido: " + alvo);
    }
}
