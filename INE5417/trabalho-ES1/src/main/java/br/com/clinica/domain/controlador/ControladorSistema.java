package br.com.clinica.domain.controlador;

import br.com.clinica.domain.model.Role;
import br.com.clinica.domain.model.Usuario;
import br.com.clinica.presentation.TelaMenu;
import br.com.clinica.repository.UsuarioRepository; // 1. IMPORTE
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // 2. IMPORTE
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // <-- ADICIONE ESTA LINHA

@Service
public class ControladorSistema {

	private final ControladorAnimal controladorAnimal;
	private final ControladorTutor controladorTutor;
	private final ControladorProfissional controladorProfissional;
	private final ControladorLotacao controladorLotacao;
	private final TelaMenu telaMenu;

	// 3. NOVAS DEPENDÊNCIAS
	private final UsuarioRepository usuarioRepository;
	private final PasswordEncoder passwordEncoder;

	private Usuario usuarioLogado;

	// 4. CONSTRUTOR ATUALIZADO
	@Autowired
	public ControladorSistema(ControladorAnimal controladorAnimal,
			ControladorTutor controladorTutor,
			ControladorProfissional controladorProfissional,
			ControladorLotacao controladorLotacao,
			TelaMenu telaMenu,
			UsuarioRepository usuarioRepository, // Injeta
			PasswordEncoder passwordEncoder) { // Injeta
		this.controladorAnimal = controladorAnimal;
		this.controladorTutor = controladorTutor;
		this.controladorProfissional = controladorProfissional;
		this.controladorLotacao = controladorLotacao;
		this.telaMenu = telaMenu;
		this.usuarioRepository = usuarioRepository; // Atribui
		this.passwordEncoder = passwordEncoder; // Atribui
	}

	public void inicializaSistema(Usuario usuario) {
		this.usuarioLogado = usuario;
		System.out.println("Usuário logado: " + usuario.getUsername() + " | Role: " + usuario.getRole());
		abreMenuPrincipal();
	}

	private void abreMenuPrincipal() {
		Role role = usuarioLogado.getRole();

		while (true) {
			int opcao = telaMenu.exibirMenu(role);

			switch (opcao) {
			case 1: // Profissionais
				if (role == Role.ADMIN) {
					abreTelaProfissionais();
				} else {
					telaMenu.mostraMensagemAcessoNegado();
				}
				break;
			case 2: // Tutores
				if (role == Role.ADMIN || role == Role.SECRETARIA) {
					abreTelaTutores();
				} else {
					telaMenu.mostraMensagemAcessoNegado();
				}
				break;
			case 3: // Animais
				if (role == Role.ADMIN || role == Role.SECRETARIA) {
					abreTelaAnimais();
				} else {
					telaMenu.mostraMensagemAcessoNegado();
				}
				break;
			case 4: // Lotações
				if (role == Role.ADMIN) {
					abreTelaLotacoes();
				} else {
					telaMenu.mostraMensagemAcessoNegado();
				}
				break;
			case 5: // 5. NOVA OPÇÃO
				abreTelaAlterarSenha();
				break;
			case 0:
				encerraSistema();
				break;
			default:
				// Não faz nada, apenas re-exibe o menu
				break;
			}
		}
	}

	/**
	 * 6. NOVO MÉTODO PARA ALTERAR A SENHA
	 */
	@Transactional // Garante que a alteração será salva no banco
	private void abreTelaAlterarSenha() {
		String[] senhas = telaMenu.pegarDadosNovaSenha();
		if (senhas == null) {
			return; // Usuário cancelou
		}

		String senhaAntiga = senhas[0];
		String novaSenha = senhas[1];
		String confirmaNovaSenha = senhas[2];

		// 1. Verifica se a senha antiga está correta
		if (!passwordEncoder.matches(senhaAntiga, usuarioLogado.getPassword())) {
			telaMenu.mostraMensagem("Erro", "A 'Senha Antiga' está incorreta.");
			return;
		}

		// 2. Verifica se a nova senha é válida (mínimo 4 caracteres, por exemplo)
		if (novaSenha.length() < 4) {
			telaMenu.mostraMensagem("Erro", "A 'Nova Senha' deve ter pelo menos 4 caracteres.");
			return;
		}

		// 3. Verifica se a nova senha e a confirmação são iguais
		if (!novaSenha.equals(confirmaNovaSenha)) {
			telaMenu.mostraMensagem("Erro", "A 'Nova Senha' e a 'Confirmação' não são iguais.");
			return;
		}

		// 4. Se tudo estiver OK, criptografa e salva
		String novaSenhaCriptografada = passwordEncoder.encode(novaSenha);
		usuarioLogado.setPassword(novaSenhaCriptografada);
		usuarioRepository.save(usuarioLogado);

		telaMenu.mostraMensagem("Sucesso", "Senha alterada com sucesso!");
	}

	private void abreTelaAnimais() { controladorAnimal.exibirMenu(usuarioLogado); }
	private void abreTelaTutores() { controladorTutor.exibirMenu(usuarioLogado); }
	private void abreTelaProfissionais() { controladorProfissional.exibirMenu(usuarioLogado); }
	private void abreTelaLotacoes() { controladorLotacao.exibirMenu(usuarioLogado); }

	private void encerraSistema() {
		System.out.println("Encerrando o sistema...");
		System.exit(0);
	}
}
