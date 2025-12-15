#include <stdio.h>
#include <stdlib.h>
#include <sys/time.h>
#include <pthread.h>
#include <unistd.h>

// Funcao de ordenacao fornecida. Não pode alterar.
void bubble_sort(int *v, int tam){
    int i, j, temp, trocou;

    for(j = 0; j < tam - 1; j++){
        trocou = 0;
        for(i = 0; i < tam - 1; i++){
            if(v[i + 1] < v[i]){
                temp = v[i];
                v[i] = v[i + 1];
                v[i + 1] = temp;
                trocou = 1;
            }
        }
        if(!trocou) break;
    }
}

// Funcao para imprimir um vetor. Não pode alterar.
void imprime_vet(unsigned int *v, int tam) {
    int i;
    for(i = 0; i < tam; i++)
        printf("%d ", v[i]);
    printf("\n");
}

// Funcao para ler os dados de um arquivo e armazenar em um vetor em memoroa. Não pode alterar.
int le_vet(char *nome_arquivo, unsigned int *v, int tam) {
    FILE *arquivo;
    
    // Abre o arquivo
    arquivo = fopen(nome_arquivo, "r");
    if (arquivo == NULL) {
        perror("Erro ao abrir o arquivo");
        return 0;
    }

    // Lê os números
    for (int i = 0; i < tam; i++)
        fscanf(arquivo, "%u", &v[i]);

    fclose(arquivo);

    return 1;
}

// essa struct define uma tarefa (seus dados, tamanho e id)
typedef struct {
    int *dados;
    int tamanho;
    int tarefa_id;
} Tarefa;

// essas são variáveis globais, acessadas pelas múltiplas threads
Tarefa *tarefas; // vetor de tarefas
int tarefas_pendentes; // total de tarefas a processar
int proxima_tarefa = 0; // índice da próxima tarefa a ser atribuída
pthread_mutex_t mutex = PTHREAD_MUTEX_INITIALIZER; // inicializa o mutex

// função executada pelas threads (chamada na etapa 2 ao criar as threads)
void *thread_func(void *arg) {
    int thread_id = *(int *)arg;
    while (1) {
        // mutex protege a região crítica que acessa e incrementa proxima_tarefa
        // isso faz com que cada tarefa seja atribuída a APENAS uma thread (obviamente)
        pthread_mutex_lock(&mutex);
        // checa se todas as tarefas foram processadas
        if (proxima_tarefa >= tarefas_pendentes) {
            pthread_mutex_unlock(&mutex);
            break;
        }
        int idx = proxima_tarefa++;
        pthread_mutex_unlock(&mutex);

        // parte obrigatória da saída, imprime que thread está fazendo que tarefa
        printf("Thread %d processando tarefa %d\n", thread_id, tarefas[idx].tarefa_id);

        // esse sleep foi usado (e pode ser usado) para testes em vetores muito pequenos
        // em vetores muito pequenos, o processo é tão rápido que a concorrência nem ocorre (apenas a Thread 0 roda)
        // usar o sleep torna as tasks artificialmente mais demoradas e permite que ocorra concorrência (para fins de testes)
        // não é necessário em vetores maiores que 1000
        // usleep(10000);

        // invoca o bubble_sort usando os dados e o tamanho da tarefa atribuída como variáveis
        bubble_sort(tarefas[idx].dados, tarefas[idx].tamanho);
    }
    return NULL;
}

int sort_paralelo(unsigned int *vetor, unsigned int tam, unsigned int ntasks, unsigned int nthreads) {
    // Retorna 0 caso haja mais tarefas que números no vetor ou caso nthreads seja 0 ou negativo
    if (nthreads < 1 || ntasks > tam) return 0;

    // Etapa 1: Criação das tarefas

    // definição de intervalo e resto para tarefas
    int intervalo = tam / ntasks;
    int resto = tam % ntasks;

    // alocação de buckets temporários
    tarefas = malloc(sizeof(Tarefa) * ntasks); // define tamanho do vetor baseado no tamanho ocupado pela struct
    int *temp_buckets[ntasks];                 // vetor de ponteiros para os dados de cada tarefa
    int counts[ntasks];                        // guarda quantos elementos cada tarefa possui

    // inicialização dos buckets
    for (int i = 0; i < ntasks; i++) {
        counts[i] = 0;
        temp_buckets[i] = malloc(sizeof(int) * tam); // aloca espaço mais do que suficiente por garantia
    }

    // distribuição dos números nos buckets
    for (unsigned int i = 0; i < tam; i++) {
        unsigned int valor = vetor[i];
        int min, max;
        for (int j = 0; j < ntasks; j++) {
            if (j < resto) {
                // tarefas que ganham um extra (até completar o resto)
                min = j * (intervalo + 1);
                max = min + (intervalo + 1);
            } else {
                // tarefas restantes (sem resto)
                min = j * intervalo + resto;
                max = min + intervalo;
            }
            if (valor >= min && valor < max) {
                // valor é colocado dentro do bucket se respeitar o intervalo
                temp_buckets[j][counts[j]++] = valor;
                break;
            }
        }
    }

    // cria as tarefas reais
    tarefas_pendentes = 0;
    for (int i = 0; i < ntasks; i++) {
        if (counts[i] > 0) { // essa condição elimina os buckets vazios
            tarefas[tarefas_pendentes].dados = malloc(sizeof(int) * counts[i]);
            tarefas[tarefas_pendentes].tamanho = counts[i];
            tarefas[tarefas_pendentes].tarefa_id = i;
            for (int j = 0; j < counts[i]; j++) {
                tarefas[tarefas_pendentes].dados[j] = temp_buckets[i][j];
            }
            tarefas_pendentes++; // só é incrementado quando há números (counts) no bucket
        }
        // libera-se a memória dos buckets temporários
        free(temp_buckets[i]);
    }

    // Etapa 2: Execução paralela

    // cria vetor de threads e vetor de ids
    // o id é usado como variável pela thread_func
    pthread_t threads[nthreads];
    int thread_ids[nthreads];
    // criam-se as threads
    for (int i = 0; i < nthreads; i++) {
        thread_ids[i] = i;
        pthread_create(&threads[i], NULL, thread_func, &thread_ids[i]);
    }
    // o programa espera pela finalização de todas
    for (int i = 0; i < nthreads; i++) {
        pthread_join(threads[i], NULL);
    }

    // Etapa 3: Concatenação dos subvetores ordenados

    // inicializa posição de escrita no vetor final
    int pos = 0;
    // percorre as tarefas reais
    for (int j = 0; j < tarefas_pendentes; j++) {
        // percorre os números ordenados dentro das tarefas e adiciona no vetor
        for (int k = 0; k < tarefas[j].tamanho; k++) {
            vetor[pos++] = tarefas[j].dados[k];
        }
        // libera a memória dos dados das tarefas
        free(tarefas[j].dados);
    }

    // libera o espaço alocado pelo vetor
    free(tarefas);
    return 1;
}

// Funcao principal do programa. Não pode alterar.
int main(int argc, char **argv) {
    
    // Verifica argumentos de entrada
    if (argc != 5) {
        fprintf(stderr, "Uso: %s <input> <nnumbers> <ntasks> <nthreads>\n", argv[0]);
        return 1;
    }

    // Argumentos de entrada
    unsigned int nnumbers = atoi(argv[2]);
    unsigned int ntasks = atoi(argv[3]);
    unsigned int nthreads = atoi(argv[4]);
    
    // Aloca vetor
    unsigned int *vetor = malloc(nnumbers * sizeof(unsigned int));

    // Variaveis de controle de tempo de ordenacao
    struct timeval inicio, fim;

    // Le os numeros do arquivo de entrada
    if (le_vet(argv[1], vetor, nnumbers) == 0)
        return 1;

    // Imprime vetor desordenado
    imprime_vet(vetor, nnumbers);

    // Ordena os numeros considerando ntasks e nthreads
    gettimeofday(&inicio, NULL);
    sort_paralelo(vetor, nnumbers, ntasks, nthreads);
    gettimeofday(&fim, NULL);

    // Imprime vetor ordenado
    imprime_vet(vetor, nnumbers);

    // Desaloca vetor
    free(vetor);

    // Imprime o tempo de ordenacao
    printf("Tempo: %.6f segundos\n", fim.tv_sec - inicio.tv_sec + (fim.tv_usec - inicio.tv_usec) / 1e6);
    
    return 0;
}