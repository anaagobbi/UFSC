package br.com.clinica.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity // 1. Marca como uma tabela do banco
public class Tutor extends Pessoa {

	// 2. Mapeia o relacionamento (Um Tutor tem Muitos Animais)
	@OneToMany(
			mappedBy = "tutor", // Indica que o lado "Animal" gerencia a relação (com o campo 'tutor')
			cascade = CascadeType.ALL, // Salva/deleta animais junto com o tutor
			fetch = FetchType.LAZY, // Boa prática: Não carrega os animais do banco até que getAnimais() seja chamado
			orphanRemoval = true // Remove animais do banco se forem removidos desta lista
	)
	private List<Animal> animais = new ArrayList<>();

	// 3. Construtor vazio (obrigatório para JPA)
	public Tutor() {
		super();
	}

	public Tutor(String cpf, String nome, String telefone, String cep, LocalDate dataNascimento) {
		super(cpf, nome, telefone, cep, dataNascimento);
	}

	// Getters, Setters e o método adicionarAnimal (continuam iguais)
	public List<Animal> getAnimais() { return animais; }
	public void setAnimais(List<Animal> animais) { this.animais = animais; }
	public void adicionarAnimal(Animal animal) {
		animais.add(animal);
		animal.setTutor(this); // Garante a consistência dos dois lados
	}
}
