package br.com.flexolx.api.dto;

import br.com.flexolx.model.imovel.Apartamento;
import br.com.flexolx.model.imovel.Casa;
import br.com.flexolx.model.imovel.Imovel;
import br.com.flexolx.model.imovel.Kitnet;

import java.math.BigDecimal;

/**
 * Representação JSON de um {@link Imovel} exposta pela API.
 * Espelha os mesmos campos que {@code ApiServer.imovelJson(...)} produzia na
 * versão com {@code com.sun.net.httpserver}.
 */
public record ImovelResponse(
        String id,
        String titulo,
        String descricao,
        BigDecimal preco,
        double areaTotal,
        int quartos,
        int banheiros,
        String endereco,
        String tipo,
        String status,
        String vendedor
) {

    public static ImovelResponse de(Imovel i) {
        return new ImovelResponse(
                i.getId().toString(),
                i.getTitulo(),
                i.getDescricao(),
                i.getPreco(),
                i.getAreaTotal(),
                i.getQuantidadeQuartos(),
                banheiros(i),
                i.getEndereco(),
                i.getTipoImovel().name(),
                i.getStatus().name(),
                i.getVendedor().getNome()
        );
    }

    private static int banheiros(Imovel i) {
        if (i instanceof Casa c) {
            return c.getQuantidadeBanheiros();
        }
        if (i instanceof Apartamento a) {
            return a.getQuantidadeBanheiros();
        }
        if (i instanceof Kitnet k) {
            return k.getQuantidadeBanheiros();
        }
        return 0;
    }
}
