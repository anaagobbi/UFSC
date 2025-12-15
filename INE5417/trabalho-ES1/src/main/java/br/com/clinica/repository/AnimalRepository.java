package br.com.clinica.repository;

import br.com.clinica.domain.model.Animal;
import br.com.clinica.domain.model.Tutor; // 1. IMPORTE TUTOR
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AnimalRepository extends JpaRepository<Animal, String> {

	@Query("SELECT a FROM Animal a JOIN FETCH a.tutor")
	List<Animal> findAllComTutor();

	// 2. NOVO MÉTODO PARA CONTAR ANIMAIS POR TUTOR
	// Spring Data JPA cria a query: "SELECT count(a) FROM Animal a WHERE a.tutor = ?"
	long countByTutor(Tutor tutor);
}
