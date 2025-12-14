package br.com.clinica.domain.controlador;

import br.com.clinica.domain.model.Usuario;
import br.com.clinica.presentation.TelaLogin;
import br.com.clinica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ControladorLogin {

	@Autowired
	private TelaLogin telaLogin;
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PasswordEncoder passwordEncoder; // Bean do SecurityConfig

	/**
	 * Tenta autenticar o usuário.
	 * Continua pedindo as credenciais até o login ser válido ou o usuário cancelar.
	 * @return O objeto Usuario logado, ou null se o usuário desistiu.
	 */
	@Transactional(readOnly = true)
	public Usuario autenticar() {
		while (true) {
			String[] credenciais = telaLogin.pegarCredenciais();

			// 1. Usuário cancelou ou fechou a janela de login
			if (credenciais == null) {
				return null; // Encerra a tentativa de login
			}

			String username = credenciais[0];
			String senhaDigitada = credenciais[1];

			// 2. Tenta buscar o usuário no banco
			Optional<Usuario> usuarioOpt = usuarioRepository.findByUsername(username);

			// 3. Verifica se o usuário existe E se a senha bate
			if (usuarioOpt.isPresent() && passwordEncoder.matches(senhaDigitada, usuarioOpt.get().getPassword())) {
				// Sucesso! Retorna o usuário logado
				return usuarioOpt.get();
			} else {
				// Falha
				telaLogin.mostraMensagemErro("Usuário ou senha inválidos. Tente novamente.");
				// O loop 'while(true)' fará a tela de login aparecer novamente
			}
		}
	}
}
