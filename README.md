# FlexOLX

Sistema de anúncios e negociação de imóveis, focado em ser uma alternativa mais barata pra corretores autônomos, imobiliárias pequenas e construtoras que não competem com o orçamento de marketing das plataformas grandes. O sistema tem uma versão de console pra testes e a versão principal, com API REST em Spring Boot e front-end em React.

## Backend (API REST)

Requer **JDK 17+** e **Maven**.

```bash
mvn spring-boot:run
```

A API sobe em `http://localhost:8080/api`.

| Método | Rota                     | Corpo / Query                                      |
|--------|--------------------------|-----------------------------------------------------|
| GET    | `/api/imoveis`           | —                                                     |
| GET    | `/api/imoveis/{id}`      | —                                                     |
| POST   | `/api/auth/login`        | `{email, senha}`                                      |
| POST   | `/api/auth/register`     | `{nome, email, senha, telefone, tipo, creci?}`        |
| GET    | `/api/propostas?email=`  | —                                                     |
| POST   | `/api/propostas`         | `{imovelId, email, valor, formaPagamento, mensagem}` |

### Persistência

`GerenciadorCatalogo` e `GerenciadorUsuarios` persistem em arquivos locais
(`imoveis.dat`, `usuarios.dat`) via `GerenciadorArquivos`. Reiniciar a
aplicação no mesmo diretório carrega os dados salvos em vez de rodar o seed
de novo. Para resetar, apague os `.dat` antes de subir a aplicação.

## Frontend

Requer **Node.js** e **npm**.

```bash
cd frontend
npm install
npm run dev
```

O front sobe em modo dev via Vite. Por padrão ele aponta para
`http://localhost:8080/api`; para apontar pra outro endereço, defina
`VITE_API_URL` antes de rodar.

## Diagrama de Classes

Gerado com o plugin **UML Generator for Java**, reorganizado manualmente
por pacote, e convertido pra SVG com o plugin **PlantUML**.
(É um arquivo vetorizado então pode dar zoon a vontado mesmo dentro do git-hub pra conseguir ver algo)
<img width="6996" height="4745" alt="flexolx_diagrama" src="https://github.com/user-attachments/assets/83507a2e-3081-44c7-ab4e-f7b9961350bd" />
