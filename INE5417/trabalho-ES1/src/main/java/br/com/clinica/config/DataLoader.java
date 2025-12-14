package br.com.clinica.config;

import br.com.clinica.domain.model.Role;
import br.com.clinica.domain.model.Usuario;
import br.com.clinica.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private PasswordEncoder passwordEncoder; // O Bean que criamos no SecurityConfig

	/**
	 * Este método é executado automaticamente pelo Spring
	 * logo após a inicialização.
	 */
	@Override
	public void run(String... args) throws Exception {

		// 1. Verifica se o usuário 'admin' já existe no banco
		if (usuarioRepository.findByUsername("admin").isEmpty()) {

			System.out.println(">>> Criando usuário ADMIN padrão...");

			// 2. Criptografa a senha "admin"
			String senhaCriptografada = passwordEncoder.encode("admin");

			// 3. Cria o novo usuário
			Usuario admin = new Usuario("admin", senhaCriptografada, Role.ADMIN);

			// 4. Salva no banco
			usuarioRepository.save(admin);

			System.out.println(">>> Usuário ADMIN criado com sucesso!");
		} else {
			System.out.println(">>> Usuário ADMIN já existe.");
		}
	}
}
