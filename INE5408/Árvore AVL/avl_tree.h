// Copyright [2024] <Ana Luiza Sales Gobbi>

#ifndef STRUCTURES_AVL_TREE_H
#define STRUCTURES_AVL_TREE_H

#include <algorithm>
#include "array_list.h"

namespace structures {

template<typename T>
class AVLTree {
 public:
    ~AVLTree() {
        clear(root_);
    }

    void insert(const T& data) {
        root_ = insertNode(root_, data);
        ++size_;
    }

    void remove(const T& data) {
        root_ = removeNode(root_, data);
        --size_;
    }

    bool contains(const T& data) const {
        return containsNode(root_, data);
    }

    bool empty() const {
        return size_ == 0;
    }

    std::size_t size() const {
        return size_;
    }

    int height() const {
        return root_ ? root_->height() : 0;
    }

    ArrayList<T> pre_order() const {
        ArrayList<T> list;
        if (root_) {
            root_->pre_order(list);
        }
        return list;
    }

    ArrayList<T> in_order() const {
        ArrayList<T> list;
        if (root_) {
            root_->in_order(list);
        }
        return list;
    }

    ArrayList<T> post_order() const {
        ArrayList<T> list;
        if (root_) {
            root_->post_order(list);
        }
        return list;
    }

 private:
    struct Node {
        explicit Node(const T& data_) :
            data(data_), height_(1), left(nullptr), right(nullptr) {}

        T data;
        int height_;
        Node* left;
        Node* right;

        void updateHeight() {
            int lh = left ? left->height_ : 0;
            int rh = right ? right->height_ : 0;
            height_ = 1 + std::max(lh, rh);
        }

        int balanceFactor() const {
            int lh = left ? left->height_ : 0;
            int rh = right ? right->height_ : 0;
            return lh - rh;
        }

        Node* simpleLeft() {
            Node* r = right;
            right = r->left;
            r->left = this;
            updateHeight();
            r->updateHeight();
            return r;
        }

        Node* simpleRight() {
            Node* l = left;
            left = l->right;
            l->right = this;
            updateHeight();
            l->updateHeight();
            return l;
        }

        Node* doubleLeft() {
            right = right->simpleRight();
            return simpleLeft();
        }

        Node* doubleRight() {
            left = left->simpleLeft();
            return simpleRight();
        }

        void pre_order(ArrayList<T>& v) const {
            v.push_back(data);
            if (left) {
                left->pre_order(v);
            }
            if (right) {
                right->pre_order(v);
            }
        }

        void in_order(ArrayList<T>& v) const {
            if (left) {
                left->in_order(v);
            }
            v.push_back(data);
            if (right) {
                right->in_order(v);
            }
        }

        void post_order(ArrayList<T>& v) const {
            if (left) {
                left->post_order(v);
            }
            if (right) {
                right->post_order(v);
            }
            v.push_back(data);
        }

        int height() const {
            return height_;
        }
    };

    Node* root_ = nullptr;
    std::size_t size_ = 0u;

    void clear(Node* node) {
        if (!node) return;
        clear(node->left);
        clear(node->right);
        delete node;
    }

    Node* insertNode(Node* node, const T& data) {
        if (!node) return new Node(data);

        if (data < node->data) {
            node->left = insertNode(node->left, data);
        } else {
            node->right = insertNode(node->right, data);
        }

        node->updateHeight();
        return balance(node);
    }

    Node* removeNode(Node* node, const T& data) {
        if (!node) return nullptr;

        if (data < node->data) {
            node->left = removeNode(node->left, data);
        } else if (data > node->data) {
            node->right = removeNode(node->right, data);
        } else {
            if (!node->left || !node->right) {
                Node* temp = node->left ? node->left : node->right;
                delete node;
                return temp;
            } else {
                Node* temp = minNode(node->right);
                node->data = temp->data;
                node->right = removeNode(node->right, temp->data);
            }
        }

        node->updateHeight();
        return balance(node);
    }

    Node* balance(Node* node) {
        int bf = node->balanceFactor();

        if (bf > 1) {
            if (node->left->balanceFactor() >= 0) {
                return node->simpleRight();  // LL
            } else {
                return node->doubleRight();  // LR
            }
        } else if (bf < -1) {
            if (node->right->balanceFactor() <= 0) {
                return node->simpleLeft();  // RR
            } else {
                return node->doubleLeft();  // RL
            }
        }
        return node;
    }

    Node* minNode(Node* node) {
        while (node->left) {
            node = node->left;
        }
        return node;
    }

    bool containsNode(Node* node, const T& data) const {
        if (!node) return false;
        if (data < node->data) return containsNode(node->left, data);
        if (data > node->data) return containsNode(node->right, data);
        return true;
    }
};

}  // namespace structures

#endif  // STRUCTURES_AVL_TREE_H