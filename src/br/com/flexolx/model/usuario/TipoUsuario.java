package br.com.flexolx.model.usuario;

/*
 * Foi utilizado um enum para representar os tipos de usuário
 * porque o conjunto de perfis é fixo e conhecido pelo sistema.
 *
 * Dessa forma evitamos o uso de Strings espalhadas pelo código,
 * reduzimos erros de digitação e aumentamos a segurança de tipo
 * (type safety), permitindo que o compilador valide os valores.
 */

/**
 * Enumeração que representa todos os tipos de perfis
 * disponíveis no sistema.
 *
 * Um usuário pode possuir um ou mais perfis simultaneamente,
 * permitindo diferentes permissões e funcionalidades.
 *
 * Exemplo:
 * Um usuário pode ser CLIENTE e PROPRIETARIO_DIRETO ao mesmo tempo.
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