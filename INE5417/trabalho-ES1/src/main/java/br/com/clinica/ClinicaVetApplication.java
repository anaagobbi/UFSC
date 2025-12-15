package br.com.clinica;

import br.com.clinica.domain.controlador.ControladorSistema;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import br.com.clinica.domain.controlador.ControladorLogin; // 1. IMPORTE O NOVO CONTROLADOR
import br.com.clinica.domain.model.Usuario; // 2. IMPORTE O MODELO USUARIO

@SpringBootApplication
public class ClinicaVetApplication {

	public static void main(String[] args) {
		SpringApplicationBuilder builder = new SpringApplicationBuilder(ClinicaVetApplication.class);
		builder.headless(false);
		ConfigurableApplicationContext context = builder.run(args);

		// --- LÓGICA DE INICIALIZAÇÃO ALTERADA ---

		// 1. Pega o Controlador de Login
		ControladorLogin controladorLogin = context.getBean(ControladorLogin.class);

		// 2. Chama o método de autenticação (que abre a tela de login)
		Usuario usuarioLogado = controladorLogin.autenticar();

		// 3. Verifica se o login foi bem-sucedido
		if (usuarioLogado != null) {
			// 4. Se foi, pega o Controlador do Sistema
			ControladorSistema sistema = context.getBean(ControladorSistema.class);

			// 5. Inicia o sistema principal, passando o usuário logado
			sistema.inicializaSistema(usuarioLogado); // <-- (Vamos criar este método agora)
		} else {
			// 6. Se o usuário cancelou o login, encerra a aplicação
			System.out.println("Login cancelado. Encerrando o sistema.");
			System.exit(0);
		}
	}
}
