//! Copyright [2025] <ANA LUIZA SALES GOBBI>
#ifndef STRUCTURES_LINKED_LIST_H
#define STRUCTURES_LINKED_LIST_H

#include <cstdint>

namespace structures {

/// Classe que representa uma lista ligada simples genérica
/// \tparam T Tipo dos dados armazenados na lista
template<typename T>
class LinkedList {
 public:
    /// Construtor padrão
    LinkedList();

    /// Destrutor
    ~LinkedList();

    /// Remove todos os elementos da lista
    void clear();

    /// Insere um elemento no final da lista
    /// \param data dado a ser inserido
    void push_back(const T& data);

    /// Insere um elemento no início da lista
    /// \param data dado a ser inserido
    void push_front(const T& data);

    /// Insere um elemento na posição indicada
    /// \param data dado a ser inserido
    /// \param index posição da inserção
    void insert(const T& data, std::size_t index);

    /// Insere elemento em ordem crescente (baseado em operador <)
    /// \param data dado a ser inserido
    void insert_sorted(const T& data);

    /// Acessa elemento na posição indicada
    /// \param index posição desejada
    /// \return referência ao dado na posição
    T& at(std::size_t index);

    /// Remove elemento da posição indicada
    /// \param index posição a ser removida
    /// \return dado removido
    T pop(std::size_t index);

    /// Remove elemento do fim da lista
    /// \return dado removido
    T pop_back();

    /// Remove elemento do início da lista
    /// \return dado removido
    T pop_front();

    /// Remove um elemento específico (primeira ocorrência)
    /// \param data dado a ser removido
    void remove(const T& data);

    /// Verifica se a lista está vazia
    /// \return verdadeiro se vazia
    bool empty() const;

    /// Verifica se um elemento está na lista
    /// \param data dado a ser verificado
    /// \return verdadeiro se presente
    bool contains(const T& data) const;

    /// Retorna a posição de um dado (ou size() se não estiver)
    /// \param data dado procurado
    /// \return índice do dado, ou size()
    std::size_t find(const T& data) const;

    /// Retorna o número de elementos na lista
    /// \return tamanho atual
    std::size_t size() const;

 private:
    /// Classe interna para representar um nó da lista
    class Node {
     public:
        /// Construtor com dado
        explicit Node(const T& data);

        /// Construtor com dado e próximo nó
        Node(const T& data, Node* next);

        /// Retorna referência ao dado
        T& data();

        /// Retorna referência constante ao dado
        const T& data() const;

        /// Retorna ponteiro para o próximo nó
        Node* next();

        /// Retorna ponteiro constante para o próximo nó
        const Node* next() const;

        /// Define o próximo nó
        /// \param node ponteiro para novo próximo
        void next(Node* node);

     private:
        T data_;           ///< Dado armazenado
        Node* next_{nullptr};  ///< Próximo nó
    };

    /// Retorna ponteiro para o nó anterior à posição dada
    /// \param index posição desejada
    /// \return ponteiro para nó anterior
    Node* before_index(std::size_t index);

    Node* head{nullptr};       ///< Ponteiro para o primeiro nó
    Node* tail{nullptr};       ///< Ponteiro para o último nó
    std::size_t size_{0u};     ///< Tamanho atual da lista
};

}  // namespace structures

#endif


// ====================== IMPLEMENTAÇÕES ==========================

template<typename T>
structures::LinkedList<T>::LinkedList() {
    head = nullptr;
    tail = nullptr;
    size_ = 0u;
}

template<typename T>
structures::LinkedList<T>::~LinkedList() {
    clear();
}

template<typename T>
void structures::LinkedList<T>::clear() {
    while (!empty()) {
        pop_front();
    }
}

template<typename T>
void structures::LinkedList<T>::push_front(const T& data) {
    Node *novo;
    novo = new Node(data, head);
    if (novo == nullptr) {
        throw std::out_of_range("memória insuficiente");
    }
    if (head == nullptr) {
        tail = novo;
    }
    head = novo;
    size_++;
}

template<typename T>
void structures::LinkedList<T>::push_back(const T& data) {
    Node *novo;
    if (empty()) {
        return push_front(data);
    }
    novo = new Node(data, nullptr);
    if (novo == nullptr) {
        throw std::out_of_range("memória insuficiente");
    }
    tail->next(novo);
    tail = novo;
    size_++;
}

template<typename T>
T& structures::LinkedList<T>::at(std::size_t index) {
    if (index >= size_) {
        throw std::out_of_range("índice inválido");
    }
    Node* atual = head;
    for (std::size_t i = 0; i < index; ++i) {
        atual = atual->next();
    }
    return atual->data();
}

template<typename T>
void structures::LinkedList<T>::insert(const T& data, std::size_t index) {
    if (index > size_) {
        throw std::out_of_range("índice inválido");
    }
    if (index == 0) {
        push_front(data);
    } else if (index == size_) {
        push_back(data);
    } else {
        Node* anterior = before_index(index);
        Node* novo = new Node(data, anterior->next());
        if (novo == nullptr) {
            throw std::out_of_range("memória insuficiente");
        }
        anterior->next(novo);
        size_++;
    }
}

template<typename T>
void structures::LinkedList<T>::insert_sorted(const T& data) {
    Node* atual = head;
    std::size_t index = 0;

    while (atual != nullptr && data > atual->data()) {
        atual = atual->next();
        index++;
    }

    insert(data, index);
}

template<typename T>
T structures::LinkedList<T>::pop_front() {
    if (empty()) {
        throw std::out_of_range("lista vazia");
    }
    T aux;
    Node *p;
    p = head;
    head = p->next();
    aux = p->data();
    delete p;
    if (head == nullptr) {
        tail = nullptr;
    }
    size_--;
    return aux;
}

template<typename T>
T structures::LinkedList<T>::pop_back() {
    if (empty()) {
        throw std::out_of_range("lista vazia");
    }
    if (size() == 1) {
        return pop_front();
    }
    T aux;
    Node *p, *ant;
    ant = nullptr;
    p = head;
    while (p != tail) {
        ant = p;
        p = p->next();
    }
    aux = p->data();
    delete p;
    if (ant == nullptr) {
        head = nullptr;
    } else {
        ant->next(nullptr);
    }
    tail = ant;
    size_--;
    return aux;
}

template<typename T>
T structures::LinkedList<T>::pop(std::size_t index) {
    if (index >= size_) {
        throw std::out_of_range("índice inválido");
    }
    if (index == 0) {
        return pop_front();
    }
    if (index == size_ - 1) {
        return pop_back();
    }

    Node* anterior = before_index(index);
    Node* atual = anterior->next();
    T dado = atual->data();
    anterior->next(atual->next());
    delete atual;
    size_--;
    return dado;
}

template<typename T>
void structures::LinkedList<T>::remove(const T& data) {
    pop(find(data));
}

template<typename T>
bool structures::LinkedList<T>::empty() const {
    return size() == 0u;
}

template<typename T>
bool structures::LinkedList<T>::contains(const T& data) const {
    return find(data) != size();
}

template<typename T>
std::size_t structures::LinkedList<T>::find(const T& data) const {
    Node* atual = head;
    std::size_t index = 0;

    while (atual != nullptr) {
        if (atual->data() == data) {
            return index;
        }
        atual = atual->next();
        index++;
    }

    return size_;
}

template<typename T>
std::size_t structures::LinkedList<T>::size() const {
    return size_;
}

template<typename T>
structures::LinkedList<T>::Node::Node(const T& data):
    data_{data}
{}

template<typename T>
structures::LinkedList<T>::Node::Node(const T& data, Node* next):
    data_{data},
    next_{next}
{}

template<typename T>
T& structures::LinkedList<T>::Node::data() {
    return data_;
}

template<typename T>
const T& structures::LinkedList<T>::Node::data() const {
    return data_;
}

template<typename T>
typename structures::LinkedList<T>::Node*
structures::LinkedList<T>::Node::next() {
    return next_;
}

template<typename T>
const typename structures::LinkedList<T>::Node*
structures::LinkedList<T>::Node::next() const {
    return next_;
}

template<typename T>
void structures::LinkedList<T>::Node::next(Node* node) {
    next_ = node;
}

template<typename T>
typename structures::LinkedList<T>::Node*
structures::LinkedList<T>::before_index(std::size_t index) {
    auto it = head;
    for (auto i = 1u; i < index; ++i) {
        it = it->next();
    }
    return it;
}
