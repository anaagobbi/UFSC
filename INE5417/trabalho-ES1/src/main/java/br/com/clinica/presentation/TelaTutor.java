package br.com.clinica.presentation;

import br.com.clinica.domain.model.Role; // 1. IMPORTE ROLE
import br.com.clinica.domain.model.Tutor;
import org.springframework.stereotype.Component;
import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.text.ParseException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList; // 2. IMPORTE ARRAYLIST

@Component
public class TelaTutor extends TelaCRUD {

	@Override
	protected String getTituloTela() {
		return "Tela de Tutores";
	}

	// 3. MÉTODO ATUALIZADO (agora recebe a Role)
	@Override
	public int exibirMenu(Role role) {
		// Define as opções base (que todos podem ver)
		List<String> opcoesList = new ArrayList<>();

		// 4. LÓGICA DE PERMISSÃO DA TELA
		if (role == Role.ADMIN || role == Role.SECRETARIA) {
			opcoesList.add("Cadastrar"); // Índice 0
			opcoesList.add("Alterar");   // Índice 1
		}

		opcoesList.add("Listar"); // Índice 0 ou 2

		if (role == Role.ADMIN || role == Role.SECRETARIA) {
			opcoesList.add("Excluir"); // Índice 2 ou 3
		}

		opcoesList.add("Voltar"); // Índice 1, 3 ou 4

		String[] opcoes = opcoesList.toArray(new String[0]);

		int escolha = JOptionPane.showOptionDialog(
				null,
				"Escolha uma opção",
				getTituloTela(),
				JOptionPane.DEFAULT_OPTION,
				JOptionPane.INFORMATION_MESSAGE,
				null,
				opcoes,
				opcoes[0]
		);

		// 5. TRADUZ A ESCOLHA DE VOLTA PARA O PADRÃO (0-4)
		// Isso é crucial para o switch-case do controlador
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

			int result = JOptionPane.showConfirmDialog(null, panel, getTituloTela(),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				return new String[]{
						cpf.getText(),
						nome.getText(),
						telefone.getText(),
						cep.getText(),
						campoData.getText()
				};
			} else {
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] pegarDadosAlteracao(Tutor t) {
		try {
			MaskFormatter mask = new MaskFormatter("##/##/####");
			mask.setPlaceholderCharacter('_');
			JFormattedTextField campoData = new JFormattedTextField(mask);
			campoData.setText(t.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("CPF:"));
			JTextField cpf = new JTextField(t.getCpf());
			cpf.setEditable(false);
			panel.add(cpf);
			panel.add(new JLabel("Nome:"));
			JTextField nome = new JTextField(t.getNome());
			panel.add(nome);
			panel.add(new JLabel("Telefone:"));
			JTextField telefone = new JTextField(t.getTelefone());
			panel.add(telefone);
			panel.add(new JLabel("CEP:"));
			JTextField cep = new JTextField(t.getCep());
			panel.add(cep);
			panel.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
			panel.add(campoData);

			int result = JOptionPane.showConfirmDialog(null, panel, getTituloTela(),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				return new String[]{
						cpf.getText(),
						nome.getText(),
						telefone.getText(),
						cep.getText(),
						campoData.getText()
				};
			} else {
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String seleciona() {
		return JOptionPane.showInputDialog("Digite o CPF do Tutor:");
	}

	public void mostraLista(List<Tutor> tutores) {
		StringBuilder sb = new StringBuilder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		for (Tutor t : tutores) {
			sb.append("CPF: ").append(t.getCpf())
					.append(", Nome: ").append(t.getNome())
					.append(", Telefone: ").append(t.getTelefone())
					.append(", CEP: ").append(t.getCep())
					.append(", Data de Nascimento: ").append(t.getDataNascimento().format(formatter))
					.append("\n");
		}
		JOptionPane.showMessageDialog(null, sb.toString(), getTituloTela(), JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostraMensagem(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
}
