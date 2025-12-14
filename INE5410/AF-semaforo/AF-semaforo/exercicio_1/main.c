#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <time.h>
#include <stdlib.h>

FILE* out;

// Semáforos para controle de ordem entre A e B
sem_t semA, semB;

void *thread_a(void *args) {
    for (int i = 0; i < *(int*)args; ++i) {
        sem_wait(&semA); // espera permissão para imprimir A
        //      +---> arquivo (FILE*) destino
        //      |    +---> string a ser impressa
        //      v    v
        fprintf(out, "A");
        // Importante para que vocês vejam o progresso do programa
        // mesmo que o programa trave em um sem_wait().
        fflush(stdout);
        sem_post(&semB); // libera permissão para imprimir B
    }
    return NULL;
}

void *thread_b(void *args) {
    for (int i = 0; i < *(int*)args; ++i) {
        sem_wait(&semB); // espera permissão para imprimir B
        fprintf(out, "B");
        fflush(stdout);
        sem_post(&semA); // libera permissão para imprimir A
    }
    return NULL;
}

int main(int argc, char** argv) {
    if (argc < 2) {
        printf("Uso: %s [ITERAÇÕES]\n", argv[0]);
        return 1;
    }
    int iters = atoi(argv[1]);
    srand(time(NULL));
    out = fopen("result.txt", "w");

    pthread_t ta, tb;

    // Inicializa os semáforos
    sem_init(&semA, 0, 1); // A começa podendo imprimir
    sem_init(&semB, 0, 0); // B espera

    // Cria threads
    pthread_create(&ta, NULL, thread_a, &iters);
    pthread_create(&tb, NULL, thread_b, &iters);

    // Espera pelas threads
    pthread_join(ta, NULL);
    pthread_join(tb, NULL);

    // Destroi os semáforos
    sem_destroy(&semA);
    sem_destroy(&semB);

    //Imprime quebra de linha e fecha arquivo
    fprintf(out, "\n");
    fclose(out);
  
    return 0;
}
