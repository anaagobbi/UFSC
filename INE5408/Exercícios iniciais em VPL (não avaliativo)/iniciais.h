// Copyright [2022] <ANA LUIZA SALES GOBBI>

/*
(1) o cálculo do maior valor; exemplo:
vetor de entrada: [10, 50, 30, 70, 20]
valor de saída: 70
*/
int maximo(int vet[], int N) {
    int max = vet[0];
    for (int i = 1; i < N; i++) {
        if (vet[i] > max) {
            max = vet[i];
        }
    }
    return max;
}


/*
(2) a posição (ou índice) da primeira ocorrência do maior valor; exemplo:
vetor de entrada: [10, 50, 30, 70, 20]
posição de saída: 3
*/
int posicao(int vet[], int N) {
    int max = vet[0];
    int pos = 0;
    for (int i = 1; i < N; i++) {
        if (vet[i] > max) {
            max = vet[i];
            pos = i;
        }
    }
    return pos;
}


/*
(3) a criação de uma estrutura, denominada maxpos, contendo o valor do maior valor e a posição de sua primeira ocorrência; exemplo:
vetor de entrada: [10, 50, 30, 70, 20]
estrutura de saída: { 70, 3 }
*/
struct maxpos {
    int max;
    int pos;
};

maxpos maximo_posicao(int vet[], int N) {
    maxpos MP;
    MP.max = vet[0];
    MP.pos = 0;
    for (int i = 1; i < N; i++) {
        if (vet[i] > MP.max) {
            MP.max = vet[i];
            MP.pos = i;
        }
    }
    return MP;
}


/*
(4) o cálculo de máximo ponto-a-ponto entre dois vetores vet1 e vet2 de mesmo tamanho N; 
a resposta consiste em atualização dos valores próprio vet1; exemplo:
vetor 1 original:  [10, 50, 30, 70, 20]
vetor 2 original:  [30, 40, 20, 70, 10]
vetor 1 final:     [30, 50, 30, 70, 20]
*/
void maximo_vetores(int vet1[], int vet2[], int N) {
    for (int i = 0; i < N; i++) {
        if (vet2[i] > vet1[i]) {
            vet1[i] = vet2[i];
        }
    }
}


/*
(5) a construção de uma inversão do vetor; exemplo:
vetor original: [A, B, C, D, E]
vetor final:    [E, D, C, B, A]
*/
void inversao(int vet[], int N) {
    for (int i = 0; i < N / 2; i++) {
        int temp = vet[i];
        vet[i] = vet[N - 1 - i];
        vet[N - 1 - i] = temp;
    }
}


/*
    *** Importante ***

    A função 'main()' não deve ser escrita aqui, pois é parte do código dos testes e já está implementada
*/

