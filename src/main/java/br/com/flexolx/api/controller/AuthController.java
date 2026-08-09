package br.com.flexolx.api.controller;

import br.com.flexolx.api.dto.LoginRequest;
import br.com.flexolx.api.dto.RegisterRequest;
import br.com.flexolx.api.dto.UsuarioResponse;
import br.com.flexolx.controller.GerenciadorUsuarios;
import br.com.flexolx.model.usuario.PerfilCliente;
import br.com.flexolx.model.usuario.PerfilCorretor;
import br.com.flexolx.model.usuario.PerfilProprietarioDireto;
import br.com.flexolx.model.usuario.PerfilUsuario;
import br.com.flexolx.model.usuario.Usuario;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de autenticação — equivalentes a {@code POST /api/auth/login} e
 * {@code POST /api/auth/register} da versão HttpServer.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final GerenciadorUsuarios usuarios;

    public AuthController(GerenciadorUsuarios usuarios) {
        this.usuarios = usuarios;
    }

    @PostMapping("/login")
    public UsuarioResponse login(@Valid @RequestBody LoginRequest body) {
        // AutenticacaoException é convertida em 401 pelo GlobalExceptionHandler.
        Usuario u = usuarios.login(body.email(), body.senha());
        return UsuarioResponse.de(u);
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest body) {
        String tipo = body.tipo() == null ? "CLIENTE" : body.tipo().toUpperCase();

        PerfilUsuario perfil = switch (tipo) {
            case "CORRETOR" -> new PerfilCorretor(body.creci());
            case "PROPRIETARIO_DIRETO" -> new PerfilProprietarioDireto();
            default -> new PerfilCliente();
        };

        Usuario novo = new Usuario(body.nome(), body.email(), body.senha(), body.telefone(), perfil);
        usuarios.adicionar(novo);

        return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioResponse.de(novo));
    }
}
