#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <stdlib.h>
#include <unistd.h>

/* ---------- Definições Globais. ---------- */
#define TEMPO_BASE 1000000

typedef enum { 
	CONTINENTE, 
	ILHA 
} cabeceira_t;

typedef struct {
	int id;
	cabeceira_t cabeceira;
	pthread_t thread;
} veiculo_t;
/* ---------------------------------------- */

/* ---------- Variáveis Globais. ---------- */
char cabeceiras[2][11] = { { "CONTINENTE" }, { "ILHA" } };
int total_veiculos;
int veiculos_turno;

// ToDo: Adicione aque quaisquer outras variávels globais necessárias.
sem_t mutex;
sem_t pode_entrar;
int sentido_atual = CONTINENTE;
int na_ponte = 0;
int esperando[2] = {0, 0};
int passou_turno = 0;
/* ---------------------------------------- */


/* Inicializa a ponte. */
void ponte_inicializar() {
	
	// ToDo: IMPLEMENTAR!
	sem_init(&mutex, 0, 1);
	sem_init(&pode_entrar, 0, 0);
	sentido_atual = CONTINENTE;
	na_ponte = 0;
	esperando[CONTINENTE] = 0;
	esperando[ILHA] = 0;
	passou_turno = 0;

	/* Imprime direção inicial da travessia. NÃO REMOVER! */
	printf("\n[PONTE] *** Novo sentido da travessia: CONTINENTE -> ILHA. ***\n\n");
	fflush(stdout);
}

/* Função executada pelo veículo para ENTRAR em uma cabeceira da ponte. */
void ponte_entrar(veiculo_t *v) {
	
	// ToDo: IMPLEMENTAR!
	sem_wait(&mutex);

	esperando[v->cabeceira]++;

	while (v->cabeceira != sentido_atual || passou_turno >= veiculos_turno) {
		sem_post(&mutex);
		sem_wait(&pode_entrar);
		sem_wait(&mutex);
	}

	esperando[v->cabeceira]--;
	na_ponte++;
	passou_turno++;

	sem_post(&mutex);
}

/* Função executada pelo veículo para SAIR de uma cabeceira da ponte. */
void ponte_sair(veiculo_t *v) {

	// ToDo: IMPLEMENTAR!
	sem_wait(&mutex);

	na_ponte--;

	if (na_ponte == 0) {
		// Troca de sentido após o turno atual
		if (passou_turno >= veiculos_turno) {
			sentido_atual = !sentido_atual;
			passou_turno = 0;

			/* Você deverá imprimir a nova direção da travessia quando for necessário! */	
			printf("\n[PONTE] *** Novo sentido da travessia: %s -> %s. ***\n\n", cabeceiras[v->cabeceira], cabeceiras[!v->cabeceira]);
			fflush(stdout);
		}

		// Libera os próximos veículos do novo sentido
		int liberados = 0;
		while (esperando[sentido_atual] > 0 && liberados < veiculos_turno) {
			sem_post(&pode_entrar);
			liberados++;
		}
	}

	sem_post(&mutex);
}

/* FINALIZA a ponte. */
void ponte_finalizar() {

	// ToDo: IMPLEMENTAR!
	sem_destroy(&mutex);
	sem_destroy(&pode_entrar);

	/* Imprime fim da execução! */
	printf("[PONTE] FIM!\n\n");
	fflush(stdout);
}

/* Implementa o comportamento de um veículo. */
void * veiculo_executa(void *arg) {
	veiculo_t *v = (veiculo_t *) arg;
	
	printf("[Veiculo %3d] Aguardando para entrar na ponte pelo(a) %s.\n", v->id, cabeceiras[v->cabeceira]);
	fflush(stdout);

	/* Entra na ponte. */
	ponte_entrar(v);
	printf("[Veiculo %3d] ENTROU na ponte pelo(a) %s.\n", v->id, cabeceiras[v->cabeceira]);
	fflush(stdout);

	/* Faz a travessia. */
	usleep(TEMPO_BASE + rand() % 1000);
	
	/* Seta cabeceira oposta para sair. */
	v->cabeceira = !v->cabeceira;

	/* Sai da ponte na cabeceira oposta. */
	printf("[Veiculo %3d] SAIU da ponte pelo(a) %s.\n", v->id, cabeceiras[v->cabeceira]);
	fflush(stdout);
	ponte_sair(v);

	pthread_exit(NULL);
}

/* Função principal: NÃO PODE ALTERAR! */
int main(int argc, char **argv) {
 
	if (argc < 3) {
		printf("Indique a quantidade total de veiculos e o numero de veiculos que podem atravessar a ponte por turno:\n\n %s [QTD_TOTAL_VEICULOS] [VEICULOS_POR_TURNO]\n\n", argv[0]);
		return 1;
	}

	total_veiculos = atoi(argv[1]);
	veiculos_turno = atoi(argv[2]);

	if (total_veiculos % (veiculos_turno * 2)) {
		printf("ERRO: O valor [QTD_TOTAL_VEICULOS] deve ser divisivel por ([VEICULOS_POR_TURNO] * 2)!\n\n %s [QTD_TOTAL_VEICULOS] [VEICULOS_POR_TURNO]\n\n", argv[0]);
		return 1;
	}

	srand(time(NULL));

	/* Aloca os veículos. */
	veiculo_t veiculos[total_veiculos];

	ponte_inicializar();

	/* Cria os veículos. */
	for (int i = 0; i < total_veiculos; i++) {
		/* Define o id do veículo. */
		veiculos[i].id = i;
		
		/* Escolhe aleatoreamente se o veículo entra pela ILHA ou CONTINENTE. */
		veiculos[i].cabeceira = i % 2; 
		
		/* Cria a thread veículo. */
		pthread_create(&veiculos[i].thread, NULL, veiculo_executa, (void *) &veiculos[i]);		
	}

	/* Aguarda o término da execução de todos os veículos antes de finalizar o programa. */
	for (int i = 0; i < total_veiculos; i++) {
		pthread_join(veiculos[i].thread, NULL);
	}

	ponte_finalizar();

	return 0;
}
