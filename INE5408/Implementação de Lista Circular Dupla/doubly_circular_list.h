#ifndef STRUCTURES_DOUBLY_CIRCULAR_LIST_H
#define STRUCTURES_DOUBLY_CIRCULAR_LIST_H

#include <stdexcept>

namespace structures {

template<typename T>
class DoublyCircularList {
 public:
    DoublyCircularList();
    ~DoublyCircularList();

    void clear();
    void push_back(const T& data);
    void push_front(const T& data);
    void insert(const T& data, std::size_t index);
    void insert_sorted(const T& data);
    T pop(std::size_t index);
    T pop_back();
    T pop_front();
    void remove(const T& data);
    bool empty() const;
    bool contains(const T& data) const;
    T& at(std::size_t index);
    const T& at(std::size_t index) const;
    std::size_t find(const T& data) const;
    std::size_t size() const;

 private:
    class Node {
     public:
        explicit Node(const T& data) : data_{data}, prev_{nullptr}, next_{nullptr} {}
        Node(const T& data, Node* prev, Node* next) : data_{data}, prev_{prev}, next_{next} {}

        T& data() { return data_; }
        const T& data() const { return data_; }

        Node* prev() { return prev_; }
        const Node* prev() const { return prev_; }
        void prev(Node* node) { prev_ = node; }

        Node* next() { return next_; }
        const Node* next() const { return next_; }
        void next(Node* node) { next_ = node; }

     private:
        T data_;
        Node* prev_;
        Node* next_;
    };

    Node* head;
    std::size_t size_;

    Node* node_at(std::size_t index) const {
        Node* current = head;
        for (std::size_t i = 0; i < index; ++i)
            current = current->next();
        return current;
    }
};

// === IMPLEMENTAÇÃO ===

template<typename T>
DoublyCircularList<T>::DoublyCircularList() : head{nullptr}, size_{0} {}

template<typename T>
DoublyCircularList<T>::~DoublyCircularList() {
    clear();
}

template<typename T>
void DoublyCircularList<T>::clear() {
    while (!empty()) {
        pop_front();
    }
}

template<typename T>
bool DoublyCircularList<T>::empty() const {
    return size_ == 0;
}

template<typename T>
std::size_t DoublyCircularList<T>::size() const {
    return size_;
}

template<typename T>
void DoublyCircularList<T>::push_front(const T& data) {
    Node* new_node = new Node(data);
    if (!new_node) throw std::out_of_range("memória insuficiente");

    if (empty()) {
        new_node->next(new_node);
        new_node->prev(new_node);
        head = new_node;
    } else {
        Node* tail = head->prev();
        new_node->next(head);
        new_node->prev(tail);
        head->prev(new_node);
        tail->next(new_node);
        head = new_node;
    }

    ++size_;
}

template<typename T>
void DoublyCircularList<T>::push_back(const T& data) {
    if (empty()) {
        push_front(data);
    } else {
        Node* tail = head->prev();
        Node* new_node = new Node(data, tail, head);
        if (!new_node) throw std::out_of_range("memória insuficiente");

        tail->next(new_node);
        head->prev(new_node);
        ++size_;
    }
}

template<typename T>
void DoublyCircularList<T>::insert(const T& data, std::size_t index) {
    if (index > size_) throw std::out_of_range("índice inválido");

    if (index == 0) {
        push_front(data);
    } else if (index == size_) {
        push_back(data);
    } else {
        Node* next_node = node_at(index);
        Node* prev_node = next_node->prev();
        Node* new_node = new Node(data, prev_node, next_node);
        if (!new_node) throw std::out_of_range("memória insuficiente");

        prev_node->next(new_node);
        next_node->prev(new_node);
        ++size_;
    }
}

template<typename T>
void DoublyCircularList<T>::insert_sorted(const T& data) {
    if (empty()) {
        push_front(data);
    } else {
        Node* current = head;
        std::size_t index = 0;
        do {
            if (data < current->data()) break;
            current = current->next();
            ++index;
        } while (current != head);
        insert(data, index);
    }
}

template<typename T>
T DoublyCircularList<T>::pop_front() {
    if (empty()) throw std::out_of_range("lista vazia");

    Node* old_head = head;
    T value = old_head->data();

    if (size_ == 1) {
        head = nullptr;
    } else {
        Node* tail = head->prev();
        head = head->next();
        head->prev(tail);
        tail->next(head);
    }

    delete old_head;
    --size_;
    return value;
}

template<typename T>
T DoublyCircularList<T>::pop_back() {
    if (empty()) throw std::out_of_range("lista vazia");
    return pop(size_ - 1);
}

template<typename T>
T DoublyCircularList<T>::pop(std::size_t index) {
    if (index >= size_) throw std::out_of_range("índice inválido");

    if (index == 0) return pop_front();

    Node* target = node_at(index);
    T value = target->data();
    Node* prev_node = target->prev();
    Node* next_node = target->next();

    prev_node->next(next_node);
    next_node->prev(prev_node);

    delete target;
    --size_;
    return value;
}

template<typename T>
void DoublyCircularList<T>::remove(const T& data) {
    Node* current = head;
    for (std::size_t i = 0; i < size_; ++i) {
        if (current->data() == data) {
            pop(i);
            return;
        }
        current = current->next();
    }
}

template<typename T>
T& DoublyCircularList<T>::at(std::size_t index) {
    if (index >= size_) throw std::out_of_range("índice inválido");
    return node_at(index)->data();
}

template<typename T>
const T& DoublyCircularList<T>::at(std::size_t index) const {
    if (index >= size_) throw std::out_of_range("índice inválido");
    return node_at(index)->data();
}

template<typename T>
bool DoublyCircularList<T>::contains(const T& data) const {
    Node* current = head;
    for (std::size_t i = 0; i < size_; ++i) {
        if (current->data() == data) return true;
        current = current->next();
    }
    return false;
}

template<typename T>
std::size_t DoublyCircularList<T>::find(const T& data) const {
    Node* current = head;
    for (std::size_t i = 0; i < size_; ++i) {
        if (current->data() == data) return i;
        current = current->next();
    }
    return size_;
}

}  // namespace structures

#endif  // STRUCTURES_DOUBLY_CIRCULAR_LIST_H
