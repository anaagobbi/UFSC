package br.com.clinica.presentation;

import org.springframework.stereotype.Component;
import javax.swing.*;
import java.awt.*;

@Component
public class TelaLogin {

	/**
	 * Exibe um diálogo modal para pegar usuário e senha.
	 * @return Um array de String: {username, password}. Retorna null se o usuário cancelar.
	 */
	public String[] pegarCredenciais() {
		JPanel panel = new JPanel(new BorderLayout(5, 5));
		panel.setPreferredSize(new Dimension(300, 100)); // Tamanho da janela

		// Painel para os campos
		JPanel fieldsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
		fieldsPanel.add(new JLabel("Usuário:"));
		JTextField usernameField = new JTextField();
		fieldsPanel.add(usernameField);

		fieldsPanel.add(new JLabel("Senha:"));
		JPasswordField passwordField = new JPasswordField();
		fieldsPanel.add(passwordField);

		panel.add(fieldsPanel, BorderLayout.CENTER);

		// Define o foco inicial no campo de usuário
		// Usamos SwingUtilities.invokeLater para garantir que o campo exista antes de focar
		SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());

		int result = JOptionPane.showConfirmDialog(
				null,
				panel,
				"Login - Clínica Veterinária",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE
		);

		if (result == JOptionPane.OK_OPTION) {
			String username = usernameField.getText();
			String password = new String(passwordField.getPassword());
			return new String[]{username, password};
		} else {
			return null; // Usuário cancelou ou fechou a janela
		}
	}

	/**
	 * Mostra uma mensagem de erro de login.
	 */
	public void mostraMensagemErro(String mensagem) {
		JOptionPane.showMessageDialog(
				null,
				mensagem,
				"Erro de Login",
				JOptionPane.ERROR_MESSAGE
		);
	}
}
