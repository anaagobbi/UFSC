// Copyright [2024] <ANA LUIZA SALES GOBBI>
#include <iostream>
#include <fstream>
#include <string>
#include <stdexcept>
#include <stack>
#include <queue>

using namespace std;

// Classe responsável por representar o cenário descrito no XML
class Cenario {
  public:
    // Construtor: extrai as informações a partir do texto XML
    Cenario(string& texto, size_t indice_inicial) {
        size_t pos = indice_inicial;
        nome = proxima_tag_conteudo(texto, pos, "nome");
        altura = static_cast<size_t>( stoi( proxima_tag_conteudo(texto, pos, "altura") ) );
        largura = static_cast<size_t>( stoi( proxima_tag_conteudo(texto, pos, "largura") ) );
        x = static_cast<size_t>( stoi( proxima_tag_conteudo(texto, pos, "x") ) );
        y = static_cast<size_t>( stoi( proxima_tag_conteudo(texto, pos, "y") ) );
        matriz = matriz_remove_espacos( proxima_tag_conteudo(texto, pos, "matriz") );
        indice_final = pos;
    }

    ~Cenario() {}

    string nome;
    size_t altura;
    size_t largura;
    size_t x;
    size_t y;
    string matriz;
    size_t indice_final;
    
    // Retorna o valor do caractere na posição (x, y)
    char at(int x, int y) {
        return matriz[x * largura + y];
    }

    // Define o valor do caractere na posição (x, y)
    void assign(int x, int y, char value) {
        matriz[x * largura + y] = value;
    }
    
  private:
    // Localiza e retorna o nome da próxima tag no texto
    string proxima_tag(string& texto, size_t& pos, bool& erro) {
        string tag = "";
        for ( ; pos < texto.length(); pos++) {
            if (texto[pos] == '<') {
                pos++;
                while (texto[pos] != '>') {
                    tag += texto[pos];
                    pos++;
                }
                pos++;
                return tag;
            }
        }
        throw std::runtime_error("Falha ao localizar próxima tag");
        return tag;
    }

    // Recupera o conteúdo entre as tags
    string proximo_conteudo(string& texto, size_t& pos) {
        string txt = "";
        while (texto[pos] != '<') {
            txt += texto[pos];
            pos++;
        }
        while (texto[pos] != '>') {
            pos++;
        }
        pos++;
        return txt;
    }

    // Busca uma tag específica e devolve seu conteúdo
    string proxima_tag_conteudo(string& texto, size_t& pos, string nome_tag) {
        string tag = "";
        bool erro = false;
        while (tag != nome_tag && erro != true) {
            tag = proxima_tag(texto, pos, erro);
        }
        return proximo_conteudo(texto, pos);
    }

    // Remove quaisquer espaços da string da matriz e mantém apenas '0' e '1'
    string matriz_remove_espacos(string texto) {
        string saida;
        for (int i = 0; i < texto.length(); i++) {
            if (texto[i] == '0' || texto[i] == '1') {
                saida += texto[i];
            }
        }
        return saida;
    }
};

// Valida a estrutura de tags XML usando pilha
bool validar(string& texto) {
    std::stack<string> pilhaTags;
    size_t pos = 0;

    while (pos < texto.length()) {
        if (texto[pos] == '<') {
            pos++;
            bool fechamento = (texto[pos] == '/');
            if (fechamento) pos++;

            string tag;
            while (pos < texto.length() && texto[pos] != '>') {
                tag += texto[pos++];
            }
            if (pos < texto.length()) pos++;

            if (!fechamento) {
                pilhaTags.push(tag);
            } else {
                if (pilhaTags.empty() || pilhaTags.top() != tag) {
                    cout << "erro" << endl;
                    return false;
                }
                pilhaTags.pop();
            }
        } else {
            pos++;
        }
    }

    if (!pilhaTags.empty()) {
        cout << "erro" << endl;
        return false;
    }

    return true;
}

// Exibe os dados do cenário no console
void print_cenario(Cenario c1) {
    cout << "nome   : " << c1.nome << endl;
    cout << "altura : " << c1.altura << endl;
    cout << "largura: " << c1.largura << endl;
    cout << "x      : " << c1.x << endl;
    cout << "y      : " << c1.y << endl;
    cout << "matriz : " << c1.matriz << endl << endl;
}

// Executa a contagem da área de 1s conectados a partir da posição (x, y)
size_t processar(Cenario c) {
    if (c.at(c.x, c.y) != '1') { return 0; }

    std::queue<size_t> fila;
    size_t contador = 0;
    fila.push(c.x);
    fila.push(c.y);
    c.assign(c.x, c.y, ' ');
    contador++;

    while (!fila.empty()) {
        size_t i = fila.front();
        fila.pop();
        size_t j = fila.front();
        fila.pop();

        // Verifica vizinho acima
        if (i > 0 && c.at(i - 1, j) == '1') {
            fila.push(i - 1);
            fila.push(j);
            c.assign(i - 1, j, ' ');
            contador++;
        }

        // Verifica vizinho abaixo
        if (i < c.altura - 1 && c.at(i + 1, j) == '1') {
            fila.push(i + 1);
            fila.push(j);
            c.assign(i + 1, j, ' ');
            contador++;
        }

        // Verifica vizinho à esquerda
        if (j > 0 && c.at(i, j - 1) == '1') {
            fila.push(i);
            fila.push(j - 1);
            c.assign(i, j - 1, ' ');
            contador++;
        }

        // Verifica vizinho à direita
        if (j < c.largura - 1 && c.at(i, j + 1) == '1') {
            fila.push(i);
            fila.push(j + 1);
            c.assign(i, j + 1, ' ');
            contador++;
        }
    }
    return contador;
}


/**********************
     FUNÇÃO PRINCIPAL
***********************/
int main() {
    string filename;
    std::cin >> filename;  // Lê o nome do arquivo digitado pelo usuário (ou passado pelo VPL)

    // Tenta abrir o arquivo de entrada
    ifstream filexml(filename);
    if (!filexml.is_open()) {
        cerr << "Erro ao abrir o arquivo " << filename << endl;
        throw runtime_error("Erro ao acessar o arquivo XML");
    }

    // Lê todo o conteúdo do arquivo para uma string
    string texto;
    char character;
    while (filexml.get(character)) {
        texto += character;
    }

    // Verifica a integridade estrutural do XML
    if(!validar(texto)) {
        return 0;
    }
    
    size_t index_file = 0;
    while (index_file < texto.length()) {
        try {
            Cenario c(texto, index_file);
            size_t num_1 = processar(c);
            cout << c.nome << " " << num_1 << endl;
            if (index_file == c.indice_final) break;
            index_file = c.indice_final;   
        } catch(std::runtime_error& e) {
            break;
        }
    }
    return 0;
}
