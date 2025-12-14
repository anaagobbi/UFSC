// Copyright [2024] <ANA LUIZA SALES GOBBI>
#include <vector>

void retira_veiculo(structures::ArrayQueue<int> *f, int k) {
    for (int i = 0; i < k; i++) {
        if ( i != k - 1) {
            int x = f->dequeue();
            f->enqueue(x);
        } else {
            f->dequeue();
        }
    }
}

void mantenha_veiculo(structures::ArrayQueue<int> *f, int k) {
    int n = f->size();
    int elemento_k = 0;

    // Remove todos os elementos, guarda apenas o k-ésimo
    for (int i = 1; i <= n; ++i) {
        int elem = f->dequeue();
        if (i == k) {
            elemento_k = elem;
        }
    }

    // Reinsere somente o k-ésimo elemento
    f->enqueue(elemento_k);
}

