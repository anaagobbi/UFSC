package br.com.clinica.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;

@Entity
public class Profissional extends Pessoa {

	// 1. Relacionamento "Muitos para Um"
	// Muitos profissionais podem ter UMA Lotação
	@ManyToOne(fetch = FetchType.LAZY) // Boa prática
	@JoinColumn(name = "lotacao_ocupacao") // Chave estrangeira no banco
	private Lotacao lotacao;

	// 3. Construtor vazio
	public Profissional() {
		super();
	}

	public Profissional(String cpf, String nome, String telefone, String cep, LocalDate dataNascimento, Lotacao lotacao) {
		super(cpf, nome, telefone, cep, dataNascimento);
		this.lotacao = lotacao;
	}

	// Getters e Setters
	public Lotacao getLotacao() { return lotacao; }
	public void setLotacao(Lotacao lotacao) { this.lotacao = lotacao; }
}
