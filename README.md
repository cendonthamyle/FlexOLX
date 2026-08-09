# FlexOLX API (Spring Boot)

Versão Spring Boot da API REST do FlexOLX, equivalente em endpoints e
comportamento à versão anterior baseada em `com.sun.net.httpserver`
(`ApiServer.java` do projeto original, que continua existindo ali como
referência histórica — não foi tocada).

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


