package br.com.clinica.presentation;

import br.com.clinica.domain.model.Lotacao;
import br.com.clinica.domain.model.Role; // 1. IMPORTE ROLE
import org.springframework.stereotype.Component;
import javax.swing.*;
import java.util.List;
import java.util.ArrayList; // 2. IMPORTE ARRAYLIST

@Component
public class TelaLotacao extends TelaCRUD {

	@Override
	protected String getTituloTela() {
		return "Tela de Lotações";
	}

	// 3. MÉTODO ATUALIZADO (recebe Role)
	@Override
	public int exibirMenu(Role role) {
		List<String> opcoesList = new ArrayList<>();

		// 4. LÓGICA DE PERMISSÃO DA TELA (APENAS ADMIN)
		if (role == Role.ADMIN) {
			opcoesList.add("Cadastrar"); // Índice 0
			opcoesList.add("Alterar");   // Índice 1
			opcoesList.add("Listar");    // Índice 2
			opcoesList.add("Excluir");   // Índice 3
		}

		opcoesList.add("Voltar"); // Índice 0 (outros) ou 4 (ADMIN)

		String[] opcoes = opcoesList.toArray(new String[0]);

		int escolha = JOptionPane.showOptionDialog(
				null, "Escolha uma opção", getTituloTela(),
				JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
				null, opcoes, opcoes[0]
		);

		// 5. TRADUZ A ESCOLHA DE VOLTA PARA O PADRÃO (0-4)
		if (escolha == JOptionPane.CLOSED_OPTION) {
			return 4; // Fechar = Voltar
		}

		String opcaoSelecionada = opcoes[escolha];

		switch (opcaoSelecionada) {
		case "Cadastrar": return 0;
		case "Alterar":   return 1;
		case "Listar":    return 2;
		case "Excluir":   return 3;
		case "Voltar":    return 4;
		default:          return -1; // Opção inválida
		}
	}

	// ... (métodos pegarDados, pegarDadosAlteracao, seleciona, mostraLista, mostraMensagem continuam iguais) ...

	public String[] pegarDados() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel("Ocupação:"));
		JTextField ocupacao = new JTextField();
		panel.add(ocupacao);
		panel.add(new JLabel("Salário:"));
		JTextField salario = new JTextField();
		panel.add(salario);
		String[] opcoesTurno = {"MATUTINO", "VESPERTINO", "PLANTAO_NOTURNO"};
		JComboBox<String> comboTurno = new JComboBox<>(opcoesTurno);
		panel.add(new JLabel("Turno:"));
		panel.add(comboTurno);
		JComboBox<Role> comboRole = new JComboBox<>(Role.values());
		panel.add(new JLabel("Nível de Acesso (Role):"));
		panel.add(comboRole);

		int result = JOptionPane.showConfirmDialog(null, panel, getTituloTela(),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			return new String[]{
					ocupacao.getText(),
					salario.getText(),
					comboTurno.getSelectedItem().toString(),
					((Role) comboRole.getSelectedItem()).name()
			};
		} else {
			return null;
		}
	}

	public String[] pegarDadosAlteracao(Lotacao l) {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(new JLabel("Ocupação: (Não pode ser alterada)"));
		JTextField ocupacao = new JTextField(l.getOcupacao());
		ocupacao.setEditable(false);
		panel.add(ocupacao);
		panel.add(new JLabel("Salário:"));
		JTextField salario = new JTextField(String.valueOf(l.getSalario()));
		panel.add(salario);
		String[] opcoesTurno = {"MATUTINO", "VESPERTINO", "PLANTAO_NOTURNO"};
		JComboBox<String> comboTurno = new JComboBox<>(opcoesTurno);
		comboTurno.setSelectedItem(l.getTurno().name());
		panel.add(new JLabel("Turno:"));
		panel.add(comboTurno);
		JComboBox<Role> comboRole = new JComboBox<>(Role.values());
		comboRole.setSelectedItem(l.getRole());
		panel.add(new JLabel("Nível de Acesso (Role):"));
		panel.add(comboRole);

		int result = JOptionPane.showConfirmDialog(null, panel, getTituloTela(),
				JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

		if (result == JOptionPane.OK_OPTION) {
			return new String[]{
					ocupacao.getText(),
					salario.getText(),
					comboTurno.getSelectedItem().toString(),
					((Role) comboRole.getSelectedItem()).name()
			};
		} else {
			return null;
		}
	}

	public String seleciona(List<Lotacao> lotacoes) {
		if (lotacoes.isEmpty()) {
			mostraMensagem("Não existem lotações cadastradas.");
			return null;
		}
		String[] nomes = lotacoes.stream().map(Lotacao::getOcupacao).toArray(String[]::new);
		return (String) JOptionPane.showInputDialog(null, "Selecione a lotação:", getTituloTela(),
				JOptionPane.PLAIN_MESSAGE, null, nomes, nomes[0]);
	}

	public void mostraLista(List<Lotacao> lotacoes) {
		StringBuilder sb = new StringBuilder();
		for (Lotacao l : lotacoes) {
			sb.append("Ocupação: ").append(l.getOcupacao())
					.append(", Salário: ").append(l.getSalario())
					.append(", Turno: ").append(l.getTurno())
					.append(", Acesso: ").append(l.getRole())
					.append("\n");
		}
		JOptionPane.showMessageDialog(null, sb.toString(), getTituloTela(), JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostraMensagem(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
}
