//!  Copyright [2024] <ANA LUIZA SALES GOBBI>

#ifndef STRUCTURES_BINARY_TREE_H
#define STRUCTURES_BINARY_TREE_H

#include "./array_list.h"


namespace structures {

//! Classe Árvore binária
template<typename T>
class BinaryTree {
 public:
    //! ...
    BinaryTree();
    //! ...
    ~BinaryTree();
    //! ...
    void insert(const T& data);
    //! ...
    void remove(const T& data);
    //! ...
    bool contains(const T& data) const;
    //! ...
    bool empty() const;
    //! ...
    std::size_t size() const;
    //! ...
    ArrayList<T> pre_order() const;
    //! ...
    ArrayList<T> in_order() const;
    //! ...
    ArrayList<T> post_order() const;

    //******************************************************************
    // Prova prática - implementações necessárias:

    // (1) poda da árvore enraizada em 'data':
    void pruning(T data);

    // (2) criação de uma árvore binária de busca balanceada da união
    //     da própria com outra B passada por parâmetro:
    BinaryTree<T> balanced_union(BinaryTree<T> *B);

    // (3) criação de uma lista contendo o comprimento de caminho de
    //     cada folha até a raiz (na sequência "em ordem" de folhas):
    ArrayList<int> leaves_path_length();

    // (4) remoção dos nós, da própria árvore, pertencentes ao maior
    //     comprimento de caminho, e retorno da lista de valores da
    //     folha até a raiz:
    ArrayList<T> remove_max_path();

    //******************************************************************


 private:
    struct Node {
        //! Construtor
        explicit Node(const T& data) :
        data_{data}
        {}

        //! Destrutor
        ~Node() {
            delete left_;
            delete right_;
        }

        T data_;
        Node* left_{nullptr};
        Node* right_{nullptr};
        Node* parent_{nullptr};

        //! Inserção
        void insert(const T& data) {
            if (data < data_) {
                if (left_ == nullptr) {
                    left_ = new Node(data);
                    if (left_ == nullptr)
                        throw std::out_of_range("Full tree!");
                    left_->parent_ = this;
                } else {
                    left_->insert(data);
                }
            } else {
                if (right_ == nullptr) {
                    right_ = new Node(data);
                    if (right_ == nullptr)
                        throw std::out_of_range("Full tree!");
                    right_->parent_ = this;
                } else {
                    right_->insert(data);
                }
            }
        }

        //! Remoção
        bool remove(const T& data) {
            bool deleted = false;
            if (data < data_ && left_ != nullptr) {
                left_ = remove(data, left_, deleted);
            } else if (data > data_ && right_ != nullptr) {
                right_ = remove(data, right_, deleted);
            } else {
                if (right_ != nullptr && left_ != nullptr) {
                    Node* temp = right_->minimum();
                    data_ = temp->data_;
                    right_ = remove(data_, right_, deleted);
                } else if (right_ != nullptr) {
                    Node* temp = right_;
                    data_ = temp->data_;
                    right_ = temp->right_;
                    left_ = temp->left_;
                    right_->parent_ = this;
                    left_->parent_ = this;
                    temp->right_ = temp->left_ = nullptr;
                    delete temp;
                    deleted = true;
                } else if (left_ != nullptr) {
                    Node* temp = left_;
                    data_ = temp->data_;
                    right_ = temp->right_;
                    left_ = temp->left_;
                    right_->parent_ = this;
                    left_->parent_ = this;
                    temp->right_ = temp->left_ = nullptr;
                    delete temp;
                    deleted = true;
                }
            }
            return deleted;
        }

        //! Contém
        bool contains(const T& data) const {
            if (data < data_)
                return left_ == nullptr? false : left_->contains(data);
            else if (data > data_)
                return right_ == nullptr? false : right_->contains(data);
            else
                return true;
        }

        //! Pré ordem
        void pre_order(ArrayList<T>& v) const {
            v.push_back(data_);
            if (left_ != nullptr)
                left_->pre_order(v);
            if (right_ != nullptr)
                right_->pre_order(v);
        }

        //! Em ordem
        void in_order(ArrayList<T>& v) const {
            if (left_ != nullptr)
                left_->in_order(v);
            v.push_back(data_);
            if (right_ != nullptr)
                right_->in_order(v);
        }

        //! Pós ordem
        void post_order(ArrayList<T>& v) const {
            if (left_ != nullptr)
                left_->post_order(v);
            if (right_ != nullptr)
                right_->post_order(v);
            v.push_back(data_);
        }


        T data() {
            return data_;
        }

        Node* min() {
            if (left_ == nullptr)
                return this;
            return left_->min();
        }

        Node* max() {
            if (right_ == nullptr)
                return this;
            return right_->max();
        }


        //! Contém
        Node *acha(const T& data) {
            if (data < data_)
                return left_ == nullptr? nullptr : left_->acha(data);
            else if (data > data_)
                return right_ == nullptr? nullptr : right_->acha(data);
            else
                return this;
        }

        //! Pós ordem
        void percorre(ArrayList<T>& v) {
            if (left_ != nullptr)
                left_->percorre(v);
            if (right_ != nullptr)
                right_->percorre(v);
            v.push_back(data_);
        }

        //! Em ordem
        void acha_folhas(ArrayList<Node *>& v) {
            if (left_ != nullptr)
                left_->acha_folhas(v);
            if (left_ == nullptr && right_ == nullptr) {
                v.push_back(this);
            }
            if (right_ != nullptr)
                right_->acha_folhas(v);
        }



     private:
        //! Complemento da funcão de remoção
        Node* remove(const T& data, Node* arv, bool& deleted) {
            deleted = false;
            if (arv == nullptr)
                return arv;
            if (data < arv->data_) {
                arv->left_ = remove(data, arv->left_, deleted);
                return arv;
            }
            if (data > arv->data_) {
                arv->right_ = remove(data, arv->right_, deleted);
                return arv;
            }
            // dois filhos
            if (arv->right_ != nullptr && arv->left_ != nullptr) {
                Node* temp = arv->right_->minimum();
                arv->data_ = temp->data_;
                arv->right_ = remove(temp->data_, arv->right_, deleted);
                return arv;
            }
            // um filho ou folha
            Node* temp = nullptr;
            if (arv->right_ != nullptr) {
                temp = arv->right_;
                if (arv->parent_ == nullptr) {
                    temp->parent_ = nullptr;
                }
            } else {
                temp = arv->left_;
                if (arv->parent_ == nullptr) {
                    temp->parent_ = nullptr;
                }
            }

            arv->right_ = nullptr;
            arv->left_  = nullptr;
            delete arv;
            deleted = true;
            return temp;
        }

        //! Encontrar o menor
        Node* minimum() {
            if (left_ == nullptr)
                return this;
            return left_->minimum();
        }
    };

    Node* root_{nullptr};
    std::size_t size_{0};
};


template<typename T>
void busca_binaria(BinaryTree<T>& A, ArrayList<T>& L,
                   std::size_t indice_inicial, std::size_t indice_final) {
    int indice_central;
    if (indice_inicial >=0 && indice_final < L.size() &&
        indice_inicial <= indice_final) {
        indice_central = (indice_inicial + indice_final) / 2;
        A.insert(L[indice_central]);
        busca_binaria(A, L, indice_inicial, indice_central-1);
        busca_binaria(A, L, indice_central+1, indice_final);
    }
}



//******************************************************************
// Prova prática - implementações necessárias:

// (1) poda da árvore enraizada em 'data':
template<typename T>
void BinaryTree<T>::pruning(T data) {
    if (empty()) return;
    typename BinaryTree<T>:: Node* sub_root = root_->acha(data);
    if (sub_root == nullptr) {
        delete root_;
        root_ = nullptr;
        size_ = 0;
    } else {
        if (sub_root->parent_->left_ == sub_root)
            sub_root->parent_->parent_->left_ = nullptr;
        else
            sub_root->parent_->right_ = nullptr;
        // std::size_t before = size_;
        delete sub_root;
        ArrayList<T> v;
        if (root_ != nullptr )
            root_ ->percorre(v);
        size_ = v.size();
    }
    //
    //    COLOQUE SEU CÓDICO AQUI..
    //
}

// (2) criação de uma árvore binária de busca balanceada da união
//     da própria com outra B passada por parâmetro:
template<typename T>
BinaryTree<T> BinaryTree<T>::balanced_union(BinaryTree<T> *B) {
    BinaryTree<T> t;
    if (!empty()) {
        ArrayList<T> vA{size_};
        root_->in_order(vA);
        for (std::size_t i = 0; i < vA.size(); ++i)
            t.insert(vA[i]);
    }
    if (B != nullptr && !B->empty()) {
        ArrayList<T> vB{B->size()};
        B->root_->in_order(vB);
        for (std::size_t i = 0; i< vB.size(); ++i)
            t.insert(vB[i]);
    }
    ArrayList<T> lista_ordenada = t.in_order();
    BinaryTree<T> result;
    busca_binaria(result, lista_ordenada, 0, lista_ordenada.size() - 1);
    return result;
    //
    //    COLOQUE SEU CÓDICO AQUI...
    //
    // return t;
}
// (3) criação de uma lista contendo o comprimento de caminho de
//     cada folha até a raiz (na sequência "em ordem" de folhas):
template<typename T>
ArrayList<int> BinaryTree<T>::leaves_path_length() {
    ArrayList<T> r;
    if (empty()) return r;
    ArrayList<typename BinaryTree<T>::Node*> folhas;
    root_->acha_folhas(folhas);
    for (std::size_t i = 0; i < folhas.size(); ++i) {
        typename BinaryTree<T>::Node* atual = folhas[i];
        int altura = 0;
        while (atual->parent_ != nullptr) {
            atual = atual->parent_;
            ++altura;
        }
        r.push_back(altura);
    }
    //
    //    COLOQUE SEU CÓDICO AQUI...
    //
    return r;
}

// (4) remoção dos nós, da própria árvore, pertencentes ao maior
//     comprimento de caminho, e retorno da lista de valores da
//     folha até a raiz:
template<typename T>
ArrayList<T> BinaryTree<T>::remove_max_path() {
    ArrayList<T> r;
    if (empty()) return r;
    ArrayList<typename BinaryTree<T>::Node*> folhas;
    root_->acha_folhas(folhas);
    typename BinaryTree<T>::Node* folha_mais_funda = nullptr;
    int max_depth = -1;
    for (std::size_t i = 0; i < folhas.size(); i++) {
        typename BinaryTree<T>::Node* atual = folhas[i];
        int depth = 0;
        while (atual->parent_ != nullptr) {
            atual = atual->parent_;
            ++depth;
        }
        if (depth > max_depth) {
            max_depth = depth;
            folha_mais_funda = folhas[i];
        }
    }
    typename BinaryTree<T>::Node* atual = folha_mais_funda;
    while (atual!= nullptr) {
        r.push_back(atual->data_);
        typename BinaryTree<T>::Node* pai = atual->parent_;
        remove(atual->data_);
        atual = pai;
    }
    //
    //    COLOQUE SEU CÓDICO AQUI...
    //
    return r;
}

//******************************************************************



//! Construtor
template<typename T>
BinaryTree<T>::BinaryTree()
{}

//! Destrutor
template<typename T>
BinaryTree<T>::~BinaryTree() {
    delete root_;
}

//! Inserção
template<typename T>
void BinaryTree<T>::insert(const T& data) {
    if (empty()) {
        root_ = new Node(data);
        if (root_ == nullptr)
            throw std::out_of_range("FUll tree!");
    } else {
            root_->insert(data);
    }
    ++size_;
}

// Remoção
template<typename T>
void BinaryTree<T>::remove(const T& data) {
    if (empty())
        throw std::out_of_range("Empty tree");

    if (size() != 1u) {
        if (root_->remove(data))
            --size_;
    } else {
        if (root_->data_ == data) {
            delete root_;
            root_ = nullptr;
            --size_;
        }
    }
}

//! Contém
template<typename T>
bool BinaryTree<T>::contains(const T& data) const {
    if (empty())
        return false;
    return root_->contains(data);
}

//! Vazio
template<typename T>
bool BinaryTree<T>::empty() const {
    return size() == 0;
}

//! Tamanho
template<typename T>
std::size_t BinaryTree<T>::size() const {
    return size_;
}

//! Pré ordem
template<typename T>
ArrayList<T> BinaryTree<T>::pre_order() const {
    ArrayList<T> v{size_};
    if (!empty())
        root_->pre_order(v);
    return v;
}

//! Em ordem
template<typename T>
ArrayList<T> BinaryTree<T>::in_order() const {
    ArrayList<T> v{size_};
    if (!empty())
        root_->in_order(v);
    return v;
}

//! Pós ordem
template<typename T>
ArrayList<T> BinaryTree<T>::post_order() const {
    ArrayList<T> v{size_};
    if (!empty())
        root_->post_order(v);
    return v;
}

}  // namespace structures

#endif
