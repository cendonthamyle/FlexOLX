// Cliente da API REST do FlexOLX (back-end Java em src/br/com/flexolx/api/ApiServer.java).
// Base configurável por variável de ambiente Vite; cai para localhost:8080 no dev.
const BASE = (import.meta as { env?: Record<string, string> }).env?.VITE_API_URL ?? "http://localhost:8080/api"

export interface ApiImovel {
  id: string
  titulo: string
  descricao: string
  preco: number
  areaTotal: number
  quartos: number
  banheiros: number
  endereco: string
  tipo: "CASA" | "APARTAMENTO" | "KITNET" | "TERRENO"
  status: string
  vendedor: string
}

export interface ApiUsuario {
  id: string
  nome: string
  email: string
  telefone: string
  perfis: string[]
}

async function req<T>(path: string, init?: RequestInit): Promise<T> {
  const res = await fetch(BASE + path, {
    headers: { "Content-Type": "application/json" },
    ...init,
  })
  const data = await res.json().catch(() => ({}))
  if (!res.ok) {
    throw new Error((data as { erro?: string }).erro ?? "Erro na requisição")
  }
  return data as T
}

export const api = {
  listImoveis: () => req<ApiImovel[]>("/imoveis"),
  getImovel: (id: string) => req<ApiImovel>(`/imoveis/${id}`),
  login: (email: string, senha: string) =>
    req<ApiUsuario>("/auth/login", { method: "POST", body: JSON.stringify({ email, senha }) }),
  register: (payload: {
    nome: string
    email: string
    senha: string
    telefone: string
    tipo: "CLIENTE" | "CORRETOR" | "PROPRIETARIO_DIRETO"
    creci?: string
  }) => req<ApiUsuario>("/auth/register", { method: "POST", body: JSON.stringify(payload) }),
  enviarProposta: (payload: {
    imovelId: string
    email: string
    valor: number
    formaPagamento: "A_VISTA" | "FINANCIAMENTO" | "PARCELADO"
    mensagem: string
  }) => req("/propostas", { method: "POST", body: JSON.stringify(payload) }),
}
