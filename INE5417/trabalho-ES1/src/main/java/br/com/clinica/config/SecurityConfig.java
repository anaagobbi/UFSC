package br.com.clinica.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Este @Bean ensina ao Spring como criptografar e verificar senhas.
	 * Usaremos o BCrypt, que é o padrão moderno.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Como estamos usando Swing e não uma página web, precisamos
	 * desabilitar o formulário de login padrão do Spring Security.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// Desabilita o CSRF (não é necessário para nosso app Swing/API)
				.csrf(csrf -> csrf.disable())
				// Desabilita o formulário de login e logout padrão
				.formLogin(form -> form.disable())
				.httpBasic(httpBasic -> httpBasic.disable())
				.logout(logout -> logout.disable());

		return http.build();
	}
}
