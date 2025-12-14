package br.com.clinica.domain.model;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;

@Entity // 1. Agora é uma Entidade (tabela)
public class Lotacao {

	@Id // 2. A ocupação será a Chave Primária
	private String ocupacao;

	@Enumerated(EnumType.STRING)
	private Turno turno;

	private int salario;

	// --- NOVO CAMPO ---
	@Enumerated(EnumType.STRING)
	private Role role; // Nível de acesso do cargo
	// --- FIM NOVO CAMPO ---

	// 3. Construtor vazio (obrigatório para JPA)
	public Lotacao() {}

	public Lotacao(String ocupacao, Turno turno, int salario, Role role) {
		this.ocupacao = ocupacao;
		this.turno = turno;
		this.salario = salario;
		this.role = role;
	}

	// Getters e Setters
	public String getOcupacao() { return ocupacao; }
	public void setOcupacao(String ocupacao) { this.ocupacao = ocupacao; }
	public Turno getTurno() { return turno; }
	public void setTurno(Turno turno) { this.turno = turno; }
	public int getSalario() { return salario;}
	public void setSalario(int salario) { this.salario = salario; }
	public Role getRole() { return role; }
	public void setRole(Role role) { this.role = role; }
}
