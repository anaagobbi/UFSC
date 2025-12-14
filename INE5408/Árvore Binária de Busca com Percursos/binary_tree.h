// Copyright [2025] <ANA LUIZA SALES GOBBI>

#include "array_list.h"

namespace structures {

// AS IMPLEMENTAÇÕES PODEM SER RECURSIVAS OU NÃO
// SE NECESSÁRIO, OPERAÇÕES ALTERNATIVAS OU AUXILIARES PODEM SER ADICIONADAS
// (NESTE CASO, A OPERAÇÃO TESTADA É BASICAMENTE UMA CHAMADA A OUTRO MÉTODO)

template<typename T>
class BinaryTree {
public:
    BinaryTree();

    ~BinaryTree();

    void insert(const T& data);

    void remove(const T& data);

    bool contains(const T& data) const;

    bool empty() const;

    std::size_t size() const;

    ArrayList<T> pre_order() const;

    ArrayList<T> in_order() const;

    ArrayList<T> post_order() const;

private:
    struct Node {
        explicit Node(const T& data_) {
            data = data_;
            left = nullptr;
            right = nullptr;
        }

        T data;
        Node* left;
        Node* right;

        void insert(const T& data_) {
            if (data_ < data) {
                if (left == nullptr)
                    left = new Node(data_);
                else
                    left->insert(data_);
            } else {
                if (right == nullptr)
                    right = new Node(data_);
                else
                    right->insert(data_);
            }
        }

        bool remove(const T& data_) {
            // NÃO USADO – remoção é feita fora do nó
            return false;
        }

        bool contains(const T& data_) const {
            if (data_ == data) {
                return true;
            } else if (data_ < data) {
                if (left != nullptr) {
                    return left->contains(data_);
                } else {
                    return false;
                }
            } else {  // data_ > data
                if (right != nullptr) {
                    return right->contains(data_);
                } else {
                    return false;
                }
            }
        }

        void pre_order(ArrayList<T>& v) const {
            v.push_back(data);
            if (left != nullptr)
                left->pre_order(v);
            if (right != nullptr)
                right->pre_order(v);
        }

        void in_order(ArrayList<T>& v) const {
            if (left != nullptr)
                left->in_order(v);
            v.push_back(data);
            if (right != nullptr)
                right->in_order(v);
        }

        void post_order(ArrayList<T>& v) const {
            if (left != nullptr)
                left->post_order(v);
            if (right != nullptr)
                right->post_order(v);
            v.push_back(data);
        }
    };

    Node* root;
    std::size_t size_;
};

}  // namespace structures

//-------------------------------------

template<typename T>
structures::BinaryTree<T>::BinaryTree() {
    root = nullptr;
    size_ = 0;
}

template<typename T>
structures::BinaryTree<T>::~BinaryTree() {
    while (!empty()) {
        remove(root->data);
    }
}

template<typename T>
void structures::BinaryTree<T>::insert(const T& data) {
    if (root == nullptr) {
        root = new Node(data);
    } else {
        root->insert(data);
    }
    ++size_;
}

template<typename T>
void structures::BinaryTree<T>::remove(const T& data) {
    if (root == nullptr) return;

    Node* parent = nullptr;
    Node* current = root;
    while (current != nullptr && current->data != data) {
        parent = current;
        if (data < current->data)
            current = current->left;
        else
            current = current->right;
    }

    if (current == nullptr) return;  // Não achou

    if (current->left == nullptr || current->right == nullptr) {
        Node* newCurr;
        if (current->left == nullptr)
            newCurr = current->right;
        else
            newCurr = current->left;

        if (parent == nullptr) {
            delete root;
            root = newCurr;
        } else {
            if (current == parent->left)
                parent->left = newCurr;
            else
                parent->right = newCurr;
            delete current;
        }
    } else {
        Node* p = nullptr;
        Node* temp = current->right;
        while (temp->left != nullptr) {
            p = temp;
            temp = temp->left;
        }

        if (p != nullptr)
            p->left = temp->right;
        else
            current->right = temp->right;

        current->data = temp->data;
        delete temp;
    }

    --size_;
}

template<typename T>
bool structures::BinaryTree<T>::contains(const T& data) const {
    if (root != nullptr) {
        return root->contains(data);
    } else {
        return false;
    }
}

template<typename T>
bool structures::BinaryTree<T>::empty() const {
    return size() == 0;
}

template<typename T>
std::size_t structures::BinaryTree<T>::size() const {
    return size_;
}

template<typename T>
structures::ArrayList<T> structures::BinaryTree<T>::pre_order() const {
    structures::ArrayList<T> L;
	if (root != nullptr) {
		root->pre_order(L);
	}
	return L;
}

template<typename T>
structures::ArrayList<T> structures::BinaryTree<T>::in_order() const {
    structures::ArrayList<T> L;
    if (root != nullptr) {
        root->in_order(L);
    }
    return L;
}

template<typename T>
structures::ArrayList<T> structures::BinaryTree<T>::post_order() const {
    structures::ArrayList<T> L;
    if (root != nullptr) {
        root->post_order(L);
    }
    return L;
}
