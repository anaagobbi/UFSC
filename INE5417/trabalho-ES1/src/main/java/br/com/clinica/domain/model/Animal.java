package br.com.clinica.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity // 1. Marca como uma tabela do banco
public class Animal {

	@Id // 2. Marca o idMicroChip como Chave Primária
	private String idMicroChip;

	private String nome;
	private String especie;

	@Enumerated(EnumType.STRING) // 3. Salva o Enum como "MASCULINO" ou "FEMININO"
	private Sexo sexo;

	private LocalDate dataNascimento;

	// 4. Mapeia o relacionamento (Muitos Animais pertencem a Um Tutor)
	@ManyToOne(fetch = FetchType.LAZY) // Boa prática: Carrega o tutor só quando getTutor() for chamado
	@JoinColumn(name = "tutor_cpf") // Define o nome da coluna de Chave Estrangeira no banco (que aponta para Pessoa.cpf)
	private Tutor tutor;

	// 5. Construtor vazio (obrigatório para JPA)
	public Animal() {
	}

	public Animal(String idMicroChip, String nome, String especie, Sexo sexo, LocalDate dataNascimento, Tutor tutor) {
		this.idMicroChip = idMicroChip;
		this.nome = nome;
		this.especie = especie;
		this.sexo = sexo;
		this.dataNascimento = dataNascimento;
		this.tutor = tutor;
	}

	// Getters e Setters (continuam iguais)
	public String getIdMicrochip() { return idMicroChip; }
	public void setIdMicroChip(String idMicroChip) { this.idMicroChip = idMicroChip; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getEspecie() { return especie; }
	public void setEspecie(String especie) { this.especie = especie; }
	public Sexo getSexo() { return sexo; }
	public void setSexo(Sexo sexo) { this.sexo = sexo; }
	public LocalDate getDataNascimento() { return dataNascimento; }
	public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
	public Tutor getTutor() { return tutor; }
	public void setTutor(Tutor tutor) { this.tutor = tutor;}
}
