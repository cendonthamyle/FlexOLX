package br.com.flexolx.api.controller;

import br.com.flexolx.api.RecursoNaoEncontradoException;
import br.com.flexolx.api.dto.ImovelResponse;
import br.com.flexolx.controller.GerenciadorCatalogo;
import br.com.flexolx.model.imovel.Imovel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints de catálogo — equivalentes a {@code GET /api/imoveis} e
 * {@code GET /api/imoveis/{id}} da versão HttpServer.
 */
@RestController
@RequestMapping("/api/imoveis")
public class ImovelController {

    private final GerenciadorCatalogo catalogo;

    public ImovelController(GerenciadorCatalogo catalogo) {
        this.catalogo = catalogo;
    }

    @GetMapping
    public List<ImovelResponse> listar() {
        return catalogo.listarTodos().stream()
                .map(ImovelResponse::de)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ImovelResponse> buscarPorId(@PathVariable String id) {
        // UUID.fromString lança IllegalArgumentException para id malformado;
        // o GlobalExceptionHandler converte isso em 400 com corpo {"erro": ...}.
        UUID uuid = UUID.fromString(id);

        Imovel imovel = catalogo.buscarPorId(uuid);
        if (imovel == null) {
            throw new RecursoNaoEncontradoException("Imóvel não encontrado.");
        }
        return ResponseEntity.ok(ImovelResponse.de(imovel));
    }
}
