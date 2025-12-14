package br.com.clinica.repository;

import br.com.clinica.domain.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

	// Spring Data cria a consulta "SELECT * FROM usuarios WHERE username = ?"
	Optional<Usuario> findByUsername(String username);
}
