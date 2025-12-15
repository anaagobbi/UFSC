package br.com.clinica.domain.controlador;

import br.com.clinica.domain.model.Lotacao;
import br.com.clinica.domain.model.Role;
import br.com.clinica.domain.model.Turno;
import br.com.clinica.domain.model.Usuario;
import br.com.clinica.repository.LotacaoRepository;
import br.com.clinica.repository.ProfissionalRepository; // 1. IMPORTE O REPOSITÓRIO PROFISSIONAL
import br.com.clinica.presentation.TelaLotacao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ControladorLotacao {
	private final TelaLotacao telaLotacao;

	@Autowired
	private LotacaoRepository lotacaoRepository;

	@Autowired // 2. INJETE O REPOSITÓRIO PROFISSIONAL
	private ProfissionalRepository profissionalRepository;

	@Autowired
	public ControladorLotacao(TelaLotacao telaLotacao) {
		this.telaLotacao = telaLotacao;
	}

	@Transactional(readOnly = true)
	public Lotacao pegaLotacaoPorOcupacao(String ocupacao) {
		return lotacaoRepository.findById(ocupacao).orElse(null);
	}

	// ... (métodos cadastrar, alterar, listar continuam iguais) ...
	@Transactional
	private void cadastrarLotacao() {
		String[] dados = telaLotacao.pegarDados();
		if (dados == null) return;
		if (pegaLotacaoPorOcupacao(dados[0]) == null) {
			try {
				int salario = Integer.parseInt(dados[1]);
				Turno turno = Turno.valueOf(dados[2]);
				Role role = Role.valueOf(dados[3]);
				Lotacao l = new Lotacao(dados[0], turno, salario, role);
				lotacaoRepository.save(l);
				telaLotacao.mostraMensagem("Lotação cadastrada com sucesso!");
			} catch (NumberFormatException e) {
				telaLotacao.mostraMensagem("Erro: Salário deve ser um número.");
			} catch (Exception e) {
				telaLotacao.mostraMensagem("Erro ao cadastrar: " + e.getMessage());
			}
		} else {
			telaLotacao.mostraMensagem("Lotação já cadastrada!");
		}
	}

	@Transactional
	private void alterarLotacao() {
		listarLotacoes();
		String ocupacao = telaLotacao.seleciona(getLotacoes());
		if (ocupacao == null) return;
		Lotacao l = pegaLotacaoPorOcupacao(ocupacao);
		if (l != null) {
			String[] novosDados = telaLotacao.pegarDadosAlteracao(l);
			if (novosDados == null) return;
			try {
				l.setSalario(Integer.parseInt(novosDados[1]));
				l.setTurno(Turno.valueOf(novosDados[2]));
				l.setRole(Role.valueOf(novosDados[3]));
				lotacaoRepository.save(l);
				telaLotacao.mostraMensagem("Lotação alterada com sucesso!");
			} catch (NumberFormatException e) {
				telaLotacao.mostraMensagem("Erro: Salário deve ser um número.");
			} catch (Exception e) {
				telaLotacao.mostraMensagem("Erro ao alterar: " + e.getMessage());
			}
		}
	}

	@Transactional(readOnly = true)
	private void listarLotacoes() {
		telaLotacao.mostraLista(lotacaoRepository.findAll());
	}


	@Transactional
	private void excluirLotacao() {
		listarLotacoes();
		String ocupacao = telaLotacao.seleciona(getLotacoes());
		if (ocupacao == null) return;

		Lotacao l = pegaLotacaoPorOcupacao(ocupacao);
		if (l != null) {

			// --- 3. NOVA VERIFICAÇÃO ---
			long countProfissionais = profissionalRepository.countByLotacao(l);
			if (countProfissionais > 0) {
				telaLotacao.mostraMensagem("Erro: Não é possível excluir esta lotação.\n" +
						countProfissionais + " profissional(is) ainda está(ão) alocado(s) nela.");
				return; // Impede a exclusão
			}
			// --- FIM DA VERIFICAÇÃO ---

			// Se passou na verificação, tenta excluir
			try {
				lotacaoRepository.delete(l);
				telaLotacao.mostraMensagem("Lotação excluída com sucesso!");
			} catch (Exception e) {
				// Embora a verificação acima deva pegar a maioria dos casos,
				// é bom manter um catch genérico para outros possíveis erros de FK.
				telaLotacao.mostraMensagem("Erro ao excluir lotação. Verifique se ela não está em uso.");
				e.printStackTrace(); // Ajuda a debugar
			}
		} else {
			telaLotacao.mostraMensagem("Lotação não encontrada.");
		}
	}

	// ... (método exibirMenu e getLotacoes continuam iguais) ...
	public void exibirMenu(Usuario usuario) {
		boolean continua = true;
		Role role = usuario.getRole();
		while (continua) {
			int opcao = telaLotacao.exibirMenu(role);
			switch (opcao) {
			case 0:
			case 1:
			case 2:
			case 3:
				if (role == Role.ADMIN) {
					if (opcao == 0) cadastrarLotacao();
					else if (opcao == 1) alterarLotacao();
					else if (opcao == 2) listarLotacoes();
					else if (opcao == 3) excluirLotacao();
				} else {
					telaLotacao.mostraMensagemAcessoNegado();
				}
				break;
			case 4:
				continua = false;
				break;
			default:
				telaLotacao.mostraMensagem("Opção inválida!");
				break;
			}
		}
	}

	@Transactional(readOnly = true)
	public List<Lotacao> getLotacoes() {
		return lotacaoRepository.findAll();
	}
}
