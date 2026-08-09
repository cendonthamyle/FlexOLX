package br.com.flexolx;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Ponto de entrada da API REST do FlexOLX.
 * <p>
 * Sobe um servidor Spring Boot (Tomcat embutido) na porta 8080, expondo os
 * mesmos endpoints que a versão anterior baseada em {@code com.sun.net.httpserver}
 * ({@code br.com.flexolx.api.ApiServer}, mantida no projeto original como
 * referência), mas agora usando controllers REST do Spring MVC + Jackson para
 * serialização JSON automática.
 * </p>
 * <p>
 * As classes de domínio ({@code model}) e os gerenciadores
 * ({@code controller.Gerenciador*}) foram copiadas <b>sem nenhuma alteração</b>
 * do projeto Java original — a camada Spring apenas as consome.
 * </p>
 */
@SpringBootApplication
public class FlexolxApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlexolxApiApplication.class, args);
    }
}
