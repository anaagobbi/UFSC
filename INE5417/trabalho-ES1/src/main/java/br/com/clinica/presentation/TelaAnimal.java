package br.com.clinica.presentation;

import br.com.clinica.domain.model.Animal;
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
public class TelaAnimal extends TelaCRUD {

	@Override
	protected String getTituloTela() {
		return "Tela de Animais";
	}

	// 3. MÉTODO ATUALIZADO (recebe Role)
	@Override
	public int exibirMenu(Role role) {
		// Define as opções base (que todos podem ver)
		List<String> opcoesList = new ArrayList<>();

		// 4. LÓGICA DE PERMISSÃO DA TELA
		if (role == Role.ADMIN || role == Role.SECRETARIA) {
			opcoesList.add("Cadastrar"); // Índice 0
			opcoesList.add("Alterar");   // Índice 1
		}

		// Todos podem Listar
		opcoesList.add("Listar"); // Índice 0 (VET) ou 2 (ADMIN/SEC)

		if (role == Role.ADMIN || role == Role.SECRETARIA) {
			opcoesList.add("Excluir"); // Índice 3
		}

		opcoesList.add("Voltar"); // Índice 1 (VET) ou 4 (ADMIN/SEC)

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

	public String[] pegarDados(List<Tutor> tutores) {
		if (tutores.isEmpty()) {
			mostraMensagem("Não existem tutores cadastrados. Cadastre um tutor antes de cadastrar um animal.");
			return null;
		}
		try {
			MaskFormatter mask = new MaskFormatter("##/##/####");
			mask.setPlaceholderCharacter('_');
			JFormattedTextField campoData = new JFormattedTextField(mask);
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("ID Microchip:"));
			JTextField idMicrochip = new JTextField();
			panel.add(idMicrochip);
			panel.add(new JLabel("Nome:"));
			JTextField nome = new JTextField();
			panel.add(nome);
			panel.add(new JLabel("Espécie:"));
			JTextField especie = new JTextField();
			panel.add(especie);
			String[] opcoesSexo = {"MASCULINO", "FEMININO"};
			JComboBox<String> comboSexo = new JComboBox<>(opcoesSexo);
			panel.add(new JLabel("Sexo:"));
			panel.add(comboSexo);
			panel.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
			panel.add(campoData);
			String[] nomesTutors = tutores.stream().map(Tutor::getNome).toArray(String[]::new);
			JComboBox<String> comboTutor = new JComboBox<>(nomesTutors);
			panel.add(new JLabel("Tutor:"));
			panel.add(comboTutor);

			int result = JOptionPane.showConfirmDialog(null, panel, getTituloTela(),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				int tutorIndex = comboTutor.getSelectedIndex();
				return new String[]{
						idMicrochip.getText(),
						nome.getText(),
						especie.getText(),
						(String) comboSexo.getSelectedItem(),
						campoData.getText(),
						String.valueOf(tutorIndex)
				};
			} else {
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String[] pegarDadosAlteracao(Animal a, List<Tutor> tutores) {
		try {
			MaskFormatter mask = new MaskFormatter("##/##/####");
			mask.setPlaceholderCharacter('_');
			JFormattedTextField campoData = new JFormattedTextField(mask);
			campoData.setText(a.getDataNascimento().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
			JPanel panel = new JPanel();
			panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
			panel.add(new JLabel("ID Microchip:"));
			JTextField idMicrochip = new JTextField(a.getIdMicrochip());
			idMicrochip.setEditable(false);
			panel.add(idMicrochip);
			panel.add(new JLabel("Nome:"));
			JTextField nome = new JTextField(a.getNome());
			panel.add(nome);
			panel.add(new JLabel("Espécie:"));
			JTextField especie = new JTextField(a.getEspecie());
			panel.add(especie);
			panel.add(new JLabel("Sexo:"));
			JComboBox<String> comboSexo = new JComboBox<>(new String[]{"MASCULINO", "FEMININO"});
			comboSexo.setSelectedItem(a.getSexo().name());
			panel.add(comboSexo);
			panel.add(new JLabel("Data de Nascimento (dd/MM/yyyy):"));
			panel.add(campoData);
			String[] nomesTutors = tutores.stream().map(Tutor::getNome).toArray(String[]::new);
			JComboBox<String> comboTutor = new JComboBox<>(nomesTutors);
			comboTutor.setSelectedIndex(tutores.indexOf(a.getTutor()));
			panel.add(new JLabel("Tutor:"));
			panel.add(comboTutor);

			int result = JOptionPane.showConfirmDialog(null, panel, getTituloTela(),
					JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

			if (result == JOptionPane.OK_OPTION) {
				int tutorIndex = comboTutor.getSelectedIndex();
				return new String[]{
						idMicrochip.getText(),
						nome.getText(),
						especie.getText(),
						(String) comboSexo.getSelectedItem(),
						campoData.getText(),
						String.valueOf(tutorIndex)
				};
			} else {
				return null;
			}
		} catch (ParseException e) {
			e.printStackTrace();
			return null;
		}
	}

	public String seleciona(List<Animal> animais) {
		if (animais.isEmpty()) {
			mostraMensagem("Não existem animais cadastrados.");
			return null;
		}
		String[] ids = animais.stream().map(Animal::getIdMicrochip).toArray(String[]::new);
		return (String) JOptionPane.showInputDialog(null, "Selecione o animal pelo Microchip:", getTituloTela(),
				JOptionPane.PLAIN_MESSAGE, null, ids, ids[0]);
	}

	public void mostraLista(List<Animal> animais) {
		StringBuilder sb = new StringBuilder();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
		for (Animal a : animais) {
			sb.append("ID: ").append(a.getIdMicrochip())
					.append(", Nome: ").append(a.getNome())
					.append(", Espécie: ").append(a.getEspecie())
					.append(", Sexo: ").append(a.getSexo())
					.append(", Data Nascimento: ").append(a.getDataNascimento().format(formatter))
					.append(", Tutor: ").append(a.getTutor().getNome())
					.append("\n");
		}
		JOptionPane.showMessageDialog(null, sb.toString(), getTituloTela(), JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostraListaFormatada(String listaDeAnimais) {
		JOptionPane.showMessageDialog(null, listaDeAnimais, getTituloTela(), JOptionPane.INFORMATION_MESSAGE);
	}

	public void mostraMensagem(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
}
