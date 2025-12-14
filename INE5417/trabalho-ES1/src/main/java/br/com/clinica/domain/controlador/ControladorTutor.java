package br.com.clinica.domain.controlador;

import br.com.clinica.domain.model.Role;
import br.com.clinica.domain.model.Tutor;
import br.com.clinica.domain.model.Usuario;
import br.com.clinica.repository.AnimalRepository; // 1. IMPORTE ANIMAL REPOSITORY
import br.com.clinica.repository.TutorRepository;
import br.com.clinica.presentation.TelaTutor;
import br.com.clinica.util.Validador;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ControladorTutor {
	private final TelaTutor telaTutor;

	@Autowired
	private TutorRepository tutorRepository;

	@Autowired // 2. INJETE O ANIMAL REPOSITORY
	private AnimalRepository animalRepository;

	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Autowired
	public ControladorTutor(TelaTutor telaTutor) {
		this.telaTutor = telaTutor;
	}

	// ... (métodos getTutores, pegaTutorPorCpf, cadastrar, alterar, listar continuam iguais) ...
	@Transactional(readOnly = true)
	public List<Tutor> getTutores() {
		return tutorRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Tutor pegaTutorPorCpf(String cpf) {
		return tutorRepository.findById(cpf.replaceAll("[^0-9]", "")).orElse(null);
	}

	@Transactional
	private void cadastrarTutor() {
		String[] dados = telaTutor.pegarDados();
		if (dados == null) return;
		String cpf = dados[0];
		String nome = dados[1];
		String telefone = dados[2];
		String cep = dados[3];
		String dataNascStr = dados[4];

		if (!Validador.isCpfValido(cpf)) {
			telaTutor.mostraMensagem("Erro: O CPF digitado é inválido!");
			return;
		}
		if (!Validador.isTelefoneValido(telefone)) {
			telaTutor.mostraMensagem("Erro: O Telefone é inválido. (Deve ter 10 ou 11 dígitos, incluindo DDD)");
			return;
		}
		if (!Validador.isCepValido(cep)) {
			telaTutor.mostraMensagem("Erro: O CEP é inválido. (Deve ter 8 dígitos)");
			return;
		}
		String cpfLimpo = cpf.replaceAll("[^0-9]", "");
		String telLimpo = telefone.replaceAll("[^0-9]", "");
		String cepLimpo = cep.replaceAll("[^0-9]", "");

		if (pegaTutorPorCpf(cpfLimpo) == null) {
			try {
				LocalDate dataNascimento = LocalDate.parse(dataNascStr, formatter);
				Tutor tutor = new Tutor(cpfLimpo, nome, telLimpo, cepLimpo, dataNascimento);
				tutorRepository.save(tutor);
				telaTutor.mostraMensagem("Tutor cadastrado com sucesso!");
			} catch (DateTimeParseException e) {
				telaTutor.mostraMensagem("Data inválida! Use o formato dd/MM/yyyy.");
			}
		} else {
			telaTutor.mostraMensagem("ATENÇÃO: Tutor já cadastrado!");
		}
	}

	@Transactional
	private void alterarCadastro() {
		listarTutores();
		String cpf = telaTutor.seleciona();
		if (cpf == null) return;
		Tutor t = pegaTutorPorCpf(cpf);
		if (t != null) {
			String[] novosDados = telaTutor.pegarDadosAlteracao(t);
			if (novosDados == null) return;
			String novoTelefone = novosDados[2];
			String novoCep = novosDados[3];
			if (!Validador.isTelefoneValido(novoTelefone)) {
				telaTutor.mostraMensagem("Erro: O Telefone é inválido. (Deve ter 10 ou 11 dígitos)");
				return;
			}
			if (!Validador.isCepValido(novoCep)) {
				telaTutor.mostraMensagem("Erro: O CEP é inválido. (Deve ter 8 dígitos)");
				return;
			}
			t.setNome(novosDados[1]);
			t.setTelefone(novoTelefone.replaceAll("[^0-9]", ""));
			t.setCep(novoCep.replaceAll("[^0-9]", ""));
			try {
				LocalDate dataNascimento = LocalDate.parse(novosDados[4], formatter);
				t.setDataNascimento(dataNascimento);
			} catch (DateTimeParseException e) {
				telaTutor.mostraMensagem("Data inválida! Alteração não realizada.");
				return;
			}
			tutorRepository.save(t);
			telaTutor.mostraMensagem("Cadastro alterado com sucesso!");
		} else {
			telaTutor.mostraMensagem("ATENÇÃO: Tutor não cadastrado.");
		}
	}

	@Transactional(readOnly = true)
	private void listarTutores() {
		telaTutor.mostraLista(tutorRepository.findAll());
	}


	@Transactional
	private void excluirTutor() {
		listarTutores();
		String cpf = telaTutor.seleciona();
		if (cpf == null) return;

		Tutor t = pegaTutorPorCpf(cpf);
		if (t != null) {

			// --- 3. VERIFICAÇÃO ALTERADA ---
			// Usa o repositório para contar os animais associados a este tutor
			long countAnimais = animalRepository.countByTutor(t);
			if (countAnimais > 0) {
				// --- FIM DA ALTERAÇÃO ---
				telaTutor.mostraMensagem("Erro: Não é possível excluir este tutor.\n" +
						countAnimais + " animal(is) ainda está(ão) associado(s) a ele.");
				return; // Impede a exclusão
			}

			// Se passou na verificação, tenta excluir
			try {
				tutorRepository.delete(t);
				telaTutor.mostraMensagem("Tutor excluído com sucesso!");
			} catch (Exception e) {
				telaTutor.mostraMensagem("Erro ao excluir tutor.");
				e.printStackTrace();
			}
		} else {
			telaTutor.mostraMensagem("ATENÇÃO: Tutor não cadastrado.");
		}
	}

	// ... (método exibirMenu continua igual) ...
	public void exibirMenu(Usuario usuario) {
		boolean continua = true;
		Role role = usuario.getRole();
		while (continua) {
			int opcao = telaTutor.exibirMenu(role);
			switch (opcao) {
			case 0: // Cadastrar
				if (role == Role.ADMIN || role == Role.SECRETARIA) {
					cadastrarTutor();
				} else {
					telaTutor.mostraMensagemAcessoNegado();
				}
				break;
			case 1: // Alterar
				if (role == Role.ADMIN || role == Role.SECRETARIA) {
					alterarCadastro();
				} else {
					telaTutor.mostraMensagemAcessoNegado();
				}
				break;
			case 2: // Listar
				listarTutores();
				break;
			case 3: // Excluir
				if (role == Role.ADMIN || role == Role.SECRETARIA) {
					excluirTutor();
				} else {
					telaTutor.mostraMensagemAcessoNegado();
				}
				break;
			case 4: // Voltar
				continua = false;
				break;
			default:
				telaTutor.mostraMensagem("Opção inválida!");
				break;
			}
		}
	}
}
