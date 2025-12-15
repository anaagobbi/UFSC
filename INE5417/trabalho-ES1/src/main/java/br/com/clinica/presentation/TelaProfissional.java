package br.com.clinica.presentation;

import br.com.clinica.domain.model.Lotacao;
import br.com.clinica.domain.model.Profissional;
import br.com.clinica.domain.model.Role; // 1. IMPORTE ROLE
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList; // 2. IMPORTE ARRAYLIST

@Component
public class TelaProfissional extends TelaCRUD {

	@Override
	protected String getTituloTela() {
		return "Tela de Profissionais";
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

	// ... (métodos pegarDados, pegarDadosAlteracao, seleciona, mostraListaFormatada, mostraMensagem continuam iguais) ...

	public String[] pegarDados(List<Lotacao> lotacoes) {
		if (lotacoes.isEmpty()) {
			mostraMensagem("Não existem lotações cadastradas. Cadastre um tutor antes de cadastrar um profissional.");
			return null;
		}
		try {
			MaskFormatter mask = new MaskFormatter("##/##/####");
			mask.setPlaceholderCharacter('_');
			JFormattedTextField campoData = new JFormattedTextField(mask);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("CPF:"));
			JTextField cpf = new JTextField();
			panel.add(cpf);
			panel.add(new JLabel("Nome:"));
			JTextField nome = new JTextField();
			panel.add(nome);
			panel.add(new JLabel("Telefone:"));
			JTextField telefone = new JTextField();
			panel.add(telefone);
			panel.add(new JLabel("CEP:"));
			JTextField cep = new JTextField();
			panel.add(cep);
			panel.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
			panel.add(campoData);
			String[] nomesLotacoes = lotacoes.stream().map(Lotacao::getOcupacao).toArray(String[]::new);
			JComboBox<String> comboLotacao = new JComboBox<>(nomesLotacoes);
			panel.add(new JLabel("Lotação:"));
			panel.add(comboLotacao);

			int result = JOptionPane.showConfirmDialog(null, panel, getTituloTela(),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				int lotacaoIndex = comboLotacao.getSelectedIndex();
				return new String[]{
						cpf.getText(),
						nome.getText(),
						telefone.getText(),
						cep.getText(),
						campoData.getText(),
						String.valueOf(lotacaoIndex)
				};
			} else {
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] pegarDadosAlteracao(Profissional p, List<Lotacao> lotacoes) {
		try {
			MaskFormatter mask = new MaskFormatter("##/##/####");
			mask.setPlaceholderCharacter('_');
			JFormattedTextField campoData = new JFormattedTextField(mask);
			campoData.setText(p.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("CPF:"));
			JTextField cpf = new JTextField(p.getCpf());
			cpf.setEditable(false);
			panel.add(cpf);
			panel.add(new JLabel("Nome:"));
			JTextField nome = new JTextField(p.getNome());
			panel.add(nome);
			panel.add(new JLabel("Telefone:"));
			JTextField telefone = new JTextField(p.getTelefone());
			panel.add(telefone);
			panel.add(new JLabel("CEP:"));
			JTextField cep = new JTextField(p.getCep());
			panel.add(cep);
			panel.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
			panel.add(campoData);
			String[] nomesLotacoes = lotacoes.stream().map(Lotacao::getOcupacao).toArray(String[]::new);
			JComboBox<String> comboLotacao = new JComboBox<>(nomesLotacoes);
			comboLotacao.setSelectedIndex(lotacoes.indexOf(p.getLotacao()));
			panel.add(new JLabel("Lotação:"));
			panel.add(comboLotacao);

			int result = JOptionPane.showConfirmDialog(null, panel, getTituloTela(),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				int lotacaoIndex = comboLotacao.getSelectedIndex();
				return new String[]{
						cpf.getText(),
						nome.getText(),
						telefone.getText(),
						cep.getText(),
						campoData.getText(),
						String.valueOf(lotacaoIndex)
				};
			} else {
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String seleciona(List<Profissional> profissionais) {
		if (profissionais.isEmpty()) {
			mostraMensagem("Não existem profissionais cadastrados.");
			return null;
		}
		String[] cpfs = profissionais.stream().map(Profissional::getCpf).toArray(String[]::new);
		return (String) JOptionPane.showInputDialog(null, "Selecione o profissional pelo CPF:", getTituloTela(),
				JOptionPane.PLAIN_MESSAGE, null, cpfs, cpfs[0]);
	}

	public void mostraLista(List<Profissional> profissionais) {
		StringBuilder sb = new StringBuilder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		for (Profissional p : profissionais) {
			sb.append("CPF: ").append(p.getCpf())
					.append(", Nome: ").append(p.getNome())
					.append(", Telefone: ").append(p.getTelefone())
					.append(", CEP: ").append(p.getCep())
					.append(", Data Nascimento: ").append(p.getDataNascimento().format(formatter))
					.append(", Lotação: ").append(p.getLotacao().getOcupacao())
					.append("\n");
		}
		JOptionPane.showMessageDialog(null, sb.toString(), getTituloTela(), JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostraListaFormatada(String listaDeProfissionais) {
		JOptionPane.showMessageDialog(null, listaDeProfissionais, getTituloTela(), JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostraMensagem(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
}
