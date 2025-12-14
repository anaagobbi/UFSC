// Copyright 2025 Ana Luiza Sales Gobbi
// Copyright 2025 Gustavo Monteiro Jorge



#include <iostream>
#include <fstream>
#include <string>

//* ÁRVORE DE PREFIXOS *//

struct NoTrie {
    NoTrie* filhos[26];         // Ponteiros para os filhos (uma letra por índice)
    unsigned long posicao;      // Posição da palavra no arquivo
    unsigned long comprimento;  // Comprimento da palavra no arquivo

    // Construtor do nó
    NoTrie() : posicao(0), comprimento(0) {
        for (int i = 0; i < 26; ++i)
            filhos[i] = nullptr;
    }
};

// Função para inserir uma palavra na Trie
void inserirPalavra(NoTrie* raiz, const std::string& palavra, unsigned long posicao, unsigned long comprimento) {
    NoTrie* atual = raiz;
    for (char c : palavra) {
        if (c < 'a' || c > 'z') {
            std::cerr << "Caractere inválido na palavra: " << c << std::endl;
            return;
        }
        int index = c - 'a';  // Converter caractere para índice (0-25)
        if (!atual->filhos[index]) {
            atual->filhos[index] = new NoTrie();
        }
        atual = atual->filhos[index];
    }
    atual->posicao = posicao;
    atual->comprimento = comprimento;
}

// Função para contar quantas palavras têm um prefixo
int contarPrefixos(NoTrie* no) {
    if (!no) return 0;
    int count = no->comprimento > 0 ? 1 : 0;
    for (int i = 0; i < 26; ++i) {
        if (no->filhos[i]) count += contarPrefixos(no->filhos[i]);
    }
    return count;
}

// Função para verificar uma palavra na Trie
void verificarPalavra(NoTrie* raiz, const std::string& palavra) {
    NoTrie* atual = raiz;
    for (char c : palavra) {
        if (c < 'a' || c > 'z') {
            std::cout << palavra << " is not prefix" << std::endl;
            return;
        }
        int index = c - 'a';
        if (!atual->filhos[index]) {
            std::cout << palavra << " is not prefix" << std::endl;
            return;
        }
        atual = atual->filhos[index];
    }

    // Contar prefixos
    int count = contarPrefixos(atual);
    std::cout << palavra << " is prefix of " << count << " words" << std::endl;

    // Verificar se é uma palavra completa do dicionário
    if (atual->comprimento > 0) {
        std::cout << palavra << " is at (" << atual->posicao << "," << atual->comprimento << ")" << std::endl;
    }
}

// Função para liberar a memória da Trie
void deletarTrie(NoTrie* no) {
    if (!no) return;

    // Percorrer todos os filhos do nó atual
    for (int i = 0; i < 26; ++i) {
        if (no->filhos[i]) {
            deletarTrie(no->filhos[i]);  // Deletar recursivamente os filhos
        }
    }

    // Após liberar todos os filhos, liberar o nó atual
    delete no;
}

//* FUNÇÃO MAIN *//

int main() {
    std::string nomeArquivo;
    std::cin >> nomeArquivo;

    // Abrir arquivo de dicionário
    std::ifstream arquivo(nomeArquivo);
    if (!arquivo.is_open()) {
        std::cerr << "Erro ao abrir arquivo: " << nomeArquivo << std::endl;
        return 1;
    }

    NoTrie* raiz = new NoTrie();  // Inicializa a Trie
    std::string linha;
    unsigned long posicao = 0;   // Posição inicial no arquivo

    // Construir Trie a partir do arquivo de dicionário
   while (std::getline(arquivo, linha)) {
    if (linha.empty() || linha.front() != '[') {
        std::cerr << "Linha inválida no arquivo: " << linha << std::endl;
        continue;
    }

    // Encontrar o colchete de fechamento ']'
    size_t fechamento = linha.find(']');
    if (fechamento == std::string::npos) {
        std::cerr << "Linha inválida no arquivo: " << linha << std::endl;
        continue;
    }

    // Extrair a palavra entre os colchetes
    std::string palavra = linha.substr(1, fechamento - 1);

    // Verificar se a palavra é válida (só contém letras minúsculas)
    if (palavra.empty() || palavra.find_first_not_of("abcdefghijklmnopqrstuvwxyz") != std::string::npos) {
        std::cerr << "Linha inválida no arquivo: " << linha << std::endl;
        continue;
    }

    // Inserir a palavra na Trie
    inserirPalavra(raiz, palavra, posicao, linha.length());
    posicao += linha.length() + 1;  // Inclui o caractere de nova linha
}

    arquivo.close();

    // Processar palavras fornecidas na entrada
    std::string palavra;
    while (std::cin >> palavra && palavra != "0") {
        verificarPalavra(raiz, palavra);
    }

    // Limpar a memória alocada para a Trie
    deletarTrie(raiz);

    return 0;
}