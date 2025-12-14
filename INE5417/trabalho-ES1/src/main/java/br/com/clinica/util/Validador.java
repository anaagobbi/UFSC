package br.com.clinica.util;

public class Validador {

	/**
	 * Valida um número de CEP (apenas 8 dígitos).
	 * @param cep O CEP como string (pode conter . e -)
	 * @return true se válido, false se inválido
	 */
	public static boolean isCepValido(String cep) {
		if (cep == null) return false;
		// 1. Remove tudo que não for dígito
		String cepLimpo = cep.replaceAll("[^0-9]", "");

		// 2. Verifica se tem 8 dígitos
		return cepLimpo.length() == 8;
		// Para uma validação real, usaríamos uma API de CEP (ex: ViaCEP)
		// Mas para formato, isso basta.
	}

	/**
	 * Valida um número de telefone (10 ou 11 dígitos, com DDD).
	 * @param telefone O telefone como string (pode conter (), - e espaço)
	 * @return true se válido, false se inválido
	 */
	public static boolean isTelefoneValido(String telefone) {
		if (telefone == null) return false;
		// 1. Remove tudo que não for dígito
		String telLimpo = telefone.replaceAll("[^0-9]", "");

		// 2. Verifica se tem 10 (fixo) ou 11 (móvel) dígitos
		return telLimpo.length() == 10 || telLimpo.length() == 11;
	}

	/**
	 * Valida um CPF (formato e dígitos verificadores).
	 * @param cpf O CPF como string (pode conter . e -)
	 * @return true se válido, false se inválido
	 */
	public static boolean isCpfValido(String cpf) {
		if (cpf == null) return false;
		// 1. Remove tudo que não for dígito
		String cpfLimpo = cpf.replaceAll("[^0-9]", "");

		// 2. Verifica se tem 11 dígitos
		if (cpfLimpo.length() != 11) {
			return false;
		}

		// 3. Verifica se todos os dígitos são iguais (ex: 111.111.111-11)
		if (cpfLimpo.matches("(\\d)\\1{10}")) {
			return false;
		}

		// 4. Se passou, calcula os dígitos verificadores
		try {
			// Cálculo do 1º dígito verificador
			int soma = 0;
			for (int i = 0; i < 9; i++) {
				soma += (cpfLimpo.charAt(i) - '0') * (10 - i);
			}
			int digito1 = 11 - (soma % 11);
			if (digito1 > 9) digito1 = 0;

			// Cálculo do 2º dígito verificador
			soma = 0;
			for (int i = 0; i < 10; i++) {
				soma += (cpfLimpo.charAt(i) - '0') * (11 - i);
			}
			int digito2 = 11 - (soma % 11);
			if (digito2 > 9) digito2 = 0;

			// 5. Compara os dígitos calculados com os dígitos reais
			return (digito1 == (cpfLimpo.charAt(9) - '0')) &&
					(digito2 == (cpfLimpo.charAt(10) - '0'));

		} catch (Exception e) {
			return false;
		}
	}
}
