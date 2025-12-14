// Copyright [year] <Copyright Owner>

#ifndef STRUCTURES_ARRAY_LIST_H
#define STRUCTURES_ARRAY_LIST_H

#include <cstdint>


namespace structures {

template<typename T>
class ArrayList {
 public:
    ArrayList();
    explicit ArrayList(std::size_t max_size);
    ~ArrayList();

    void clear();
    void push_back(const T& data);
    void push_front(const T& data);
    void insert(const T& data, std::size_t index);
    void insert_sorted(const T& data);
    T pop(std::size_t index);
    T pop_back();
    T pop_front();
    void remove(const T& data);
    bool full() const;
    bool empty() const;
    bool contains(const T& data) const;
    std::size_t find(const T& data) const;
    std::size_t size() const;
    std::size_t max_size() const;
    T& at(std::size_t index);
    T& operator[](std::size_t index);
    const T& at(std::size_t index) const;
    const T& operator[](std::size_t index) const;
    // descricao do 'operator []' na FAQ da disciplina

 private:
    T* contents;
    std::size_t size_;
    std::size_t max_size_;

    static const auto DEFAULT_MAX = 10u;
};

}  // namespace structures

#endif

//-------------------------------------

template<typename T>
structures::ArrayList<T>::ArrayList() {
    max_size_ = DEFAULT_MAX;
    contents = new T[max_size_];
    size_ = 0;
}

template<typename T>
structures::ArrayList<T>::ArrayList(std::size_t max_size) {
    max_size_ = max_size;
    contents = new T[max_size_];
    size_ = 0;
}

template<typename T>
structures::ArrayList<T>::~ArrayList() {
    delete [] contents;
}

template<typename T>
void structures::ArrayList<T>::clear() {
    size_ = 0;
}

template<typename T>
void structures::ArrayList<T>::push_back(const T& data) {
    insert(data, size());
}

template<typename T>
void structures::ArrayList<T>::push_front(const T& data) {
    insert(data, 0);
}

template<typename T>
void structures::ArrayList<T>::insert(const T& data, std::size_t index) {
    if (full()) {
        throw std::out_of_range("lista cheia");
    } else if (index > size()) {  // se index == size(), insere no fim
        throw std::out_of_range("índice inválido para a inserção");
    }

    // Desloca os elementos para a direita
    for (std::size_t i = size(); i > index; --i) {
        contents[i] = contents[i - 1];
    }
    contents[index] = data;  // Insere o novo valor
    ++size_;
}





template<typename T>
void structures::ArrayList<T>::insert_sorted(const T& data) {
    if (full()) {
        throw std::out_of_range("lista cheia");
    }

    std::size_t i = 0;
    while (i < size() && contents[i] < data) {
        ++i;
    }
    insert(data, i);  // Chama o insert para posicionar o dado na posição correta
}

template<typename T>
T structures::ArrayList<T>::pop(std::size_t index) {
    if (empty()) {
        throw std::out_of_range("lista vazia");
    } else if (index >= size()) {
        throw std::out_of_range("índice inválido para a remoção");
    }
    T aux = contents[index];

    // Desloca os elementos para a esquerda
    for (std::size_t i = index; i < size() - 1; ++i) {
        contents[i] = contents[i + 1];
    }

    --size_;
    return aux;
}


template<typename T>
T structures::ArrayList<T>::pop_back() {
    return pop(size()-1);
}

template<typename T>
T structures::ArrayList<T>::pop_front() {
    return pop(0);
}

template<typename T>
void structures::ArrayList<T>::remove(const T& data) {
    // remove primeira ocorrência de 'data' (pelo conteúdo e não pelo índice)
    // se não encontrar 'data', mantém a lista como estava
    std::size_t index = find(data);
    if (index < size()) {
        pop(index);  // Remove o elemento encontrado
    }
}

template<typename T>
bool structures::ArrayList<T>::empty() const {
    return (size() == 0);
}

template<typename T>
bool structures::ArrayList<T>::full() const {
    return (size() == max_size());
}

template<typename T>
bool structures::ArrayList<T>::contains(const T& data) const {
    std::size_t index = find(data);
    return (index < size());
}

template<typename T>
std::size_t structures::ArrayList<T>::find(const T& data) const {
    // retorna o índice da primeira ocorrência de 'data' na lista, se houver
    // retorna 'size()', se 'data' não fizer parte da lista

    for (std::size_t i = 0; i < size(); ++i) {
        if (contents[i] == data) {
            return i;
        }
    }
    return size();  // Não encontrou o elemento
}



template<typename T>
std::size_t structures::ArrayList<T>::size() const {
    return size_;
}

template<typename T>
std::size_t structures::ArrayList<T>::max_size() const {
    return max_size_;
}

template<typename T>
T& structures::ArrayList<T>::at(std::size_t index) {
    if (empty()) {
        throw std::out_of_range("lista vazia");
    } else if (index >= size()) {
        throw std::out_of_range("índice inválido");
    }
    return contents[index];
}

template<typename T>
T& structures::ArrayList<T>::operator[](std::size_t index) {
    // difere do 'at()' por ser uma alternativa sem verificação de 'index'
    return contents[index];
}

template<typename T>
const T& structures::ArrayList<T>::at(std::size_t index) const {
    const T aux = at(index);
    return aux;
}


template<typename T>
const T& structures::ArrayList<T>::operator[](std::size_t index) const {
    // difere do 'at()' por ser uma alternativa sem verificação de 'index'
    return contents[index];
}
