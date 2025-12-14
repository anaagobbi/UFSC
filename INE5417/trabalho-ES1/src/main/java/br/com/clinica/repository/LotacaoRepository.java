package br.com.clinica.repository;

import br.com.clinica.domain.model.Lotacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LotacaoRepository extends JpaRepository<Lotacao, String> { // <Entidade, Tipo do @Id (ocupacao)>
}
