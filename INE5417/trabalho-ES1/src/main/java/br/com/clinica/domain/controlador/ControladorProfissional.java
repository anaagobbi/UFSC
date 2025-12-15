package br.com.clinica.domain.controlador;

import br.com.clinica.domain.model.Lotacao;
import br.com.clinica.domain.model.Profissional;
import br.com.clinica.domain.model.Role;
import br.com.clinica.domain.model.Usuario; // 1. IMPORTE O USUARIO
import br.com.clinica.repository.LotacaoRepository;
import br.com.clinica.repository.ProfissionalRepository;
import br.com.clinica.repository.UsuarioRepository; // 2. IMPORTE O REPOSITÓRIO DO USUÁRIO
import br.com.clinica.presentation.TelaProfissional;
import br.com.clinica.util.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder; // 3. IMPORTE O ENCODER
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ControladorProfissional {

	private final TelaProfissional telaProfissional;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Autowired
	private ProfissionalRepository profissionalRepository;
	@Autowired
	private LotacaoRepository lotacaoRepository;

	// 4. INJETE O REPOSITÓRIO E O ENCODER DE SENHA
	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	public ControladorProfissional(TelaProfissional telaProfissional) {
		this.telaProfissional = telaProfissional;
	}

	@Transactional(readOnly = true)
	public Profissional pegaProfissionalPorCpf(String cpf) {
		return profissionalRepository.findById(cpf.replaceAll("[^0-9]", "")).orElse(null);
	}

	@Transactional
	private void cadastrarProfissional() {
		List<Lotacao> lotacoesDisponiveis = lotacaoRepository.findAll();
		if (lotacoesDisponiveis.isEmpty()) {
			telaProfissional.mostraMensagem("Não existem lotações cadastradas. Cadastre uma lotação primeiro.");
			return;
		}

		String[] dados = telaProfissional.pegarDados(lotacoesDisponiveis);
		if (dados == null) return;

		// --- VALIDAÇÃO DE FORMATO ---
		String cpf = dados[0];
		String nome = dados[1];
		String telefone = dados[2];
		String cep = dados[3];
		String dataNascStr = dados[4];

		if (!Validador.isCpfValido(cpf)) {
			telaProfissional.mostraMensagem("Erro: O CPF digitado é inválido!");
			return;
		}
		// ... (outras validações de telefone e cep) ...
		if (!Validador.isTelefoneValido(telefone)) {
			telaProfissional.mostraMensagem("Erro: O Telefone é inválido. (Deve ter 10 ou 11 dígitos)");
			return;
		}
		if (!Validador.isCepValido(cep)) {
			telaProfissional.mostraMensagem("Erro: O CEP é inválido. (Deve ter 8 dígitos)");
			return;
		}

		String cpfLimpo = cpf.replaceAll("[^0-9]", "");
		String telLimpo = telefone.replaceAll("[^0-9]", "");
		String cepLimpo = cep.replaceAll("[^0-9]", "");

		// 5. NOVAS VERIFICAÇÕES DE EXISTÊNCIA
		if (pegaProfissionalPorCpf(cpfLimpo) != null) {
			telaProfissional.mostraMensagem("ATENÇÃO: Já existe um Profissional com este CPF!");
			return;
		}
		if (usuarioRepository.findByUsername(cpfLimpo).isPresent()) {
			telaProfissional.mostraMensagem("ATENÇÃO: Já existe um Usuário com este CPF como login!");
			return;
		}
		// --- FIM DAS NOVAS VERIFICAÇÕES ---

		// Se passou em tudo, prossegue com o cadastro
		try {
			Lotacao lotacao = lotacoesDisponiveis.get(Integer.parseInt(dados[5]));
			LocalDate data = LocalDate.parse(dataNascStr, formatter);

			// 6. SALVA O PROFISSIONAL PRIMEIRO
			Profissional p = new Profissional(cpfLimpo, nome, telLimpo, cepLimpo, data, lotacao);
			profissionalRepository.save(p);

			// 7. CRIA E SALVA O USUÁRIO AUTOMATICAMENTE
			String senhaPadrao = cpfLimpo; // Senha padrão = CPF
			String senhaCriptografada = passwordEncoder.encode(senhaPadrao);
			Role role = lotacao.getRole(); // Pega a Role da Lotação

			Usuario novoUsuario = new Usuario(cpfLimpo, senhaCriptografada, role);
			novoUsuario.setProfissional(p); // Vincula o usuário ao profissional

			usuarioRepository.save(novoUsuario);
			// --- FIM DA CRIAÇÃO DO USUÁRIO ---

			telaProfissional.mostraMensagem("Profissional (e seu Usuário) cadastrado com sucesso!");

		} catch (Exception e) {
			telaProfissional.mostraMensagem("Erro ao cadastrar: " + e.getMessage());
			// Como o método é @Transactional, se der erro aqui, o Profissional também não será salvo (rollback).
		}
	}

	@Transactional
	private void alterarProfissional() {
		// ... (A lógica de alterar profissional não precisa mudar agora) ...
		// ... (ela já lida com alteração de lotação e validação de dados) ...
		List<Profissional> profissionais = getProfissionais();
		String cpf = telaProfissional.seleciona(profissionais);
		if (cpf == null) return;
		Profissional p = pegaProfissionalPorCpf(cpf);
		if (p != null) {
			List<Lotacao> lotacoesDisponiveis = lotacaoRepository.findAll();
			String[] dados = telaProfissional.pegarDadosAlteracao(p, lotacoesDisponiveis);
			if (dados == null) return;
			String novoTelefone = dados[2];
			String novoCep = dados[3];
			if (!Validador.isTelefoneValido(novoTelefone)) {
				telaProfissional.mostraMensagem("Erro: O Telefone é inválido. (Deve ter 10 ou 11 dígitos)");
				return;
			}
			if (!Validador.isCepValido(novoCep)) {
				telaProfissional.mostraMensagem("Erro: O CEP é inválido. (Deve ter 8 dígitos)");
				return;
			}
			try {
				Lotacao novaLotacao = lotacoesDisponiveis.get(Integer.parseInt(dados[5]));
				p.setNome(dados[1]);
				p.setTelefone(novoTelefone.replaceAll("[^0-9]", ""));
				p.setCep(novoCep.replaceAll("[^0-9]", ""));
				p.setDataNascimento(LocalDate.parse(dados[4], formatter));
				p.setLotacao(novaLotacao);

				// 8. ATUALIZA A ROLE DO USUÁRIO CASO A LOTAÇÃO MUDE
				// (Opcional, mas muito importante)
				Usuario usuario = usuarioRepository.findByUsername(p.getCpf()).orElse(null);
				if (usuario != null && usuario.getRole() != novaLotacao.getRole()) {
					usuario.setRole(novaLotacao.getRole());
					usuarioRepository.save(usuario);
				}
				// --- FIM DA ATUALIZAÇÃO DE ROLE ---

				profissionalRepository.save(p);
				telaProfissional.mostraMensagem("Profissional alterado com sucesso!");
			} catch (Exception e) {
				telaProfissional.mostraMensagem("Erro ao alterar: " + e.getMessage());
			}
		} else {
			telaProfissional.mostraMensagem("Profissional não cadastrado.");
		}
	}

	@Transactional(readOnly = true)
	private void listarProfissionais() {
		List<Profissional> profissionaisComLotacao = profissionalRepository.findAllComLotacao();
		if (profissionaisComLotacao.isEmpty()) {
			telaProfissional.mostraMensagem("Não há profissionais cadastrados.");
			return;
		}
		telaProfissional.mostraLista(profissionaisComLotacao);
	}

	@Transactional
	private void excluirProfissional() {
		List<Profissional> profissionais = getProfissionais();
		String cpf = telaProfissional.seleciona(profissionais);
		if (cpf == null) return;

		Profissional p = pegaProfissionalPorCpf(cpf);
		if (p != null) {

			// 9. EXCLUI O USUÁRIO ASSOCIADO PRIMEIRO
			// Usamos 'ifPresent' para rodar o código só se o usuário for encontrado
			usuarioRepository.findByUsername(p.getCpf()).ifPresent(usuario -> {
				System.out.println("Excluindo usuário associado: " + usuario.getUsername());
				usuarioRepository.delete(usuario);
			});
			// --- FIM DA EXCLUSÃO DO USUÁRIO ---

			profissionalRepository.delete(p);
			telaProfissional.mostraMensagem("Profissional excluído com sucesso!");
		} else {
			telaProfissional.mostraMensagem("Profissional não cadastrado.");
		}
	}

	// ... (método exibirMenu(Usuario usuario) e getProfissionais() continuam iguais) ...
	public void exibirMenu(Usuario usuario) {
		boolean continua = true;
		Role role = usuario.getRole();
		while (continua) {
			int opcao = telaProfissional.exibirMenu(role);
			switch (opcao) {
			case 0:
			case 1:
			case 2:
			case 3:
				if (role == Role.ADMIN) {
					if (opcao == 0) cadastrarProfissional();
					else if (opcao == 1) alterarProfissional();
					else if (opcao == 2) listarProfissionais();
					else if (opcao == 3) excluirProfissional();
				} else {
					telaProfissional.mostraMensagemAcessoNegado();
				}
				break;
			case 4:
				continua = false;
				break;
			default:
				telaProfissional.mostraMensagem("Opção inválida!");
				break;
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Profissional> getProfissionais() {
		return profissionalRepository.findAll();
	}
}
