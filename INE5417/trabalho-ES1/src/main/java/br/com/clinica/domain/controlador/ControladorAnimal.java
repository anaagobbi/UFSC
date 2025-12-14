package br.com.clinica.domain.controlador;

import br.com.clinica.domain.model.*; // Importe Modelos
import br.com.clinica.repository.AnimalRepository;
import br.com.clinica.repository.TutorRepository;
import br.com.clinica.presentation.TelaAnimal;
import br.com.clinica.util.Validador; // Importe Validador
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Service
public class ControladorAnimal {
	private final TelaAnimal telaAnimal;
	private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	@Autowired
	private AnimalRepository animalRepository;
	@Autowired
	private TutorRepository tutorRepository;

	@Autowired
	public ControladorAnimal(TelaAnimal telaAnimal) {
		this.telaAnimal = telaAnimal;
	}

	@Transactional(readOnly = true)
	private Animal pegaAnimalPorChip(String chip) {
		return animalRepository.findById(chip).orElse(null);
	}

	@Transactional
	private void cadastrarAnimal() {
		List<Tutor> tutoresDisponiveis = tutorRepository.findAll();
		String[] dados = telaAnimal.pegarDados(tutoresDisponiveis);
		if (dados == null) return;
		// (Validação de dados do animal pode ser adicionada aqui se necessário)
		if (pegaAnimalPorChip(dados[0]) == null) {
			try {
				LocalDate dataNascimento = LocalDate.parse(dados[4], formatter);
				int tutorIndex = Integer.parseInt(dados[5]);
				Tutor tutorSelecionado = tutoresDisponiveis.get(tutorIndex);
				Animal animal = new Animal(
						dados[0], dados[1], dados[2],
						Sexo.valueOf(dados[3].toUpperCase()),
						dataNascimento, tutorSelecionado
				);
				animalRepository.save(animal);
				telaAnimal.mostraMensagem("Animal cadastrado com sucesso!");
			} catch (Exception e) {
				telaAnimal.mostraMensagem("Erro ao cadastrar: " + e.getMessage());
			}
		} else {
			telaAnimal.mostraMensagem("Animal já cadastrado!");
		}
	}

	@Transactional
	private void alterarCadastro() {
		List<Animal> animais = getAnimais();
		String chip = telaAnimal.seleciona(animais);
		if (chip == null) return;
		Animal a = pegaAnimalPorChip(chip);
		if (a != null) {
			List<Tutor> tutoresDisponiveis = tutorRepository.findAll();
			String[] novosDados = telaAnimal.pegarDadosAlteracao(a, tutoresDisponiveis);
			if (novosDados == null) return;
			a.setNome(novosDados[1]);
			a.setEspecie(novosDados[2]);
			a.setSexo(Sexo.valueOf(novosDados[3].toUpperCase()));
			try {
				a.setDataNascimento(LocalDate.parse(novosDados[4], formatter));
			} catch(DateTimeParseException e) {
				telaAnimal.mostraMensagem("Data inválida! Alteração não realizada.");
				return;
			}
			int tutorIndex = Integer.parseInt(novosDados[5]);
			Tutor tutorSelecionado = tutoresDisponiveis.get(tutorIndex);
			a.setTutor(tutorSelecionado);
			animalRepository.save(a);
			telaAnimal.mostraMensagem("Cadastro alterado com sucesso!");
		} else {
			telaAnimal.mostraMensagem("Animal não cadastrado.");
		}
	}

	@Transactional(readOnly = true)
	private void listarAnimais() {
		List<Animal> animaisComTutor = animalRepository.findAllComTutor();
		if (animaisComTutor.isEmpty()) {
			telaAnimal.mostraMensagem("Não há animais cadastrados.");
			return;
		}
		telaAnimal.mostraLista(animaisComTutor);
	}

	@Transactional
	private void excluirAnimal() {
		List<Animal> animais = getAnimais();
		String chip = telaAnimal.seleciona(animais);
		if (chip == null) return;
		Animal a = pegaAnimalPorChip(chip);
		if (a != null) {
			animalRepository.delete(a);
			telaAnimal.mostraMensagem("Animal excluído com sucesso!");
		} else {
			telaAnimal.mostraMensagem("Animal não cadastrado.");
		}
	}

	// 1. MÉTODO ATUALIZADO (recebe Usuário)
	public void exibirMenu(Usuario usuario) {
		boolean continua = true;
		Role role = usuario.getRole(); // Pega a Role

		while (continua) {
			// 2. Passa a Role para a tela
			int opcao = telaAnimal.exibirMenu(role);

			// 3. Switch com lógica de permissão
			switch (opcao) {
			case 0: // Cadastrar
				if (role == Role.ADMIN || role == Role.SECRETARIA) {
					cadastrarAnimal();
				} else {
					telaAnimal.mostraMensagemAcessoNegado();
				}
				break;
			case 1: // Alterar
				if (role == Role.ADMIN || role == Role.SECRETARIA) {
					alterarCadastro();
				} else {
					telaAnimal.mostraMensagemAcessoNegado();
				}
				break;
			case 2: // Listar
				// Todos (Admin, Secretaria, Veterinario) podem listar
				listarAnimais();
				break;
			case 3: // Excluir
				if (role == Role.ADMIN || role == Role.SECRETARIA) {
					excluirAnimal();
				} else {
					telaAnimal.mostraMensagemAcessoNegado();
				}
				break;
			case 4: // Voltar
				continua = false;
				break;
			default:
				telaAnimal.mostraMensagem("Opção inválida!");
				break;
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Animal> getAnimais() {
		return animalRepository.findAll();
	}
}
