import numpy as np
import matplotlib.pyplot as plt

def resolver_tridiagonal(a, d, c, b):
    """Resolve o sistema tridiagonal Ax = b usando o método de Thomas."""
    n = len(d)
    a, d, c, b = a.copy(), d.copy(), c.copy(), b.copy()

    # Eliminação
    for i in range(1, n):
        w = a[i-1] / d[i-1]
        d[i] = d[i] - w * c[i-1]
        b[i] = b[i] - w * b[i-1]

    # Substituição para trás
    x = np.zeros(n)
    x[-1] = b[-1] / d[-1]
    for i in range(n-2, -1, -1):
        x[i] = (b[i] - c[i]*x[i+1]) / d[i]

    return x

def teste_n5():
    """Teste para n = 5"""
    n = 5
    a = np.ones(n-1) * (-1)  # subdiagonal
    d = np.ones(n) * 2       # diagonal principal
    c = np.ones(n-1) * (-1)  # superdiagonal
    b = np.zeros(n)
    b[0] = 1
    b[-1] = 1

    x = resolver_tridiagonal(a, d, c, b)
    print("Solução para n=5:", x)

def teste_n30():
    """Teste para n = 30 e h = 0.1"""
    n = 30
    h = 0.1
    valor_diag = -2 * (1 + h**2)

    a = np.ones(n-1)  # subdiagonal
    d = np.ones(n) * valor_diag  # diagonal principal
    c = np.ones(n-1)  # superdiagonal
    b = np.zeros(n)
    b[0] = 1
    b[-1] = 1

    x = resolver_tridiagonal(a, d, c, b)

    plt.plot(np.linspace(0, 1, n), x, marker='o')
    plt.title('Solução para n = 30 e h = 0.1')
    plt.xlabel('i')
    plt.ylabel('x_i')
    plt.grid(True)
    plt.show()

if __name__ == "__main__":
    print("Escolha o teste:")
    print("1 - Sistema com n=5")
    print("2 - Sistema com n=30 e gráfico")

    opcao = input("Digite 1 ou 2: ")

    if opcao == "1":
        teste_n5()
    elif opcao == "2":
        teste_n30()
    else:
        print("Opção inválida.")
