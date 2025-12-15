package br.com.clinica.domain.model;

import java.time.LocalDate;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;

@MappedSuperclass // Corrigido: 'c' minúsculo
public class Pessoa {

	@Id // Marca o CPF como Chave Primária
	private String cpf;

	private String nome;
	private String telefone;
	private String cep;
	private LocalDate dataNascimento;

	// 1. Construtor vazio (obrigatório para JPA)
	public Pessoa() {
	}

	public Pessoa(String cpf, String nome, String telefone, String cep, LocalDate dataNascimento) {
		this.cpf = cpf;
		this.nome = nome;
		this.telefone = telefone;
		this.cep = cep;
		this.dataNascimento = dataNascimento;
	}

	// Getters e Setters (continuam iguais)
	public String getCpf() { return cpf; }
	public void setCpf(String cpf) { this.cpf = cpf; }
	public String getNome() { return nome; }
	public void setNome(String nome) { this.nome = nome; }
	public String getTelefone() { return telefone; }
	public void setTelefone(String telefone) { this.telefone = telefone; }
	public String getCep() { return cep; }
	public void setCep(String cep) { this.cep = cep; }
	public LocalDate getDataNascimento() { return dataNascimento; }
	public void setDataNascimento(LocalDate dataNascimento) { this.dataNascimento = dataNascimento; }
}
