package br.com.clinica.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios") // Nome da tabela no banco
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username; // login

	@Column(nullable = false)
	private String password; // senha criptografada

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Role role; // Nível de acesso

	// Relaciona o login a um Profissional (opcional, mas muito útil)
	@OneToOne
	@JoinColumn(name = "profissional_cpf", referencedColumnName = "cpf")
	private Profissional profissional;

	// Construtores
	public Usuario() {}

	public Usuario(String username, String password, Role role) {
		this.username = username;
		this.password = password;
		this.role = role;
	}

	// Getters e Setters
	public Long getId() { return id; }
	public void setId(Long id) { this.id = id; }
	public String getUsername() { return username; }
	public void setUsername(String username) { this.username = username; }
	public String getPassword() { return password; }
	public void setPassword(String password) { this.password = password; }
	public Role getRole() { return role; }
	public void setRole(Role role) { this.role = role; }
	public Profissional getProfissional() { return profissional; }
	public void setProfissional(Profissional profissional) { this.profissional = profissional; }
}
