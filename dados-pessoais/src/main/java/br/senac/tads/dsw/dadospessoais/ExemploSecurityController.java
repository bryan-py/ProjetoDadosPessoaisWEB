package br.senac.tads.dsw.dadospessoais;

import br.senac.tads.dsw.dadospessoais.seguranca.UsuarioSistema;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@SecurityRequirement(name = "bearerJwt")
public class ExemploSecurityController {

    private final UserDetailsService userDetailsService;

    // Injetamos o UserDetailsService para carregar o UsuarioSistema completo pelo username,
    // já que o JwtFilter armazena apenas o username (String) no SecurityContextHolder.
    public ExemploSecurityController(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    // Acessível apenas para usuários autenticados.
    // auth.getPrincipal() retorna o username (String) extraído do JWT pelo JwtFilter.
    // O cast para UsuarioSistema é seguro: o UsuarioSistemaService sempre retorna
    // instâncias desta classe concreta em loadUserByUsername().
    //
    // ATENÇÃO — abordagem exclusivamente didática:
    // retornar diretamente a entidade de domínio expõe campos internos (hash da senha,
    // flags de estado da conta) que não deveriam aparecer em uma API de produção.
    // Em um sistema real, projete um DTO de resposta contendo apenas os dados necessários.
    @GetMapping("/me")
    public UsuarioSistema obterDadosUsuarioLogado(Authentication auth) {
        String username = (String) auth.getPrincipal();
        return (UsuarioSistema) userDetailsService.loadUserByUsername(username);
    }

    public record MensagemRoleDto(String usuario, String mensagem) {}

    // Endpoint destinado apenas a usuários com ROLE_ADMIN.
    // O controle de acesso real por role será adicionado no passo 7.6.
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/acesso/admin")
    public MensagemRoleDto mensagemAdmin(Authentication auth) {
        return new MensagemRoleDto(
            (String) auth.getPrincipal(),
            "Mensagem para usuário com Role ADMIN"
        );
    }

    // Endpoint destinado apenas a usuários com ROLE_GERENTE.
    @PreAuthorize("hasRole('GERENTE')")
    @GetMapping("/acesso/gerente")
    public MensagemRoleDto mensagemGerente(Authentication auth) {
        return new MensagemRoleDto(
            (String) auth.getPrincipal(),
            "Mensagem para usuário com Role GERENTE"
        );
    }

    // Endpoint destinado apenas a usuários com ROLE_USER.
    @PreAuthorize("hasRole('USER')")
    @GetMapping("/acesso/user")
    public MensagemRoleDto mensagemUser(Authentication auth) {
        return new MensagemRoleDto(
            (String) auth.getPrincipal(),
            "Mensagem para usuário com Role USER"
        );
    }
}
