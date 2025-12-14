package br.com.clinica.repository;

import br.com.clinica.domain.model.Tutor;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository extends JpaRepository<Tutor, String> { // <Entidade, Tipo do @Id (cpf)>
}
