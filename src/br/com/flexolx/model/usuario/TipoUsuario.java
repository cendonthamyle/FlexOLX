package br.com.flexolx.model.usuario;

/*
 * Foi utilizado um enum para representar os tipos de usuário
 * porque o conjunto de perfis é fixo e conhecido pelo sistema.
 *
 * Dessa forma, evita-se o uso de Strings espalhadas pelo código,
 * reduzindo erros de digitação e aumentando a segurança de tipo
 * (type safety), já que o compilador valida os valores possíveis.
 */

/**
 * Representa os tipos de perfis de usuário disponíveis no sistema.
 *
 * Cada constante identifica um perfil com permissões e responsabilidades
 * específicas dentro da aplicação.
 *
 * Um mesmo usuário pode possuir mais de um perfil simultaneamente,
 * permitindo o acesso a diferentes funcionalidades conforme as regras
 * de negócio.
 *
 * <p>Exemplo: um usuário pode possuir os perfis
 * {@link #CLIENTE} e {@link #PROPRIETARIO_DIRETO} ao mesmo tempo.</p>
 */
public enum TipoUsuario {
    /**
     * Usuário interessado em pesquisar, favoritar,
     * visitar ou negociar imóveis.
     */
    CLIENTE,

    /**
     * Empresa responsável pelo gerenciamento e divulgação
     * de imóveis cadastrados na plataforma.
     */
    IMOBILIARIA,

    /**
     * Profissional responsável pela intermediação de
     * negociações entre compradores, vendedores e imobiliárias.
     */
    CORRETOR,

    /**
     * Usuário com privilégios administrativos.
     *
     * Possui acesso às funcionalidades de gerenciamento
     * e manutenção do sistema.
     */
    ADMINISTRADOR,

    /**
     * Profissional responsável por realizar avaliações
     * técnicas de imóveis.
     */
    AVALIADOR,

    /**
     * Pessoa física que anuncia seus próprios imóveis
     * sem intermédio de uma imobiliária.
     */
    PROPRIETARIO_DIRETO
}