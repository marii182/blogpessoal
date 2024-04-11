package com.generation.blogpessoal.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Optional;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.generation.blogpessoal.model.Usuario;
import com.generation.blogpessoal.repository.UsuarioRepository;
import com.generation.blogpessoal.service.UsuarioService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS) // testa a classe e os métodos interagem
public class UsuarioControllerTest {
	
	@Autowired
	private TestRestTemplate testRestTemplate; // simula ambiente de testes
	
	@Autowired // chama a classe e a deixa independente
	private UsuarioService usuarioService;
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@BeforeAll // antes de tudo
	void start() {
		
		usuarioRepository.deleteAll(); // apaga o banco de dados
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Root", "root@root.com", "rootroot", " ")); // cadastra um usuario para teste
	}
	
	@Test
	@DisplayName("Cadastrar Um Usuário") // teste o endpoint de criar um usuário
	public void deveCriarUsuario() {
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L,
			"Paulo Antunes", "paulo_antunes@email.com.br", "13465278", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.CREATED, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Não deve permitir duplicação do Usuário")
	public void naoDeveDuplicarUsuario() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L, "Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(new Usuario(0L, 
			"Maria da Silva", "maria_silva@email.com.br", "13465278", "-"));
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate
				.exchange("/usuarios/cadastrar", HttpMethod.POST, corpoRequisicao, Usuario.class);
		
		assertEquals(HttpStatus.BAD_REQUEST, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Atualizar um Usuário")
	public void deveAtualizarUmUsuario() {
		
		Optional<Usuario> usuarioCadastrado = usuarioService.cadastrarUsuario(new Usuario(0L,
			"Juliana Andrews", "juliana_andrews@email.com.br", "juliana123", "-"));
		
		Usuario usuarioUpdate = new Usuario(usuarioCadastrado.get().getId(),
				"Juliana Andrews Ramos", "juliana_ramos@email.com.br", "juliana123", "-");
		
		HttpEntity<Usuario> corpoRequisicao = new HttpEntity<Usuario>(usuarioUpdate); // simula o corpo da requisição (json)
		
		ResponseEntity<Usuario> corpoResposta = testRestTemplate // simula o corpo da resposta para o usuario
				.withBasicAuth("root@root.com", "rootroot")
				.exchange("/usuarios/atualizar", HttpMethod.PUT, corpoRequisicao, Usuario.class); // simula o envio da requisição
		
		assertEquals(HttpStatus.OK, corpoResposta.getStatusCode());
	}
	
	@Test
	@DisplayName("Listar todos os Usuários")
	public void deveMostrarTodosUsuarios() {
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
			"Sabrina Sanches", "sabrina_sanches@email.com.br", "sabrina123", "-"));
		
		usuarioService.cadastrarUsuario(new Usuario(0L,
			"Ricardo Marques", "ricardo_marques@email.com.br", "ricardo123", "-"));
		
		ResponseEntity<String> resposta = testRestTemplate
			.withBasicAuth("root@root.com", "rootroot")
			.exchange("/usuarios/all", HttpMethod.GET, null, String.class);
		
		assertEquals(HttpStatus.OK, resposta.getStatusCode());
	}

}
