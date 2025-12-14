// Copyright [2022] <COLOQUE SEU NOME AQUI...>
#include "./array_stack.h"

bool verificaChaves(std::string trecho_programa) {
    bool dentro_string = false;
    int tamanho = trecho_programa.length();
    structures::ArrayStack<char> pilha(500);

    for (int i = 0; i < tamanho; i++) {
        char atual = trecho_programa[i];

        
        if (atual == '\"') {
             
            if (i == 0 || trecho_programa[i - 1] != '\\') {
                dentro_string = !dentro_string;
            }
            continue;
        }

        if (dentro_string) {
            continue;  
        }

        if (atual == '{') {
            pilha.push(atual);
        } else if (atual == '}') {
            if (pilha.empty()) {
                return false;  
            }
            pilha.pop(); 
        }
    }

    return pilha.empty();  
}
