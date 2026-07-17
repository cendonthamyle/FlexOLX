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

import br.com.flexolx.model.enums.TipoUsuario;

/**
 * Representa um usuário do sistema.
 *
 * É responsável por armazenar os dados cadastrais do usuário,
 * gerenciar sua autenticação, controlar os perfis associados e
 * garantir a integridade das informações por meio de validações.
 *
 * A senha do usuário é armazenada exclusivamente na forma de hash
 * utilizando o algoritmo SHA-256 combinado com um salt exclusivo
 * para cada instância, aumentando a segurança contra ataques de
 * pré-computação.
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
     * Não verifica se o e-mail existe, apenas sua estrutura sintática.
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
     * Salt exclusivo utilizado na geração do hash da senha.
     *
     * O uso de um salt garante que senhas iguais produzam hashes
     * diferentes, reduzindo a vulnerabilidade a ataques do tipo
     * Rainbow Table.
     */
    private final String salt; 

    private String telefone;

    /**
     * Lista de perfis associados ao usuário.
     *
     * Foi utilizada a implementação {@code CopyOnWriteArrayList} para
     * evitar {@code ConcurrentModificationException} durante iterações
     * concorrentes e proporcionar maior segurança em ambientes
     * multi-thread.
     */
    private final List<PerfilUsuario> perfis = new CopyOnWriteArrayList<>();

    /**
     * Cria um novo usuário após validar todos os dados informados.
     *
     * Caso alguma informação seja inválida, nenhuma instância será
     * criada e uma exceção será lançada contendo todas as
     * inconsistências encontradas durante a validação.
     *
     * @param nome nome do usuário.
     * @param email endereço de e-mail do usuário.
     * @param senha senha em texto puro utilizada para gerar o hash.
     * @param telefone telefone do usuário.
     * @param perfilInicial perfil inicial obrigatório do usuário.
     *
     * @throws IllegalArgumentException caso algum dos dados informados
     * seja inválido.
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
     * Verifica se as credenciais informadas pertencem ao usuário.
     *
     * A autenticação é realizada comparando o e-mail informado com o
     * endereço cadastrado e o hash da senha fornecida com o hash
     * armazenado para o usuário.
     *
     * @param emailTentativa e-mail informado para autenticação.
     * @param senhaTentativa senha informada para autenticação.
     * @return {@code true} caso as credenciais sejam válidas;
     * {@code false} caso contrário.
     */
    public boolean autenticar(String emailTentativa, String senhaTentativa) {
        if (emailTentativa == null || senhaTentativa == null) return false;
        String hashTentativa = gerarHashSenha(senhaTentativa, this.salt);
        return this.email.equalsIgnoreCase(emailTentativa.trim()) && this.senhaHash.equals(hashTentativa);
    }

    /**
     * Altera a senha do usuário.
     *
     * A alteração somente é realizada caso a senha atual esteja correta
     * e a nova senha atenda aos critérios mínimos de validação.
     *
     * Este método é sincronizado para evitar alterações concorrentes
     * no estado do objeto.
     *
     * @param senhaAntiga senha atualmente cadastrada.
     * @param novaSenha nova senha do usuário.
     * @throws IllegalArgumentException caso a senha atual esteja
     * incorreta ou a nova senha seja inválida.
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
     * Gera um salt criptograficamente seguro.
     *
     * Cada usuário recebe um salt exclusivo, utilizado durante a geração
     * do hash da senha para reduzir a vulnerabilidade a ataques de
     * pré-computação.
     *
     * @return salt codificado em Base64.
     */
    private String gerarSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);
        return Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Gera o hash da senha utilizando o algoritmo SHA-256.
     *
     * O hash é calculado a partir da concatenação da senha em texto puro
     * com o salt do usuário.
     *
     * @param senhaLimpa senha em texto puro.
     * @param saltUsuario salt utilizado na geração do hash.
     * @return hash da senha codificado em Base64.
     * @throws IllegalStateException caso o algoritmo SHA-256 não esteja
     * disponível na plataforma de execução.
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
     * Antes da inclusão, o perfil é validado e verificado para garantir
     * que ainda não exista outro perfil do mesmo tipo associado ao usuário 
     * ou que seja nulo.
     *
     * Este método é sincronizado para impedir modificações concorrentes
     * na coleção de perfis.
     *
     * @param novoPerfil perfil que será adicionado ao usuário.
     * @throws IllegalArgumentException caso o perfil seja nulo, inválido
     * ou já exista um perfil do mesmo tipo.
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
     * A remoção somente é permitida quando:
     * <ul>
     *     <li>o usuário possuir mais de um perfil;</li>
     *     <li>o perfil informado existir;</li>
     *     <li>não houver dependências que impeçam sua remoção.</li>
     * </ul>
     *
     * A verificação de dependências é delegada para uma implementação de
     * {@link ValidadorDependenciaPerfil}, mantendo esta classe desacoplada
     * das regras específicas de negócio.
     *
     * @param tipo tipo do perfil que será removido.
     * @param validador componente responsável por verificar dependências
     * antes da remoção. Pode ser {@code null}.
     *
     * @throws IllegalArgumentException caso o usuário não possua o perfil
     * informado.
     *
     * @throws IllegalStateException caso o usuário fique sem perfis ou
     * existam dependências que impeçam a remoção.
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
     * Verifica se o usuário possui um perfil de determinado tipo.
     *
     * @param tipo tipo do perfil a ser consultado.
     * @return {@code true} se o usuário possuir o perfil informado;
     * {@code false} caso contrário.
     */
    public boolean possuiPerfil(TipoUsuario tipo) {
        return perfis.stream().anyMatch(p -> p.getTipo() == tipo);
    }

    public String getId() { return id; }
    
    /**
     * Retorna uma cópia da lista de perfis associados ao usuário.
     *
     * Uma cópia defensiva é retornada para impedir que outras classes
     * modifiquem diretamente a coleção interna.
     *
     * @return lista contendo os perfis do usuário.
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

    /**
     * Compara este usuário com outro objeto.
     *
     * Dois usuários são considerados iguais quando possuem o mesmo
     * identificador único.
     *
     * @param o objeto a ser comparado.
     * @return {@code true} se ambos representam o mesmo usuário;
     * {@code false} caso contrário.
     */
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