import { useEffect, useState } from "react"
import { api, type ApiImovel } from "./api"

// Imagens de exemplo reutilizadas quando o imóvel vem da API (que não armazena fotos).
const fallbackImages = [
  "https://images.unsplash.com/photo-1724582586529-62622e50c0b3?w=600&h=400&fit=crop&auto=format",
  "https://images.unsplash.com/photo-1649083048770-82e8ffd80431?w=600&h=400&fit=crop&auto=format",
  "https://images.unsplash.com/photo-1600210492493-0946911123ea?w=600&h=400&fit=crop&auto=format",
  "https://images.unsplash.com/photo-1724582586495-d050726cf354?w=600&h=400&fit=crop&auto=format",
]

function apiImovelToProperty(im: ApiImovel, index: number): Property {
  const tagByStatus: Record<string, string | null> = { ATIVO: "Destaque", RESERVADO: "Reservado" }
  return {
    id: index + 1,
    apiId: im.id,
    title: im.titulo,
    image: fallbackImages[index % fallbackImages.length],
    size: Math.round(im.areaTotal),
    rooms: im.quartos,
    bathrooms: im.banheiros,
    location: im.endereco,
    price: im.preco,
    seller: im.vendedor,
    tag: tagByStatus[im.status] ?? null,
  }
}

type Page = "home" | "login" | "signup" | "detail"

const mockProperties: Property[] = [
  {
    id: 1,
    title: "Apartamento Moderno no Miramar",
    image: "https://images.unsplash.com/photo-1724582586529-62622e50c0b3?w=600&h=400&fit=crop&auto=format",
    size: 92,
    rooms: 3,
    bathrooms: 2,
    location: "Miramar, João Pessoa — PB",
    price: 480000,
    seller: "Carlos Mendes",
    tag: "Destaque",
  },
  {
    id: 2,
    title: "Casa com Piscina no Altiplano",
    image: "https://images.unsplash.com/photo-1649083048770-82e8ffd80431?w=600&h=400&fit=crop&auto=format",
    size: 210,
    rooms: 4,
    bathrooms: 3,
    location: "Altiplano, João Pessoa — PB",
    price: 1200000,
    seller: "Ana Beatriz Lima",
    tag: "Novo",
  },
  {
    id: 3,
    title: "Apartamento no Cabo Branco",
    image: "https://images.unsplash.com/photo-1600210492493-0946911123ea?w=600&h=400&fit=crop&auto=format",
    size: 75,
    rooms: 2,
    bathrooms: 1,
    location: "Cabo Branco, João Pessoa — PB",
    price: 320000,
    seller: "Rodrigo Farias",
    tag: null,
  },
  {
    id: 4,
    title: "Cobertura Duplex no Bessa",
    image: "https://images.unsplash.com/photo-1724582586495-d050726cf354?w=600&h=400&fit=crop&auto=format",
    size: 180,
    rooms: 4,
    bathrooms: 4,
    location: "Bessa, João Pessoa — PB",
    price: 950000,
    seller: "Fernanda Costa",
    tag: "Exclusivo",
  },
  {
    id: 5,
    title: "Apartamento Compacto no Tambaú",
    image: "https://images.unsplash.com/photo-1564078516393-cf04bd966897?w=600&h=400&fit=crop&auto=format",
    size: 48,
    rooms: 1,
    bathrooms: 1,
    location: "Tambaú, João Pessoa — PB",
    price: 190000,
    seller: "Paulo Henrique",
    tag: null,
  },
  {
    id: 6,
    title: "Casa Térrea no Água Fria",
    image: "https://images.unsplash.com/photo-1724582586458-a51791349977?w=600&h=400&fit=crop&auto=format",
    size: 130,
    rooms: 3,
    bathrooms: 2,
    location: "Água Fria, João Pessoa — PB",
    price: 410000,
    seller: "Mariana Souza",
    tag: null,
  },
]

function formatPrice(n: number) {
  return "R$ " + n.toLocaleString("pt-BR")
}

export default function App() {
  const [page, setPage] = useState<Page>("home")
  const [filterType, setFilterType] = useState<"todos" | "apartamento" | "casa">("todos")
  const [menuOpen, setMenuOpen] = useState(false)
  const [selectedProperty, setSelectedProperty] = useState<Property | null>(null)
  const [properties, setProperties] = useState<Property[]>(mockProperties)

  // Carrega imóveis da API FlexOLX; mantém os dados de exemplo se ela estiver offline.
  useEffect(() => {
    let ativo = true
    api.listImoveis()
      .then(lista => {
        if (ativo && lista.length > 0) {
          setProperties(lista.map(apiImovelToProperty))
        }
      })
      .catch(() => {/* API offline — segue com mockProperties */})
    return () => { ativo = false }
  }, [])
  const [loginRole, setLoginRole] = useState<"corretor" | "usuario">("corretor")
  const [loginCreci, setLoginCreci] = useState("")
  const [loginCpf, setLoginCpf] = useState("")
  const [loginEmail, setLoginEmail] = useState("")
  const [loginPassword, setLoginPassword] = useState("")
  const [loginError, setLoginError] = useState(false)

  // Signup state
  const [signupRole, setSignupRole] = useState<"corretor" | "usuario">("usuario")
  const [signupName, setSignupName] = useState("")
  const [signupPhone, setSignupPhone] = useState("")
  const [signupCpf, setSignupCpf] = useState("")
  const [signupCreci, setSignupCreci] = useState("")
  const [signupPassword, setSignupPassword] = useState("")
  const [signupError, setSignupError] = useState(false)

  const filtered = filterType === "todos"
    ? properties
    : properties.filter(p =>
        filterType === "apartamento"
          ? p.title.toLowerCase().includes("apartamento") || p.title.toLowerCase().includes("cobertura")
          : p.title.toLowerCase().includes("casa")
      )

  function formatCpf(value: string) {
    const digits = value.replace(/\D/g, "").slice(0, 11)
    return digits
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d)/, "$1.$2")
      .replace(/(\d{3})(\d{1,2})$/, "$1-$2")
  }

  const emailValid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(loginEmail)

  function handleLogin(e: React.FormEvent) {
    e.preventDefault()
    const ok = loginRole === "corretor"
      ? loginCreci.trim() && loginCpf.replace(/\D/g, "").length === 11 && loginPassword.length >= 6
      : emailValid && loginPassword.length >= 6
    if (ok) {
      if (loginRole === "usuario") {
        // Autentica na API FlexOLX quando disponível (não bloqueia o preview offline).
        api.login(loginEmail, loginPassword).catch(() => {/* API offline */})
      }
      setPage("home")
      setLoginError(false)
    } else {
      setLoginError(true)
    }
  }

  function switchRole(role: "corretor" | "usuario") {
    setLoginRole(role)
    setLoginError(false)
  }

  function formatPhone(value: string) {
    const digits = value.replace(/\D/g, "").slice(0, 11)
    if (digits.length <= 10) {
      return digits
        .replace(/(\d{2})(\d)/, "($1) $2")
        .replace(/(\d{4})(\d{1,4})$/, "$1-$2")
    }
    return digits
      .replace(/(\d{2})(\d)/, "($1) $2")
      .replace(/(\d{5})(\d{1,4})$/, "$1-$2")
  }

  function handleSignup(e: React.FormEvent) {
    e.preventDefault()
    const baseOk =
      signupName.trim().length >= 3 &&
      signupPhone.replace(/\D/g, "").length >= 10 &&
      signupCpf.replace(/\D/g, "").length === 11 &&
      signupPassword.length >= 6
    const ok = signupRole === "corretor" ? baseOk && signupCreci.trim() : baseOk
    if (ok) {
      // Cria a conta na API FlexOLX quando disponível. A API usa e-mail como
      // credencial; derivamos um a partir do CPF apenas para o fluxo de demo.
      api.register({
        nome: signupName,
        email: `${signupCpf.replace(/\D/g, "")}@flexolx.com`,
        senha: signupPassword,
        telefone: signupPhone,
        tipo: signupRole === "corretor" ? "CORRETOR" : "CLIENTE",
        creci: signupRole === "corretor" ? signupCreci : undefined,
      }).catch(() => {/* API offline */})
      setPage("home")
      setSignupError(false)
    } else {
      setSignupError(true)
    }
  }

  if (page === "detail" && selectedProperty) {
    return <PropertyDetail property={selectedProperty} onBack={() => setPage("home")} />
  }

  if (page === "signup") {
    const inputBase = "w-full px-4 py-3 rounded-xl text-sm outline-none transition-all"
    const errBorder = (bad: boolean) =>
      signupError && bad ? "1.5px solid #e07070" : "1.5px solid #e8ddd5"
    return (
      <div className="min-h-screen flex" style={{ background: "#fff" }}>
        {/* Left panel */}
        <div
          className="hidden lg:flex flex-col justify-between w-[46%] p-12 relative overflow-hidden"
          style={{ background: "linear-gradient(145deg, #3E4E50 0%, #2a3638 100%)" }}
        >
          <div
            className="absolute inset-0 opacity-10"
            style={{
              backgroundImage: `url(https://images.unsplash.com/photo-1656038339605-f48e55b367d5?w=800&h=1200&fit=crop&auto=format)`,
              backgroundSize: "cover",
              backgroundPosition: "center",
            }}
          />
          <button onClick={() => setPage("home")} className="relative z-10 flex items-center gap-2">
            <span style={{ color: "#F5AC72", fontSize: 22 }}>⌂</span>
            <span className="font-display text-xl font-bold tracking-tight" style={{ color: "#FACFAD" }}>
              ImovelPrime
            </span>
          </button>
          <div className="relative z-10">
            <p className="font-display text-4xl font-semibold leading-tight text-white mb-4">
              Crie sua conta e comece hoje.
            </p>
            <p style={{ color: "#F8BD7F" }} className="text-sm leading-relaxed">
              {signupRole === "corretor"
                ? "Alcance mais clientes e feche negócios com agilidade."
                : "Salve favoritos, envie propostas e acompanhe tudo em um só lugar."}
            </p>
          </div>
          <p className="relative z-10 text-xs text-white/60">
            Mais de 10 mil imóveis à sua disposição.
          </p>
        </div>

        {/* Right panel */}
        <div className="flex-1 flex items-center justify-center p-8 overflow-y-auto">
          <div className="w-full max-w-sm py-8">
            <button onClick={() => setPage("home")} className="lg:hidden flex items-center gap-2 mb-8">
              <span style={{ color: "#F5AC72", fontSize: 20 }}>⌂</span>
              <span className="font-display text-lg font-bold" style={{ color: "#3E4E50" }}>ImovelPrime</span>
            </button>

            <h1 className="font-display text-3xl font-bold mb-1" style={{ color: "#3E4E50" }}>
              Criar conta
            </h1>
            <p className="text-sm mb-6" style={{ color: "#7a9194" }}>
              Preencha seus dados para começar
            </p>

            {/* Role toggle */}
            <div className="flex p-1 rounded-xl mb-7" style={{ background: "#f2ece7" }}>
              {([
                { key: "usuario", label: "Sou usuário" },
                { key: "corretor", label: "Sou corretor" },
              ] as const).map(r => (
                <button
                  key={r.key}
                  type="button"
                  onClick={() => { setSignupRole(r.key); setSignupError(false) }}
                  className="flex-1 py-2 rounded-lg text-sm font-medium transition-all"
                  style={
                    signupRole === r.key
                      ? { background: "#fff", color: "#3E4E50", boxShadow: "0 1px 3px rgba(62,78,80,0.12)" }
                      : { background: "transparent", color: "#8a9799" }
                  }
                >
                  {r.label}
                </button>
              ))}
            </div>

            <form onSubmit={handleSignup} className="flex flex-col gap-5">
              {/* Nome */}
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                  Nome completo
                </label>
                <input
                  type="text"
                  placeholder="Seu nome"
                  value={signupName}
                  onChange={e => { setSignupName(e.target.value); setSignupError(false) }}
                  required
                  className={inputBase}
                  style={{ border: errBorder(signupName.trim().length < 3), background: "#faf9f7", color: "#3E4E50" }}
                  onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                  onBlur={e => { e.currentTarget.style.borderColor = signupError && signupName.trim().length < 3 ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                />
              </div>

              {/* Telefone */}
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                  Telefone
                </label>
                <input
                  type="tel"
                  inputMode="numeric"
                  placeholder="(00) 00000-0000"
                  value={signupPhone}
                  onChange={e => { setSignupPhone(formatPhone(e.target.value)); setSignupError(false) }}
                  required
                  className={inputBase}
                  style={{ border: errBorder(signupPhone.replace(/\D/g, "").length < 10), background: "#faf9f7", color: "#3E4E50", letterSpacing: "0.04em" }}
                  onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                  onBlur={e => { e.currentTarget.style.borderColor = signupError && signupPhone.replace(/\D/g, "").length < 10 ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                />
              </div>

              {/* CPF */}
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                  CPF
                </label>
                <input
                  type="text"
                  inputMode="numeric"
                  placeholder="000.000.000-00"
                  value={signupCpf}
                  onChange={e => { setSignupCpf(formatCpf(e.target.value)); setSignupError(false) }}
                  required
                  className={inputBase}
                  style={{ border: errBorder(signupCpf.replace(/\D/g, "").length !== 11), background: "#faf9f7", color: "#3E4E50", letterSpacing: "0.04em" }}
                  onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                  onBlur={e => { e.currentTarget.style.borderColor = signupError && signupCpf.replace(/\D/g, "").length !== 11 ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                />
              </div>

              {/* CRECI (corretor apenas) */}
              {signupRole === "corretor" && (
                <div className="flex flex-col gap-1.5">
                  <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                    CRECI
                  </label>
                  <div className="relative">
                    <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-xs font-semibold" style={{ color: "#F5AC72" }}>
                      CRECI
                    </span>
                    <input
                      type="text"
                      placeholder="000000-F"
                      value={signupCreci}
                      onChange={e => { setSignupCreci(e.target.value.toUpperCase()); setSignupError(false) }}
                      required
                      className="w-full pl-14 pr-4 py-3 rounded-xl text-sm outline-none transition-all"
                      style={{ border: errBorder(!signupCreci.trim()), background: "#faf9f7", color: "#3E4E50" }}
                      onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                      onBlur={e => { e.currentTarget.style.borderColor = signupError && !signupCreci.trim() ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                    />
                  </div>
                  <p className="text-[11px]" style={{ color: "#aab8b9" }}>Número da sua carteirinha de corretor</p>
                </div>
              )}

              {/* Senha */}
              <div className="flex flex-col gap-1.5">
                <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                  Senha
                </label>
                <input
                  type="password"
                  placeholder="Mínimo 6 caracteres"
                  value={signupPassword}
                  onChange={e => { setSignupPassword(e.target.value); setSignupError(false) }}
                  required
                  className={inputBase}
                  style={{ border: errBorder(signupPassword.length < 6), background: "#faf9f7", color: "#3E4E50" }}
                  onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                  onBlur={e => { e.currentTarget.style.borderColor = signupError && signupPassword.length < 6 ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                />
              </div>

              {signupError && (
                <p className="text-xs" style={{ color: "#c0504d" }}>
                  Verifique os dados informados. Todos os campos são obrigatórios.
                </p>
              )}

              <button
                type="submit"
                className="w-full py-3 rounded-xl font-semibold text-sm transition-all hover:opacity-90 active:scale-[0.98]"
                style={{ background: "#3E4E50", color: "#FACFAD" }}
              >
                Criar conta
              </button>
            </form>

            <p className="text-center text-sm mt-6" style={{ color: "#7a9194" }}>
              Já tem conta?{" "}
              <button onClick={() => { setPage("login"); setSignupError(false) }} className="font-semibold hover:underline" style={{ color: "#F5AC72" }}>
                Entrar
              </button>
            </p>
          </div>
        </div>
      </div>
    )
  }

  if (page === "login") {
    return (
      <div className="min-h-screen flex" style={{ background: "#fff" }}>
        {/* Left panel */}
        <div
          className="hidden lg:flex flex-col justify-between w-[46%] p-12 relative overflow-hidden"
          style={{ background: "linear-gradient(145deg, #3E4E50 0%, #2a3638 100%)" }}
        >
          <div
            className="absolute inset-0 opacity-10"
            style={{
              backgroundImage: `url(https://images.unsplash.com/photo-1656038339605-f48e55b367d5?w=800&h=1200&fit=crop&auto=format)`,
              backgroundSize: "cover",
              backgroundPosition: "center",
            }}
          />
          <div className="relative z-10">
            <button
              onClick={() => setPage("home")}
              className="flex items-center gap-2 group"
            >
              <span style={{ color: "#F5AC72", fontSize: 22 }}>⌂</span>
              <span className="font-display text-xl font-bold tracking-tight" style={{ color: "#FACFAD" }}>
                ImovelPrime
              </span>
            </button>
          </div>
          <div className="relative z-10">
            <p className="font-display text-4xl font-semibold leading-tight text-white mb-4">
              {loginRole === "corretor"
                ? "Feche mais negócios com a plataforma dos corretores."
                : "O imóvel dos seus sonhos está a um passo."}
            </p>
            <p style={{ color: "#F8BD7F" }} className="text-sm leading-relaxed">
              {loginRole === "corretor"
                ? "Gerencie propostas e clientes em um só lugar."
                : "Conectando pessoas a lares desde 2018."}
            </p>
          </div>
          <div className="relative z-10 flex items-center gap-3">
            <div className="w-8 h-8 rounded-full overflow-hidden border-2" style={{ borderColor: "#F5AC72" }}>
              <img src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=64&h=64&fit=crop&auto=format" alt="Depoimento" className="w-full h-full object-cover" />
            </div>
            <p className="text-xs text-white/70 italic">
              "Encontrei meu apartamento em menos de uma semana." — Rafael T.
            </p>
          </div>
        </div>

        {/* Right panel */}
        <div className="flex-1 flex items-center justify-center p-8">
          <div className="w-full max-w-sm">
            <button
              onClick={() => setPage("home")}
              className="lg:hidden flex items-center gap-2 mb-8"
            >
              <span style={{ color: "#F5AC72", fontSize: 20 }}>⌂</span>
              <span className="font-display text-lg font-bold" style={{ color: "#3E4E50" }}>ImovelPrime</span>
            </button>

            <h1 className="font-display text-3xl font-bold mb-1" style={{ color: "#3E4E50" }}>
              Entrar
            </h1>
            <p className="text-sm mb-6" style={{ color: "#7a9194" }}>
              {loginRole === "corretor"
                ? "Acesse sua conta profissional de corretor"
                : "Acesse sua conta para continuar"}
            </p>

            {/* Role toggle */}
            <div
              className="flex p-1 rounded-xl mb-7"
              style={{ background: "#f2ece7" }}
            >
              {([
                { key: "corretor", label: "Sou corretor" },
                { key: "usuario", label: "Sou usuário" },
              ] as const).map(r => (
                <button
                  key={r.key}
                  type="button"
                  onClick={() => switchRole(r.key)}
                  className="flex-1 py-2 rounded-lg text-sm font-medium transition-all"
                  style={
                    loginRole === r.key
                      ? { background: "#fff", color: "#3E4E50", boxShadow: "0 1px 3px rgba(62,78,80,0.12)" }
                      : { background: "transparent", color: "#8a9799" }
                  }
                >
                  {r.label}
                </button>
              ))}
            </div>

            <form onSubmit={handleLogin} className="flex flex-col gap-5">
              {loginRole === "corretor" ? (
                <>
                  {/* CRECI */}
                  <div className="flex flex-col gap-1.5">
                    <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                      CRECI
                    </label>
                    <div className="relative">
                      <span className="absolute left-3.5 top-1/2 -translate-y-1/2 text-xs font-semibold" style={{ color: "#F5AC72" }}>
                        CRECI
                      </span>
                      <input
                        type="text"
                        placeholder="000000-F"
                        value={loginCreci}
                        onChange={e => { setLoginCreci(e.target.value.toUpperCase()); setLoginError(false) }}
                        required
                        className="w-full pl-14 pr-4 py-3 rounded-xl text-sm outline-none transition-all"
                        style={{
                          border: loginError && !loginCreci.trim() ? "1.5px solid #e07070" : "1.5px solid #e8ddd5",
                          background: "#faf9f7",
                          color: "#3E4E50",
                        }}
                        onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                        onBlur={e => { e.currentTarget.style.borderColor = loginError && !loginCreci.trim() ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                      />
                    </div>
                    <p className="text-[11px]" style={{ color: "#aab8b9" }}>Número da sua carteirinha de corretor</p>
                  </div>

                  {/* CPF */}
                  <div className="flex flex-col gap-1.5">
                    <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                      CPF
                    </label>
                    <input
                      type="text"
                      inputMode="numeric"
                      placeholder="000.000.000-00"
                      value={loginCpf}
                      onChange={e => { setLoginCpf(formatCpf(e.target.value)); setLoginError(false) }}
                      required
                      className="w-full px-4 py-3 rounded-xl text-sm outline-none transition-all"
                      style={{
                        border: loginError && loginCpf.replace(/\D/g, "").length !== 11 ? "1.5px solid #e07070" : "1.5px solid #e8ddd5",
                        background: "#faf9f7",
                        color: "#3E4E50",
                        letterSpacing: "0.04em",
                      }}
                      onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                      onBlur={e => { e.currentTarget.style.borderColor = loginError && loginCpf.replace(/\D/g, "").length !== 11 ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                    />
                  </div>
                </>
              ) : (
                /* E-mail */
                <div className="flex flex-col gap-1.5">
                  <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                    E-mail
                  </label>
                  <input
                    type="email"
                    placeholder="voce@email.com"
                    value={loginEmail}
                    onChange={e => { setLoginEmail(e.target.value); setLoginError(false) }}
                    required
                    className="w-full px-4 py-3 rounded-xl text-sm outline-none transition-all"
                    style={{
                      border: loginError && !emailValid ? "1.5px solid #e07070" : "1.5px solid #e8ddd5",
                      background: "#faf9f7",
                      color: "#3E4E50",
                    }}
                    onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                    onBlur={e => { e.currentTarget.style.borderColor = loginError && !emailValid ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                  />
                  <p className="text-[11px]" style={{ color: "#aab8b9" }}>Não precisa de CRECI para navegar pelos imóveis</p>
                </div>
              )}

              {/* Senha */}
              <div className="flex flex-col gap-1.5">
                <div className="flex items-center justify-between">
                  <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>
                    Senha
                  </label>
                  <a href="#" className="text-xs hover:underline" style={{ color: "#F5AC72" }}>Esqueci minha senha</a>
                </div>
                <input
                  type="password"
                  placeholder="••••••••"
                  value={loginPassword}
                  onChange={e => { setLoginPassword(e.target.value); setLoginError(false) }}
                  required
                  className="w-full px-4 py-3 rounded-xl text-sm outline-none transition-all"
                  style={{
                    border: loginError && loginPassword.length < 6 ? "1.5px solid #e07070" : "1.5px solid #e8ddd5",
                    background: "#faf9f7",
                    color: "#3E4E50",
                  }}
                  onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                  onBlur={e => { e.currentTarget.style.borderColor = loginError && loginPassword.length < 6 ? "#e07070" : "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                />
              </div>

              {loginError && (
                <p className="text-xs" style={{ color: "#c0504d" }}>
                  {loginRole === "corretor"
                    ? "Verifique os dados informados. CRECI, CPF e senha são obrigatórios."
                    : "Verifique os dados informados. Informe um e-mail válido e a senha."}
                </p>
              )}

              <button
                type="submit"
                className="w-full py-3 rounded-xl font-semibold text-sm transition-all hover:opacity-90 active:scale-[0.98]"
                style={{ background: "#3E4E50", color: "#FACFAD" }}
              >
                Entrar
              </button>

              <div className="flex items-center gap-3">
                <div className="flex-1 h-px" style={{ background: "#e8ddd5" }} />
                <span className="text-xs" style={{ color: "#aaa" }}>ou</span>
                <div className="flex-1 h-px" style={{ background: "#e8ddd5" }} />
              </div>

              <button
                type="button"
                onClick={() => { setPage("signup"); setLoginError(false) }}
                className="w-full py-3 rounded-xl font-medium text-sm border transition-all hover:bg-orange-50 active:scale-[0.98]"
                style={{ borderColor: "#F5AC72", color: "#3E4E50" }}
              >
                Criar conta gratuita
              </button>
            </form>

            <p className="text-center text-xs mt-6" style={{ color: "#aaa" }}>
              Ao entrar, você concorda com os{" "}
              <span className="underline cursor-pointer" style={{ color: "#F5AC72" }}>Termos de Uso</span>
            </p>
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="min-h-screen" style={{ background: "#ffffff" }}>
      {/* Navbar */}
      <nav
        className="sticky top-0 z-50 w-full"
        style={{
          background: "rgba(255,255,255,0.92)",
          backdropFilter: "blur(12px)",
          borderBottom: "1px solid rgba(245,172,114,0.18)",
        }}
      >
        <div className="max-w-7xl mx-auto px-5 lg:px-8 h-16 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <span style={{ color: "#F5AC72", fontSize: 24 }}>⌂</span>
            <span className="font-display text-xl font-bold tracking-tight" style={{ color: "#3E4E50" }}>
              ImovelPrime
            </span>
          </div>

          <div className="hidden md:flex items-center gap-7 text-sm font-medium" style={{ color: "#3E4E50" }}>
            {["Comprar", "Alugar", "Lançamentos", "Contato"].map(item => (
              <a
                key={item}
                href="#"
                className="transition-colors hover:opacity-70 pb-0.5"
                style={{ borderBottom: item === "Comprar" ? "2px solid #F5AC72" : "2px solid transparent" }}
              >
                {item}
              </a>
            ))}
          </div>

          <div className="flex items-center gap-3">
            <button
              onClick={() => setPage("login")}
              className="hidden md:block text-sm font-medium px-4 py-2 rounded-lg transition-all hover:opacity-80"
              style={{ color: "#3E4E50", border: "1.5px solid #F5AC72" }}
            >
              Entrar
            </button>
            <button
              className="hidden md:block text-sm font-semibold px-5 py-2 rounded-lg transition-all hover:opacity-90"
              style={{ background: "#3E4E50", color: "#FACFAD" }}
            >
              Anunciar
            </button>
            <button
              className="md:hidden p-2 rounded-lg"
              onClick={() => setMenuOpen(!menuOpen)}
              style={{ color: "#3E4E50" }}
            >
              <svg width="20" height="20" viewBox="0 0 20 20" fill="currentColor">
                {menuOpen
                  ? <path fillRule="evenodd" d="M4.293 4.293a1 1 0 011.414 0L10 8.586l4.293-4.293a1 1 0 111.414 1.414L11.414 10l4.293 4.293a1 1 0 01-1.414 1.414L10 11.414l-4.293 4.293a1 1 0 01-1.414-1.414L8.586 10 4.293 5.707a1 1 0 010-1.414z" clipRule="evenodd" />
                  : <path fillRule="evenodd" d="M3 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1zm0 5a1 1 0 011-1h12a1 1 0 110 2H4a1 1 0 01-1-1z" clipRule="evenodd" />
                }
              </svg>
            </button>
          </div>
        </div>

        {menuOpen && (
          <div className="md:hidden px-5 pb-4 flex flex-col gap-3 text-sm font-medium" style={{ color: "#3E4E50", borderTop: "1px solid rgba(245,172,114,0.15)" }}>
            {["Comprar", "Alugar", "Lançamentos", "Contato"].map(item => (
              <a key={item} href="#" className="py-1">{item}</a>
            ))}
            <button onClick={() => setPage("login")} className="text-left py-1" style={{ color: "#F5AC72" }}>Entrar</button>
          </div>
        )}
      </nav>

      {/* Hero */}
      <section className="relative w-full overflow-hidden" style={{ height: "clamp(420px, 55vh, 620px)" }}>
        <img
          src="https://images.unsplash.com/photo-1656038339605-f48e55b367d5?w=1600&h=900&fit=crop&auto=format"
          alt="Condomínio de luxo com piscina"
          className="absolute inset-0 w-full h-full object-cover"
        />
        <div
          className="absolute inset-0"
          style={{ background: "linear-gradient(to bottom, rgba(62,78,80,0.45) 0%, rgba(62,78,80,0.72) 100%)" }}
        />
        <div className="relative z-10 h-full flex flex-col items-center justify-center px-5 text-center">
          <p className="text-sm font-medium uppercase tracking-[0.18em] mb-3" style={{ color: "#F8BD7F" }}>
            Encontre seu próximo lar
          </p>
          <h1 className="font-display text-4xl md:text-5xl lg:text-6xl font-bold text-white mb-4 leading-tight">
            Imóveis que combinam <br className="hidden sm:block" />
            <em>com você.</em>
          </h1>
          <p className="text-white/75 text-sm md:text-base max-w-md mb-8">
            Mais de 4.800 imóveis em João Pessoa e região. Apartamentos, casas e coberturas com os melhores corretores.
          </p>

          {/* Search bar */}
          <div
            className="flex w-full max-w-xl rounded-2xl overflow-hidden shadow-2xl"
            style={{ background: "rgba(255,255,255,0.95)" }}
          >
            <input
              type="text"
              placeholder="Buscar por bairro, cidade ou código..."
              className="flex-1 px-5 py-4 text-sm outline-none bg-transparent"
              style={{ color: "#3E4E50" }}
            />
            <button
              className="px-6 py-4 text-sm font-semibold transition-all hover:opacity-90 shrink-0"
              style={{ background: "#F5AC72", color: "#3E4E50" }}
            >
              Buscar
            </button>
          </div>
        </div>
      </section>

      {/* Stats strip */}
      <div
        className="w-full py-4 border-b"
        style={{ background: "#faf7f4", borderColor: "rgba(245,172,114,0.18)" }}
      >
        <div className="max-w-7xl mx-auto px-5 lg:px-8 flex flex-wrap items-center justify-center gap-x-10 gap-y-2">
          {[
            { label: "Imóveis disponíveis", value: "4.800+" },
            { label: "Corretores ativos", value: "320" },
            { label: "Vendas em 2025", value: "1.240" },
            { label: "Cidades atendidas", value: "18" },
          ].map(s => (
            <div key={s.label} className="flex items-center gap-2 text-sm">
              <span className="font-display font-bold text-lg" style={{ color: "#F5AC72" }}>{s.value}</span>
              <span style={{ color: "#7a9194" }}>{s.label}</span>
            </div>
          ))}
        </div>
      </div>

      {/* Listings */}
      <main className="max-w-7xl mx-auto px-5 lg:px-8 py-14">
        <div className="flex flex-col sm:flex-row sm:items-end justify-between gap-4 mb-10">
          <div>
            <p className="text-xs font-medium uppercase tracking-widest mb-1" style={{ color: "#F5AC72" }}>
              Imóveis em destaque
            </p>
            <h2 className="font-display text-3xl font-semibold" style={{ color: "#3E4E50" }}>
              Mais procurados agora
            </h2>
          </div>
          <div className="flex gap-2">
            {(["todos", "apartamento", "casa"] as const).map(t => (
              <button
                key={t}
                onClick={() => setFilterType(t)}
                className="px-4 py-1.5 rounded-full text-xs font-medium capitalize transition-all"
                style={{
                  background: filterType === t ? "#3E4E50" : "transparent",
                  color: filterType === t ? "#FACFAD" : "#3E4E50",
                  border: "1.5px solid",
                  borderColor: filterType === t ? "#3E4E50" : "rgba(62,78,80,0.22)",
                }}
              >
                {t === "todos" ? "Todos" : t === "apartamento" ? "Apartamentos" : "Casas"}
              </button>
            ))}
          </div>
        </div>

        <div className="grid grid-cols-1 sm:grid-cols-2 xl:grid-cols-3 gap-5">
            {filtered.map(p => (
              <PropertyCard key={p.id} property={p} onOpen={() => { setSelectedProperty(p); setPage("detail") }} />
            ))}
            {filtered.length === 0 && (
              <div className="col-span-full text-center py-16" style={{ color: "#7a9194" }}>
                Nenhum imóvel encontrado para esse filtro.
              </div>
            )}
        </div>
      </main>

      {/* CTA section */}
      <section
        className="w-full py-16"
        style={{ background: "#faf7f4", borderTop: "1px solid rgba(245,172,114,0.15)" }}
      >
        <div className="max-w-3xl mx-auto px-5 text-center">
          <p className="text-xs font-medium uppercase tracking-widest mb-2" style={{ color: "#F5AC72" }}>
            Para corretores
          </p>
          <h2 className="font-display text-3xl font-semibold mb-4" style={{ color: "#3E4E50" }}>
            Anuncie seu imóvel e alcance milhares de compradores
          </h2>
          <p className="text-sm mb-7" style={{ color: "#7a9194" }}>
            Cadastre seu imóvel gratuitamente e tenha acesso a ferramentas exclusivas para corretores. Agende visitas, gerencie propostas e feche negócios mais rápido.
          </p>
          <div className="flex flex-col sm:flex-row items-center justify-center gap-3">
            <button
              className="px-7 py-3 rounded-xl font-semibold text-sm transition-all hover:opacity-90"
              style={{ background: "#3E4E50", color: "#FACFAD" }}
            >
              Cadastrar imóvel
            </button>
            <button
              className="px-7 py-3 rounded-xl font-medium text-sm border transition-all hover:bg-orange-50"
              style={{ borderColor: "#F5AC72", color: "#3E4E50" }}
            >
              Saber mais
            </button>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer
        className="w-full py-10 mt-0"
        style={{ background: "#3E4E50" }}
      >
        <div className="max-w-7xl mx-auto px-5 lg:px-8 flex flex-col md:flex-row items-center justify-between gap-4">
          <div className="flex items-center gap-2">
            <span style={{ color: "#F5AC72", fontSize: 20 }}>⌂</span>
            <span className="font-display font-bold text-lg" style={{ color: "#FACFAD" }}>ImovelPrime</span>
          </div>
          <p className="text-xs text-center md:text-right" style={{ color: "#8fa8aa" }}>
            © 2026 ImovelPrime. Todos os direitos reservados. · João Pessoa — PB
          </p>
        </div>
      </footer>
    </div>
  )
}

interface Property {
  id: number
  apiId?: string
  title: string
  image: string
  size: number
  rooms: number
  bathrooms: number
  location: string
  price: number
  seller: string
  tag: string | null
}

function PropertyCard({ property: p, onOpen }: { property: Property; onOpen?: () => void }) {
  const [proposed, setProposed] = useState(false)
  const [hover, setHover] = useState(false)

  return (
    <div
      className="rounded-2xl overflow-hidden flex flex-col"
      style={{ background: "#ffffff", border: "1px solid rgba(245,172,114,0.25)", transform: hover ? "translateY(-3px)" : "translateY(0)", transition: "transform 0.25s ease, box-shadow 0.25s ease", boxShadow: hover ? "0 12px 36px rgba(62,78,80,0.13), 0 2px 8px rgba(245,172,114,0.12)" : "0 2px 12px rgba(62,78,80,0.07)" }}
      onMouseEnter={() => setHover(true)}
      onMouseLeave={() => setHover(false)}
    >
      {/* Image */}
      <div className="relative w-full" style={{ height: 195 }}>
        <img
          src={p.image}
          alt={p.title}
          onClick={onOpen}
          className="w-full h-full object-cover cursor-pointer"
          style={{ transition: "transform 0.4s ease", transform: hover ? "scale(1.04)" : "scale(1)" }}
        />
        {p.tag && (
          <span
            className="absolute top-3 left-3 text-xs font-semibold px-2.5 py-0.5 rounded-full"
            style={{ background: "#F5AC72", color: "#3E4E50" }}
          >
            {p.tag}
          </span>
        )}
        <div
          className="absolute bottom-0 left-0 right-0 h-12"
          style={{ background: "linear-gradient(to top, rgba(255,255,255,0.3), transparent)" }}
        />
      </div>

      {/* Body */}
      <div className="flex flex-col flex-1 p-4 gap-3">
        <h3 className="font-display font-semibold text-base leading-snug" style={{ color: "#3E4E50" }}>
          {p.title}
        </h3>

        {/* Stats row */}
        <div className="flex flex-wrap gap-x-4 gap-y-1.5">
          <StatPill icon="📐" label={`${p.size} m²`} />
          <StatPill icon="🛏" label={`${p.rooms} quarto${p.rooms > 1 ? "s" : ""}`} />
          <StatPill icon="🚿" label={`${p.bathrooms} banheiro${p.bathrooms > 1 ? "s" : ""}`} />
        </div>

        {/* Location */}
        <div className="flex items-start gap-1.5 text-xs" style={{ color: "#7a9194" }}>
          <span className="mt-px shrink-0">📍</span>
          <span>{p.location}</span>
        </div>

        {/* Divider */}
        <div className="h-px w-full" style={{ background: "rgba(245,172,114,0.25)" }} />

        {/* Price + seller */}
        <div className="flex items-end justify-between">
          <div>
            <p className="text-xs mb-0.5" style={{ color: "#7a9194" }}>Preço</p>
            <p className="font-display font-bold text-lg leading-none" style={{ color: "#3E4E50" }}>
              {formatPrice(p.price)}
            </p>
          </div>
          <div className="flex items-center gap-2 text-right">
            <div>
              <p className="text-xs" style={{ color: "#7a9194" }}>Vendedor</p>
              <p className="text-xs font-medium" style={{ color: "#3E4E50" }}>{p.seller}</p>
            </div>
            <div
              className="w-7 h-7 rounded-full flex items-center justify-center text-xs font-bold shrink-0"
              style={{ background: "#FACFAD", color: "#3E4E50" }}
            >
              {p.seller.charAt(0)}
            </div>
          </div>
        </div>

        {/* CTA Button */}
        <button
          onClick={() => setProposed(prev => !prev)}
          className="w-full py-2.5 rounded-xl text-sm font-semibold transition-all duration-200 active:scale-[0.97]"
          style={{
            background: proposed ? "#3E4E50" : "#F5AC72",
            color: proposed ? "#FACFAD" : "#3E4E50",
          }}
        >
          {proposed ? "✓ Proposta enviada!" : "Mandar proposta"}
        </button>
      </div>
    </div>
  )
}

const galleryExtras = [
  "https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=800&h=600&fit=crop&auto=format",
  "https://images.unsplash.com/photo-1600566753086-00f18fb6b3ea?w=800&h=600&fit=crop&auto=format",
  "https://images.unsplash.com/photo-1600607687939-ce8a6c25118c?w=800&h=600&fit=crop&auto=format",
  "https://images.unsplash.com/photo-1600210492486-724fe5c67fb0?w=800&h=600&fit=crop&auto=format",
]

function PropertyDetail({ property: p, onBack }: { property: Property; onBack: () => void }) {
  const gallery = [p.image, ...galleryExtras]
  const [active, setActive] = useState(0)
  const [proposed, setProposed] = useState(false)
  const [offer, setOffer] = useState("")
  const [message, setMessage] = useState("")
  const [formaPagamento, setFormaPagamento] = useState<"A_VISTA" | "FINANCIAMENTO" | "PARCELADO">("A_VISTA")

  return (
    <div className="min-h-screen" style={{ background: "#ffffff" }}>
      {/* Top bar */}
      <nav
        className="sticky top-0 z-50 w-full"
        style={{ background: "rgba(255,255,255,0.92)", backdropFilter: "blur(12px)", borderBottom: "1px solid rgba(245,172,114,0.18)" }}
      >
        <div className="max-w-6xl mx-auto px-5 lg:px-8 h-16 flex items-center justify-between">
          <button onClick={onBack} className="flex items-center gap-2 text-sm font-medium" style={{ color: "#3E4E50" }}>
            <span style={{ color: "#F5AC72", fontSize: 18 }}>←</span> Voltar
          </button>
          <div className="flex items-center gap-2">
            <span style={{ color: "#F5AC72", fontSize: 22 }}>⌂</span>
            <span className="font-display text-lg font-bold tracking-tight" style={{ color: "#3E4E50" }}>ImovelPrime</span>
          </div>
        </div>
      </nav>

      <div className="max-w-6xl mx-auto px-5 lg:px-8 py-8">
        {/* Header */}
        <div className="flex flex-wrap items-start justify-between gap-3 mb-6">
          <div>
            {p.tag && (
              <span className="inline-block text-xs font-semibold px-2.5 py-0.5 rounded-full mb-2" style={{ background: "#F5AC72", color: "#3E4E50" }}>
                {p.tag}
              </span>
            )}
            <h1 className="font-display text-3xl font-bold leading-tight" style={{ color: "#3E4E50" }}>{p.title}</h1>
            <div className="flex items-center gap-1.5 text-sm mt-2" style={{ color: "#7a9194" }}>
              <span>📍</span><span>{p.location}</span>
            </div>
          </div>
          <div className="text-right">
            <p className="text-xs" style={{ color: "#7a9194" }}>Preço</p>
            <p className="font-display font-bold text-3xl leading-none" style={{ color: "#3E4E50" }}>{formatPrice(p.price)}</p>
          </div>
        </div>

        <div className="grid lg:grid-cols-3 gap-8">
          {/* Gallery */}
          <div className="lg:col-span-2">
            <div className="rounded-2xl overflow-hidden" style={{ border: "1px solid rgba(245,172,114,0.25)" }}>
              <img src={gallery[active]} alt={p.title} className="w-full object-cover" style={{ height: 420 }} />
            </div>
            <div className="grid grid-cols-5 gap-3 mt-3">
              {gallery.map((src, i) => (
                <button
                  key={i}
                  onClick={() => setActive(i)}
                  className="rounded-xl overflow-hidden transition-all"
                  style={{ height: 72, border: active === i ? "2px solid #F5AC72" : "2px solid transparent", opacity: active === i ? 1 : 0.7 }}
                >
                  <img src={src} alt={`Foto ${i + 1}`} className="w-full h-full object-cover" />
                </button>
              ))}
            </div>

            {/* Specs */}
            <div className="grid grid-cols-3 gap-3 mt-6">
              {[
                { icon: "📐", label: "Área", value: `${p.size} m²` },
                { icon: "🛏", label: "Quartos", value: `${p.rooms}` },
                { icon: "🚿", label: "Banheiros", value: `${p.bathrooms}` },
              ].map(s => (
                <div key={s.label} className="rounded-xl p-4 text-center" style={{ background: "#faf6f2", border: "1px solid rgba(245,172,114,0.25)" }}>
                  <div className="text-xl mb-1">{s.icon}</div>
                  <p className="font-display font-bold text-lg" style={{ color: "#3E4E50" }}>{s.value}</p>
                  <p className="text-xs" style={{ color: "#7a9194" }}>{s.label}</p>
                </div>
              ))}
            </div>

            {/* Description */}
            <div className="mt-6">
              <h2 className="font-display font-semibold text-lg mb-2" style={{ color: "#3E4E50" }}>Sobre o imóvel</h2>
              <p className="text-sm leading-relaxed" style={{ color: "#5c6d6f" }}>
                {p.title} com {p.size} m², {p.rooms} quarto{p.rooms > 1 ? "s" : ""} e {p.bathrooms} banheiro{p.bathrooms > 1 ? "s" : ""},
                localizado em {p.location}. Um espaço pensado para o conforto, com acabamento de alto padrão,
                boa iluminação natural e localização privilegiada, próximo a comércios, escolas e áreas de lazer.
              </p>
            </div>
          </div>

          {/* Proposal panel */}
          <aside className="lg:col-span-1">
            <div className="rounded-2xl p-6 lg:sticky lg:top-24" style={{ background: "#ffffff", border: "1px solid rgba(245,172,114,0.3)", boxShadow: "0 8px 28px rgba(62,78,80,0.1)" }}>
              <div className="flex items-center gap-3 mb-5">
                <div className="w-11 h-11 rounded-full flex items-center justify-center text-base font-bold shrink-0" style={{ background: "#FACFAD", color: "#3E4E50" }}>
                  {p.seller.charAt(0)}
                </div>
                <div>
                  <p className="text-xs" style={{ color: "#7a9194" }}>Vendedor</p>
                  <p className="text-sm font-semibold" style={{ color: "#3E4E50" }}>{p.seller}</p>
                </div>
              </div>

              {proposed ? (
                <div className="text-center py-6">
                  <div className="text-3xl mb-2">✓</div>
                  <p className="font-display font-semibold" style={{ color: "#3E4E50" }}>Proposta enviada!</p>
                  <p className="text-xs mt-1" style={{ color: "#7a9194" }}>{p.seller} entrará em contato em breve.</p>
                </div>
              ) : (
                <form
                  onSubmit={e => {
                    e.preventDefault()
                    if (p.apiId) {
                      const valor = Number(offer.replace(/[^\d,]/g, "").replace(",", ".")) || p.price
                      api.enviarProposta({
                        imovelId: p.apiId,
                        email: "ana@flexolx.com",
                        valor,
                        formaPagamento,
                        mensagem: message,
                      }).catch(() => {/* mantém confirmação otimista mesmo offline */})
                    }
                    setProposed(true)
                  }}
                  className="flex flex-col gap-4"
                >
                  <div className="flex flex-col gap-1.5">
                    <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>Valor da proposta</label>
                    <input
                      type="text"
                      inputMode="numeric"
                      placeholder={formatPrice(p.price)}
                      value={offer}
                      onChange={e => setOffer(e.target.value)}
                      className="w-full px-4 py-3 rounded-xl text-sm outline-none transition-all"
                      style={{ border: "1.5px solid #e8ddd5", background: "#faf9f7", color: "#3E4E50" }}
                      onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                      onBlur={e => { e.currentTarget.style.borderColor = "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                    />
                  </div>
                  <div className="flex flex-col gap-1.5">
                    <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>Forma de pagamento</label>
                    <select
                      value={formaPagamento}
                      onChange={e => setFormaPagamento(e.target.value as typeof formaPagamento)}
                      className="w-full px-4 py-3 rounded-xl text-sm outline-none transition-all"
                      style={{ border: "1.5px solid #e8ddd5", background: "#faf9f7", color: "#3E4E50" }}
                      onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                      onBlur={e => { e.currentTarget.style.borderColor = "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                    >
                      <option value="A_VISTA">À vista</option>
                      <option value="FINANCIAMENTO">Financiamento</option>
                      <option value="PARCELADO">Parcelado</option>
                    </select>
                  </div>
                  <div className="flex flex-col gap-1.5">
                    <label className="text-xs font-medium uppercase tracking-wider" style={{ color: "#3E4E50" }}>Mensagem</label>
                    <textarea
                      rows={3}
                      value={message}
                      onChange={e => setMessage(e.target.value)}
                      placeholder="Escreva uma mensagem para o vendedor..."
                      className="w-full px-4 py-3 rounded-xl text-sm outline-none transition-all resize-none"
                      style={{ border: "1.5px solid #e8ddd5", background: "#faf9f7", color: "#3E4E50" }}
                      onFocus={e => { e.currentTarget.style.borderColor = "#F5AC72"; e.currentTarget.style.boxShadow = "0 0 0 3px rgba(245,172,114,0.15)" }}
                      onBlur={e => { e.currentTarget.style.borderColor = "#e8ddd5"; e.currentTarget.style.boxShadow = "none" }}
                    />
                  </div>
                  <button type="submit" className="w-full py-3 rounded-xl text-sm font-semibold transition-all active:scale-[0.98]" style={{ background: "#F5AC72", color: "#3E4E50" }}>
                    Mandar proposta
                  </button>
                  <p className="text-[11px] text-center" style={{ color: "#aab8b9" }}>Sem compromisso. O vendedor responde diretamente.</p>
                </form>
              )}
            </div>
          </aside>
        </div>
      </div>
    </div>
  )
}

function StatPill({ icon, label }: { icon: string; label: string }) {
  return (
    <div className="flex items-center gap-1 text-xs" style={{ color: "#3E4E50" }}>
      <span>{icon}</span>
      <span className="font-medium">{label}</span>
    </div>
  )
}
