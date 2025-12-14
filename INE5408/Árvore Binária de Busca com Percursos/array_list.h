// Copyright [2025] <Autor>

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
    if (index > size()) {
        throw std::out_of_range("invalid index");
    }
    if (full()) {
        throw std::out_of_range("full list");
    }
    for (std::size_t i = size(); i > index; i--) {
        contents[i] = contents[i-1];
    }
    contents[index] = data;
    size_++;
}

template<typename T>
void structures::ArrayList<T>::insert_sorted(const T& data) {
    std::size_t i;
    for (i = 0; i < size() && contents[i] < data; i++) {
        continue;
    }
    insert(data, i);
}

template<typename T>
T structures::ArrayList<T>::pop(std::size_t index) {
    if (empty()) {
        throw std::out_of_range("empty list");
    } else if (index >= size()) {
        throw std::out_of_range("invalid index");
    }
    T aux = contents[index];
    for (std::size_t i = index+1; i < size(); i++) {
        contents[i-1] = contents[i];
    }
    size_--;
    return aux;
}

template<typename T>
T structures::ArrayList<T>::pop_back() {
    T aux = pop(size()-1);
    return aux;
}

template<typename T>
T structures::ArrayList<T>::pop_front() {
    if (empty()) {
        throw std::out_of_range("empty list");}
    T aux = contents[0];
    for (std::size_t i = 1; i < size(); i++) {
        contents[i-1] = contents[i];
    }
    size_--;
    return aux;
}

template<typename T>
void structures::ArrayList<T>::remove(const T& data) {
    for (std::size_t i = 0; i < size() ; i++) {
        if (contents[i] == data) {
            pop(i);
            break;
        }
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
    for (std::size_t i = 0; i < size() ; i++) {
        if (contents[i] == data) {
            return i;
        }
    }
    return size();
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
        throw std::out_of_range("empty list");
    } else if (index >= size()) {
        throw std::out_of_range("invalid index");
    }
    return contents[index];
}

template<typename T>
T& structures::ArrayList<T>::operator[](std::size_t index) {
    return contents[index];
}

template<typename T>
const T& structures::ArrayList<T>::at(std::size_t index) const {
    const T aux = at(index);
    return aux;
}

template<typename T>
const T& structures::ArrayList<T>::operator[](std::size_t index) const {
    const T aux = at(index);
    return aux;
}
