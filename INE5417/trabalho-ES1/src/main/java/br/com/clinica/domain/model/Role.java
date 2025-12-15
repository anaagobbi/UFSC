package br.com.clinica.domain.model;

public enum Role {
	ADMIN,      // Acesso total (será o admin padrão)
	SECRETARIA, // Cadastra Tutor, Animal, Profissional
	VETERINARIO // Acesso limitado (ex: só listar)
}
