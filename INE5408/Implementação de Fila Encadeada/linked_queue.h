#ifndef STRUCTURES_LINKED_QUEUE_H
#define STRUCTURES_LINKED_QUEUE_H

#include <cstdint>      // std::size_t
#include <stdexcept>    // exceções

namespace structures {

template<typename T>
class LinkedQueue {
 public:
    LinkedQueue() {
        head = nullptr;
        tail = nullptr;
        size_ = 0;
    }

    ~LinkedQueue() {
        clear();
    }

    void clear() {
        while (!empty()) {
            dequeue();
        }
    }

    void enqueue(const T& data) {
        Node* novo = new Node(data);
        if (empty()) {
            head = tail = novo;
        } else {
            tail->next = novo;
            tail = novo;
        }
        ++size_;
    }

    T dequeue() {
        if (empty()) {
            throw std::out_of_range("Fila vazia");
        }
        Node* lixo = head;
        T dado = lixo->data;
        head = head->next;
        delete lixo;
        --size_;
        if (empty()) {
            tail = nullptr;  // se esvaziou a fila, zera o tail também
        }
        return dado;
    }

    T& front() const {
        if (empty()) {
            throw std::out_of_range("Fila vazia");
        }
        return head->data;
    }

    T& back() const {
        if (empty()) {
            throw std::out_of_range("Fila vazia");
        }
        return tail->data;
    }

    bool empty() const {
        return size_ == 0;
    }

    std::size_t size() const {
        return size_;
    }

 private:
    struct Node {
        T data;
        Node* next;

        Node(const T& data_) : data(data_), next(nullptr) {}
    };

    Node* head;  // nodo-cabeça (frente da fila)
    Node* tail;  // nodo-cauda (fim da fila)
    std::size_t size_;
};

}  // namespace structures

#endif
