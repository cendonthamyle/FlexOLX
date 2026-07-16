package br.com.flexolx.model.usuario;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

/*
 * Escolha da estrutura:
 *
 * Foi utilizada CopyOnWriteArrayList porque a aplicação realiza
 * muitas leituras da lista de perfis e poucas modificações.
 *
 * Essa estrutura elimina ConcurrentModificationException durante
 * iterações concorrentes, aumentando a segurança em ambientes
 * multi-thread.
 */
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Representa um usuário do sistema.
 *
 * Responsabilidades:
 * - Armazenar os dados básicos do usuário;
 * - Gerenciar autenticação por senha;
 * - Gerenciar os perfis associados ao usuário;
 * - Garantir validações de integridade;
 * - Proteger operações críticas contra acesso concorrente.
 */
public class Usuario implements Serializable {
    /**
     * Identificador de versão utilizado pela serialização.
     *
     * Foi atualizado porque a estrutura interna passou a utilizar
     * coleções thread-safe (CopyOnWriteArrayList).
     */
    private static final long serialVersionUID = 7L; 

    /**
     * Expressão regular utilizada para validar o formato do e-mail.
     * Não verifica se o e-mail existe, apenas sua estrutura.
     */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    /** Identificador único e imutável do usuário. */
    private final String id;

    private String nome;
    private String email;

    /**
     * A senha nunca é armazenada em texto puro.
     * 
     * Para simplificação acadêmica foi utilizado SHA-256.
     * Apenas o hash gerado com SHA-256 + Salt é persistido.
     */
    private String senhaHash;
    
    /**
     * Salt exclusivo para este usuário.
     *
     * O Salt protege contra ataques de Rainbow Tables.
     * Mesmo que dois usuários utilizem a mesma senha,
     * os hashes finais serão diferentes.
     */

    private final String salt; 

    private String telefone;

    /**
     * Lista de perfis do usuário.
     *
     * Foi utilizada CopyOnWriteArrayList para evitar
     * ConcurrentModificationException durante iterações
     * simultâneas em ambientes concorrentes.
     */
    private final List<PerfilUsuario> perfis = new CopyOnWriteArrayList<>();

    /**
     * Construtor responsável por validar todos os dados recebidos
     * antes da criação do usuário.
     *
     * Caso qualquer informação seja inválida,
     * nenhuma instância é criada.
     */
    public Usuario(String nome, String email, String senha, String telefone, PerfilUsuario perfilInicial) {
        ResultadoValidacao resultado = new ResultadoValidacao();
        
        // Validação dos dados básicos
        if (nome == null || nome.trim().isEmpty()) resultado.adicionarErro("O nome não pode estar vazio.");
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) resultado.adicionarErro("Formato de e-mail inválido.");
        if (senha == null || senha.trim().length() < 6) resultado.adicionarErro("A senha deve ter pelo menos 6 caracteres.");
        if (telefone == null || telefone.replaceAll("\\D", "").length() < 10) resultado.adicionarErro("Telefone deve conter DDD.");
        
        // O usuário obrigatoriamente deve possuir um perfil.
        if (perfilInicial != null) {
            perfilInicial.validar(resultado);
        } else {
            resultado.adicionarErro("Pelo menos um perfil de usuário é obrigatório.");
        }

        // Caso exista qualquer erro encontrado durante as validações,
        // interrompe imediatamente a criação do objeto.
        if (!resultado.ehValido()) {
            throw new IllegalArgumentException("Dados inválidos: " + String.join(", ", resultado.getErros()));
        }

        // Inicialização do objeto
        this.id = UUID.randomUUID().toString();
        this.nome = nome.trim();
        this.email = email.trim().toLowerCase();

        // Cada usuário recebe um salt exclusivo.
        this.salt = gerarSalt();

        // A senha é armazenada apenas em formato criptografado.
        this.senhaHash = gerarHashSenha(senha, this.salt);

        // Apenas números são armazenados no telefone.
        this.telefone = telefone.replaceAll("\\D", "");
        
        this.perfis.add(perfilInicial);
    }

    /**
     * Realiza autenticação comparando:
     * - e-mail informado
     * - hash da senha informada
     *
     * A senha digitada nunca é comparada diretamente.
     */
    public boolean autenticar(String emailTentativa, String senhaTentativa) {
        if (emailTentativa == null || senhaTentativa == null) return false;
        String hashTentativa = gerarHashSenha(senhaTentativa, this.salt);
        return this.email.equalsIgnoreCase(emailTentativa.trim()) && this.senhaHash.equals(hashTentativa);
    }

    /**
     * Altera a senha do usuário.
     *
     * synchronized garante que duas alterações simultâneas
     * não ocorram ao mesmo tempo.
     */
    public synchronized void alterarSenha(String senhaAntiga, String novaSenha) {
        if (!autenticar(this.email, senhaAntiga)) {
            throw new IllegalArgumentException("A senha antiga fornecida está incorreta.");
        }
        if (novaSenha == null || novaSenha.trim().length() < 6) {
            throw new IllegalArgumentException("A nova senha deve ter pelo menos 6 caracteres.");
        }
        this.senhaHash = gerarHashSenha(novaSenha, this.salt);
    }

    /**
     * Gera um Salt aleatório de 16 bytes.
     *
     * O SecureRandom produz valores criptograficamente seguros.
     */
    private String gerarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Gera o hash SHA-256 da senha concatenada com o Salt.
     *
     * Fluxo:
     * senha -> senha + salt -> SHA-256 -> Base64
     */
    private String gerarHashSenha(String senhaLimpa, String saltUsuario) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String senhaComSalt = senhaLimpa + saltUsuario;
            byte[] hash = digest.digest(senhaComSalt.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("Erro crítico: SHA-256 indisponível.", e);
        }
    }

    /**
     * Adiciona um novo perfil ao usuário.
     *
     * Regras:
     * - não aceita perfil nulo;
     * - não permite perfis duplicados;
     * - valida o perfil antes da inserção.
     */
    public synchronized void adicionarPerfil(PerfilUsuario novoPerfil) {
        if (novoPerfil == null) throw new IllegalArgumentException("Perfil não pode ser nulo.");
        
        for (PerfilUsuario p : perfis) {
            if (p.getTipo() == novoPerfil.getTipo()) {
                throw new IllegalArgumentException("O usuário já possui o perfil do tipo: " + novoPerfil.getTipo());
            }
        }

        ResultadoValidacao r = new ResultadoValidacao();
        novoPerfil.validar(r);
        if (!r.ehValido()) {
            throw new IllegalArgumentException("Perfil inválido: " + String.join(", ", r.getErros()));
        }
        this.perfis.add(novoPerfil);
    }

    /**
     * Remove um perfil do usuário.
     *
     * Regras:
     * - sempre deve existir pelo menos um perfil;
     * - o perfil deve existir;
     * - dependências externas são verificadas antes da remoção.
     *
     * A validação de dependências não é responsabilidade da classe Usuario.
     * Ela é delegada para outra camada através de uma interface,
     * reduzindo acoplamento e facilitando manutenção e testes.
     */
    public synchronized void removerPerfil(TipoUsuario tipo, ValidadorDependenciaPerfil validador) {
        if (perfis.size() <= 1) {
            throw new IllegalStateException("O usuário deve manter pelo menos um perfil ativo.");
        }
        if (!possuiPerfil(tipo)) {
            throw new IllegalArgumentException("O usuário não possui o perfil do tipo: " + tipo);
        }

        if (validador != null) {
            validador.verificarDependencias(this.id, tipo);
        }

        perfis.removeIf(p -> p.getTipo() == tipo);
    }

    /**
     * Verifica se o usuário possui determinado perfil.
     */
    public boolean possuiPerfil(TipoUsuario tipo) {
        return perfis.stream().anyMatch(p -> p.getTipo() == tipo);
    }

    public String getId() { return id; }
    
    /**
     * Retorna uma cópia da lista de perfis.
     *
     * Isso impede que outras classes alterem diretamente
     * a coleção interna da classe.
     */
    public List<PerfilUsuario> getPerfis() { return new ArrayList<>(perfis); }

    public synchronized String getNome() { return nome; }
    public synchronized String getEmail() { return email; }
    public synchronized String getTelefone() { return telefone; }
    public String getSalt() { return salt; }
    public String getSenhaHash() { return senhaHash; }

    public synchronized void setNome(String nome) {
        if (nome == null || nome.trim().isEmpty()) throw new IllegalArgumentException("Nome inválido.");
        this.nome = nome.trim();
    }

    public synchronized void setEmail(String email) {
        if (email == null || !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new IllegalArgumentException("Formato de e-mail inválido.");
        }
        this.email = email.trim().toLowerCase();
    }

    public synchronized void setTelefone(String telefone) {
        if (telefone == null) throw new IllegalArgumentException("Telefone nulo.");
        String apenasNumeros = telefone.replaceAll("\\D", "");
        if (apenasNumeros.length() < 10) throw new IllegalArgumentException("Telefone deve conter DDD.");
        this.telefone = apenasNumeros;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Usuario usuario = (Usuario) o;
        return id.equals(usuario.id);
    }

    @Override
    public int hashCode() { return id.hashCode(); }

    @Override
    public String toString() {
        String tiposStr = perfis.stream()
                .map(p -> p.getTipo().toString())
                .collect(Collectors.joining(", "));
        
        String detalhesStr = perfis.stream()
                .map(Object::toString)
                .collect(Collectors.joining(" | "));

        return String.format("ID: %s | Nome: %s | Email: %s | Perfis: [%s] | Detalhes: [%s]", 
            id, nome, email, tiposStr, detalhesStr);
    }
}