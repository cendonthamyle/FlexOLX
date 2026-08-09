package br.com.flexolx.api.config;

import br.com.flexolx.controller.GerenciadorCatalogo;
import br.com.flexolx.controller.GerenciadorUsuarios;
import br.com.flexolx.model.imovel.Apartamento;
import br.com.flexolx.model.imovel.Casa;
import br.com.flexolx.model.imovel.Imovel;
import br.com.flexolx.model.imovel.Kitnet;
import br.com.flexolx.model.usuario.PerfilAvaliador;
import br.com.flexolx.model.usuario.PerfilCliente;
import br.com.flexolx.model.usuario.PerfilCorretor;
import br.com.flexolx.model.usuario.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Popula o catálogo e os usuários com dados de exemplo no boot, para o
 * front-end ter o que consumir. Equivalente ao método {@code semear()} que
 * existia em {@code ApiServer} (versão HttpServer nativo), já com as duas
 * correções aplicadas em 09/08/2026 (ver {@code ALTERACOES.md} do projeto
 * original):
 * <ol>
 *   <li>A Kitnet é anunciada pelo {@code corretor}, não pelo {@code cliente}
 *       — {@code PerfilCliente} não tem autorização para anunciar.</li>
 *   <li>Cada imóvel tem a avaliação técnica aprovada (por um usuário
 *       Avaliador) e é publicado antes do boot terminar, para já nascer com
 *       {@code StatusAnuncio.ATIVO}. Sem isso, nenhum imóvel aceita
 *       propostas (fica em {@code RASCUNHO}).</li>
 * </ol>
 */
@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final GerenciadorCatalogo catalogo;
    private final GerenciadorUsuarios usuarios;

    public DataSeeder(GerenciadorCatalogo catalogo, GerenciadorUsuarios usuarios) {
        this.catalogo = catalogo;
        this.usuarios = usuarios;
    }

    @Override
    public void run(String... args) {
        try {
            if (!usuarios.listarTodos().isEmpty()) {
                log.info("Dados já existentes (carregados de arquivo) — seed ignorado.");
                return;
            }

            // Vendedores precisam ter perfil autorizado a anunciar
            // (Imobiliária, Corretor ou Proprietário Direto) — PerfilCliente não anuncia.
            Usuario corretor = new Usuario("Carlos Mendes", "carlos@flexolx.com",
                    "123456", "83 99999-0001", new PerfilCorretor("012345-F"));
            Usuario cliente = new Usuario("Ana Beatriz Lima", "ana@flexolx.com",
                    "123456", "83 99999-0002", new PerfilCliente());
            // Avaliador usado apenas para aprovar a avaliação técnica no seed.
            Usuario avaliador = new Usuario("Marina Duarte", "marina.avaliadora@flexolx.com",
                    "123456", "83 99999-0003", new PerfilAvaliador("54321"));

            usuarios.adicionar(corretor);
            usuarios.adicionar(cliente);
            usuarios.adicionar(avaliador);

            Imovel apartamento = new Apartamento(
                    "Apartamento Moderno no Miramar",
                    "Apartamento arejado com vista para o mar, próximo a comércios e escolas.",
                    new BigDecimal("480000"), corretor, 92, 3,
                    "Miramar, João Pessoa — PB",
                    7, 2, 1, true, new BigDecimal("650"));

            Imovel casa = new Casa(
                    "Casa com Piscina no Altiplano",
                    "Casa espaçosa com piscina e quintal amplo em bairro nobre.",
                    new BigDecimal("1200000"), corretor, 210, 4,
                    "Altiplano, João Pessoa — PB",
                    3, 2, true);

            Imovel kitnet = new Kitnet(
                    "Kitnet Compacta no Centro",
                    "Kitnet mobiliada, ideal para estudantes, próxima à universidade.",
                    new BigDecimal("120000"), corretor, 32,
                    "Centro, João Pessoa — PB",
                    1, true);

            // Publicar exige: avaliação aprovada (por um Avaliador que não seja o
            // vendedor) + status ATIVO via publicar(vendedor). Sem isso, o imóvel
            // fica em RASCUNHO e nenhuma Proposta pode ser criada para ele.
            for (Imovel imovel : List.of(apartamento, casa, kitnet)) {
                imovel.aprovarAvaliacao(avaliador);
                catalogo.adicionar(imovel);
                imovel.publicar(corretor);
            }

            log.info("Seed concluído: {} usuários, {} imóveis (todos ATIVO).",
                    usuarios.listarTodos().size(), catalogo.listarTodos().size());
        } catch (Exception e) {
            log.error("Falha ao semear dados: {}", e.getMessage(), e);
        }
    }
}
