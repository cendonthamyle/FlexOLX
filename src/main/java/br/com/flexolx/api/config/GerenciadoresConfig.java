package br.com.flexolx.api.config;

import br.com.flexolx.controller.GerenciadorCatalogo;
import br.com.flexolx.controller.GerenciadorPropostas;
import br.com.flexolx.controller.GerenciadorUsuarios;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra os {@code Gerenciador*} originais do projeto como beans singleton
 * do Spring, para serem injetados nos controllers via construtor.
 * <p>
 * Nenhuma classe {@code Gerenciador*} foi modificada — esta classe apenas as
 * instancia, do mesmo jeito que {@code ApiServer} fazia com {@code new}.
 * </p>
 */
@Configuration
public class GerenciadoresConfig {

    @Bean
    public GerenciadorCatalogo gerenciadorCatalogo() {
        return new GerenciadorCatalogo();
    }

    @Bean
    public GerenciadorUsuarios gerenciadorUsuarios() {
        return new GerenciadorUsuarios();
    }

    @Bean
    public GerenciadorPropostas gerenciadorPropostas() {
        return new GerenciadorPropostas();
    }
}
