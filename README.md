# FlexOLX API (Spring Boot)

Versão Spring Boot da API REST do FlexOLX, equivalente em endpoints e
comportamento à versão anterior baseada em `com.sun.net.httpserver`
(`ApiServer.java` do projeto original, que continua existindo ali como
referência histórica — não foi tocada).

## O que mudou em relação à versão HttpServer

- **Nenhuma classe de `model` ou `controller.Gerenciador*` foi alterada** —
  foram copiadas exatamente como estavam.
- A camada de API foi reescrita usando Spring MVC (`@RestController`) em vez
  do `com.sun.net.httpserver` manual.
- Serialização JSON agora é automática via Jackson (a classe `Json.java`,
  escrita à mão, não existe mais nesta versão — o Spring faz isso sozinho a
  partir dos `record` em `api/dto/`).
- Erros de domínio (`AutenticacaoException`, `IllegalArgumentException`,
  `IllegalStateException`, `SecurityException`) são convertidos em respostas
  HTTP com corpo `{"erro": "..."}` por um `@RestControllerAdvice`
  (`GlobalExceptionHandler`), no mesmo formato que a versão antiga produzia.
- O seed de dados de exemplo virou um `CommandLineRunner`
  (`DataSeeder`), com as **duas correções já aplicadas**:
  1. A Kitnet agora é anunciada pelo corretor (não pelo cliente, que não tem
     perfil autorizado a anunciar).
  2. Os 3 imóveis semeados têm a avaliação técnica aprovada e são publicados
     no boot, ficando com status `ATIVO` — sem isso nenhuma proposta
     conseguia ser criada.

## Como rodar

Requer **JDK 17+** e **Maven** (ou o wrapper `./mvnw`, se você gerar um com
`mvn -N io.takari:maven:wrapper`). Este pacote não inclui o wrapper porque
o ambiente onde ele foi escrito não tem acesso ao Maven Central — mas ele
roda normalmente na sua máquina com o Maven instalado.

```bash
cd flexolx-api
mvn spring-boot:run
```

A API sobe em `http://localhost:8080/api`, com os endpoints:

| Método | Rota                     | Corpo / Query                                              |
|--------|--------------------------|-------------------------------------------------------------|
| GET    | `/api/imoveis`           | —                                                             |
| GET    | `/api/imoveis/{id}`      | —                                                             |
| POST   | `/api/auth/login`        | `{email, senha}`                                              |
| POST   | `/api/auth/register`     | `{nome, email, senha, telefone, tipo, creci?}`                |
| GET    | `/api/propostas?email=`  | —                                                             |
| POST   | `/api/propostas`         | `{imovelId, email, valor, formaPagamento, mensagem}`          |

## Persistência

`GerenciadorCatalogo` e `GerenciadorUsuarios` (não alterados) continuam
persistindo em arquivos locais (`imoveis.dat`, `usuarios.dat`) via
`GerenciadorArquivos`. Isso significa que reiniciar a aplicação no mesmo
diretório carrega os dados salvos em vez de rodar o seed de novo. Para
resetar, apague os `.dat` antes de subir a aplicação.

## Front-end

Nenhuma mudança é necessária no front-end (`frontend/`) — ele já aponta para
`http://localhost:8080/api` por padrão (`VITE_API_URL`).

## O que eu NÃO consegui testar aqui

Este pacote foi escrito num ambiente sem acesso ao Maven Central, então não
foi possível rodar `mvn compile`/`mvn spring-boot:run` para confirmar que
compila. As classes de `model`/`controller` (copiadas sem alteração) foram
recompiladas isoladamente com `javac` puro e continuam OK. Os arquivos novos
(`api/**`) foram revisados manualmente, mas peço que você rode
`mvn spring-boot:run` localmente e me avise se aparecer algum erro de
compilação — é rápido de corrigir a partir da mensagem do Maven.
