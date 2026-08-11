# FlexOLX API (Spring Boot)

Versão Spring Boot da API REST do FlexOLX, equivalente em endpoints e
comportamento à versão anterior baseada em `com.sun.net.httpserver`
(`ApiServer.java` do projeto original)

## Como rodar

Requer **JDK 17+** e **Maven** (ou o wrapper `./mvnw`, se você gerar um com
`mvn -N io.takari:maven:wrapper`). Este pacote não inclui o wrapper porque
o ambiente onde ele foi escrito não tem acesso ao Maven Central, mas ele
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

`GerenciadorCatalogo` e `GerenciadorUsuarios`
persistem em arquivos locais (`imoveis.dat`, `usuarios.dat`) via
`GerenciadorArquivos`. Isso significa que reiniciar a aplicação no mesmo
diretório carrega os dados salvos em vez de rodar o seed de novo. Para
resetar, apague os `.dat` antes de subir a aplicação.

## Diagrama de Classes
Considerando o grau de complexidade, escolhemos usar o plugin **UML Generator for Java** para fazer a base e editamos para colocar cada classe dentro do seu pacode adequado, para gerar o svg em si foi usado o plugin **PlantUML** para a conversão do arquivo .puml
<img width="6996" height="4745" alt="flexolx_diagrama" src="https://github.com/user-attachments/assets/83507a2e-3081-44c7-ab4e-f7b9961350bd" />
