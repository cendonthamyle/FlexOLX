# Como rodar e testar o FlexOLX

Este guia cobre a stack completa: **backend** (API REST em Spring Boot) e
**frontend** (React + Vite). Todos os passos abaixo foram executados e
validados manualmente antes de escrever este documento.

## Pré-requisitos

| Ferramenta | Versão usada no teste | Observação |
|---|---|---|
| JDK      | 17+ (testado com Java 26) | `java -version` |
| Maven    | 3.9+ | `mvn -version` |
| Node.js  | 18+ (testado com Node 24) | `node --version` |
| npm      | testado com 11 | `npm --version` |

Não é necessário instalar nada além disso. O `docker-compose.yml` na raiz
sobe um MySQL, mas **não é usado por nenhuma parte do projeto** — a
persistência real é em arquivo (`imoveis.dat` / `usuarios.dat`), então pode
ser ignorado.

---

## 1. Rodando o backend

```bash
cd FlexOLX
mvn spring-boot:run
```

- Sobe em `http://localhost:8080`, todos os endpoints sob `/api`.
- Na primeira execução (sem `imoveis.dat`/`usuarios.dat` na pasta), um
  `DataSeeder` cria automaticamente 3 usuários e 3 imóveis de teste — veja
  a seção de credenciais abaixo. Se os arquivos `.dat` já existirem, o seed
  é pulado e os dados salvos são carregados.
- Para resetar o estado, pare o servidor e apague `imoveis.dat` e
  `usuarios.dat` antes de subir de novo.
- Para parar: `Ctrl+C` no terminal onde o Maven está rodando.

### Confirmação de que subiu certo

O log deve terminar com uma linha parecida com:

```
Started FlexolxApiApplication in X.XXX seconds
Seed concluído: 3 usuários, 3 imóveis (todos ATIVO).
```

Teste rápido pelo terminal:

```bash
curl http://localhost:8080/api/imoveis
```

Deve retornar um JSON com os 3 imóveis semeados.

---

## 2. Rodando o frontend

```bash
cd FlexOLX/frontend
npm install   # só necessário se node_modules não estiver presente
npm run dev
```

- Sobe em `http://localhost:8443` (porta definida em `vite.config.ts` —
  **não** é a porta padrão 5173 do Vite).
- Por padrão ele aponta para `http://localhost:8080/api` (o backend do
  passo 1). Para apontar pra outro backend, crie um `.env` em
  `frontend/` com `VITE_API_URL=http://outro-host:porta/api`.
- **O backend precisa estar rodando antes/junto**, senão a listagem de
  imóveis e o login ficam em branco/com erro de rede.

---

## 3. Credenciais de teste (criadas pelo seed)

| Papel | E-mail | Senha |
|---|---|---|
| Corretor (anunciante dos 3 imóveis) | `carlos@flexolx.com` | `123456` |
| Cliente | `ana@flexolx.com` | `123456` |
| Avaliador | `marina.avaliadora@flexolx.com` | `123456` |

---

## 4. Testando a API diretamente (curl)

Com o backend rodando em `localhost:8080`:

```bash
# Listar imóveis
curl http://localhost:8080/api/imoveis

# Buscar 1 imóvel por id (pegue um "id" da resposta acima)
curl http://localhost:8080/api/imoveis/<id>

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"ana@flexolx.com","senha":"123456"}'
# -> 200 com dados do usuário; senha errada -> 401 {"erro":"..."}

# Registrar novo usuário (tipo: CLIENTE default, ou CORRETOR/PROPRIETARIO_DIRETO)
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"nome":"Fulano","email":"fulano@teste.com","senha":"123456","telefone":"83999999999","tipo":"CLIENTE"}'

# Enviar proposta (troque <id> pelo id de um imóvel ATIVO)
curl -X POST http://localhost:8080/api/propostas \
  -H "Content-Type: application/json" \
  -d '{"imovelId":"<id>","email":"ana@flexolx.com","valor":450000,"formaPagamento":"A_VISTA","mensagem":"Tenho interesse"}'
# formaPagamento aceita: A_VISTA | FINANCIAMENTO | PARCELADO

# Listar propostas de um usuário (enviadas + recebidas)
curl "http://localhost:8080/api/propostas?email=ana@flexolx.com"
```

Todos os comandos acima foram rodados durante a validação deste documento
e retornaram os status esperados (200/201, e 401 para senha errada).

---

## 5. Testando pela interface (checklist manual)

Com backend (porta 8080) e frontend (porta 8443) rodando:

1. Abra `http://localhost:8443` — a home deve carregar e mostrar 3 imóveis
   em destaque (Apartamento Miramar, Casa Altiplano, Kitnet Centro) vindos
   de verdade da API (confirmado via DevTools/Network — não é mock).
2. Clique em **Entrar → "Sou usuário"** e logue com `ana@flexolx.com` /
   `123456`. A chamada `POST /api/auth/login` retorna 200.
3. Registro: em "Criar conta gratuita", crie um usuário novo — chama
   `POST /api/auth/register` e retorna 201.

### ⚠️ Limitação conhecida encontrada no teste

O botão **"Mandar proposta"** nos cards da home mostra "✓ Proposta
enviada!" mas **não faz nenhuma chamada de rede** — é um estado visual
local, não está de fato ligado a `api.enviarProposta()` (que existe em
`frontend/src/api.ts` mas não é chamada por esse botão). Ou seja, propostas
mandadas por esse botão específico **não chegam no backend** — confirmado
comparando `GET /api/propostas?email=...` antes e depois do clique (sem
diferença). Se for testar o fluxo de proposta de ponta a ponta, use o curl
da seção 4 por enquanto, ou trate isso como um bug a corrigir no
`App.tsx`/componente do card.

---

## 6. Rodando os dois juntos (resumo rápido)

Terminal 1:
```bash
cd FlexOLX && mvn spring-boot:run
```

Terminal 2:
```bash
cd FlexOLX/frontend && npm run dev
```

Depois acesse `http://localhost:8443`.

---

## 7. Testes automatizados

O projeto **não tem suíte de testes** (`src/test` não existe). Toda a
validação hoje é manual, pelos passos acima.
