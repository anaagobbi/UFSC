#ifndef STRUCTURES_LINKED_STACK_H
#define STRUCTURES_LINKED_STACK_H

#include <cstdint>  // std::size_t
#include <stdexcept>  // exceções

namespace structures {

template<typename T>
class LinkedStack {
 public:
    LinkedStack() {
        top_ = nullptr;
        size_ = 0;
    }

    ~LinkedStack() {
        clear();
    }

    void clear() {
        while (!empty()) {
            pop();
        }
    }

    void push(const T& data) {
        Node* novo = new Node(data, top_);
        top_ = novo;
        ++size_;
    }

    T pop() {
        if (empty()) {
            throw std::out_of_range("Pilha vazia");
        }
        Node* lixo = top_;
        T dado = lixo->data();
        top_ = top_->next();
        delete lixo;
        --size_;
        return dado;
    }

    T& top() const {
        if (empty()) {
            throw std::out_of_range("Pilha vazia");
        }
        return top_->data();
    }

    bool empty() const {
        return size_ == 0;
    }

    std::size_t size() const {
        return size_;
    }

 private:
    class Node {
     public:
        Node(const T& data) : data_{data}, next_{nullptr} {}

        Node(const T& data, Node* next) : data_{data}, next_{next} {}

        T& data() {
            return data_;
        }

        const T& data() const {
            return data_;
        }

        Node* next() {
            return next_;
        }

        const Node* next() const {
            return next_;
        }

        void next(Node* next) {
            next_ = next;
        }

     private:
        T data_;
        Node* next_;
    };

    Node* top_;  // nodo-topo
    std::size_t size_;  // tamanho
};

}  // namespace structures

#endif
