package br.com.clinica.repository;

import br.com.clinica.domain.model.Lotacao; // 1. ADICIONE ESTE IMPORT
import br.com.clinica.domain.model.Profissional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ProfissionalRepository extends JpaRepository<Profissional, String> {

	@Query("SELECT p FROM Profissional p JOIN FETCH p.lotacao")
	List<Profissional> findAllComLotacao();

	// 2. ADICIONE ESTE MÉTODO
	// O Spring Data JPA vai gerar a query automaticamente
	long countByLotacao(Lotacao lotacao);

}
