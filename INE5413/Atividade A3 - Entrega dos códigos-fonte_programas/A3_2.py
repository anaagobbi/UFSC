# A3_2.py
# Implementação do Algoritmo de Hopcroft-Karp

from collections import deque
import sys

# Constantes para representar "não pareado" e "infinito"
NIL = 0
INF = float('inf')

class HopcroftKarp:
    """
    Classe para implementar o algoritmo de Hopcroft-Karp.
    """
    def __init__(self, V_left, V_right):
        # V_left e V_right são as coleções de vértices das duas partições.
        self.V_left = V_left
        self.V_right = V_right
        
        # O grafo é armazenado como uma lista de adjacência, 
        # onde as chaves são vértices da esquerda e os valores são seus vizinhos na direita.
        self.adj = {u: [] for u in V_left}
        
        # pairU[u] = par de u, onde u está em V_left. NIL se u não está pareado.
        self.pairU = {u: NIL for u in V_left}
        # pairV[v] = par de v, onde v está em V_right. NIL se v não está pareado.
        self.pairV = {v: NIL for v in V_right}
        
        # dist[u] = distância (nível) de u durante a BFS.
        self.dist = {u: INF for u in V_left}

    def add_edge(self, u, v):
        """Adiciona uma aresta (u, v) onde u está na partição esquerda."""
        if u in self.adj:
            self.adj[u].append(v)

    def bfs(self):
        """
        Executa a BFS para encontrar o conjunto maximal de caminhos de aumento 
        mais curtos. 
        """
        queue = deque()
        
        # Inicializa distâncias e fila com vértices livres na esquerda
        for u in self.V_left:
            if self.pairU[u] == NIL:
                self.dist[u] = 0
                queue.append(u)
            else:
                self.dist[u] = INF
        
        # dist[NIL] é o nível onde termina a busca (qualquer vértice livre na direita)
        self.dist[NIL] = INF

        while queue:
            u = queue.popleft()
            
            if self.dist[u] < self.dist[NIL]:
                for v in self.adj[u]:
                    # O "par" de v (pairV[v]) está na partição esquerda ou é NIL.
                    matched_u = self.pairV[v]
                    
                    # Se o par na esquerda não foi visitado, defina seu nível e adicione à fila.
                    if self.dist.get(matched_u, INF) == INF:
                        self.dist[matched_u] = self.dist[u] + 1
                        queue.append(matched_u)

        return self.dist[NIL] != INF # Retorna True se um caminho de aumento foi encontrado.


    def dfs(self, u):
        """
        Executa a DFS para encontrar um caminho de aumento a partir de 'u' 
        e atualiza o emparelhamento.
        """
        if u != NIL:
            for v in self.adj[u]:
                matched_u = self.pairV[v]
                
                # Procura por um caminho de aumento que respeita os níveis de BFS
                if self.dist.get(matched_u, INF) == self.dist[u] + 1:
                    
                    if self.dfs(matched_u):
                        # Se encontramos um caminho, atualiza o emparelhamento
                        self.pairV[v] = u
                        self.pairU[u] = v
                        return True
            
            # Se não encontrar caminho a partir de u, aumenta a distância (para evitar revisitar)
            self.dist[u] = INF 
            return False
        return True # NIL é o destino, sempre termina um caminho.


    def max_matching(self):
        """
        Função principal que executa o algoritmo de Hopcroft-Karp.
        Retorna: O tamanho do emparelhamento máximo e as arestas do emparelhamento.
        """
        matching_size = 0
        
        while self.bfs():
            for u in self.V_left:
                if self.pairU[u] == NIL:
                    if self.dfs(u):
                        matching_size += 1
                        
        # Formata o resultado
        matching_edges = []
        for u in self.V_left:
            v = self.pairU[u]
            if v != NIL:
                # Aresta formatada como "u-v"
                matching_edges.append(f"{u}-{v}")
                
        return matching_size, matching_edges


# --- Função de Leitura de Grafo ---
def read_bipartite_graph(file_path):
    """
    Lê o grafo a partir do arquivo (assumindo o formato .net, não-dirigido, 
    não-ponderado) e divide nos dois conjuntos bipartidos.
    
    ADAPTE ESTA FUNÇÃO para o formato de instância exato fornecido.
    A implementação abaixo é um esboço.
    """
    try:
        with open(file_path, 'r') as f:
            lines = f.readlines()
    except FileNotFoundError:
        print(f"Erro: Arquivo {file_path} não encontrado.", file=sys.stderr)
        return None, None
        
    n = 0
    edges = []
    
    # Lógica de leitura (exemplo simplificado, ajuste para o formato .net)
    in_edges_section = False
    for line in lines:
        line = line.strip()
        if not line or line.startswith('*'):
            if line.startswith('*vertices'):
                # Espera que a linha seguinte contenha o número de vértices
                try:
                    n = int(lines[lines.index(line) + 1].split()[0])
                except:
                    pass
            if line.startswith('*edges'):
                in_edges_section = True
                continue
            continue
            
        if in_edges_section:
            # Assumindo formato "u v" (não-ponderado)
            try:
                parts = line.split()
                u, v = int(parts[0]), int(parts[1])
                edges.append((u, v))
            except:
                continue

    if n == 0:
        # Tenta inferir o número de vértices a partir das arestas
        max_v = 0
        for u, v in edges:
            max_v = max(max_v, u, v)
        n = max_v
        
    # Divisão do grafo bipartido (vértices de 1 a n)
    # Parte 1: Vértices 1 a floor(n/2)
    # Parte 2: Vértices floor(n/2) + 1 a n
    
    half_n = n // 2
    V_left = set(range(1, half_n + 1))
    V_right = set(range(half_n + 1, n + 1))
    
    if n == 0 or not V_left or not V_right:
        print("Erro: Não foi possível determinar o número de vértices ou a partição bipartida.", file=sys.stderr)
        return None, None
        
    hk = HopcroftKarp(V_left, V_right)
    
    for u, v in edges:
        # As arestas só podem conectar um vértice de V_left a um de V_right (ou vice-versa)
        if u in V_left and v in V_right:
            hk.add_edge(u, v)
        elif v in V_left and u in V_right:
            hk.add_edge(v, u) # Sempre armazena u (left) -> v (right)
        # Ignora arestas dentro da mesma partição (não deve ocorrer em grafo bipartido)
        
    return hk

# --- Execução Principal ---
if __name__ == "__main__":
    if len(sys.argv) != 2:
        # sys.argv[0] é o nome do programa
        # sys.argv[1] é o nome do arquivo do grafo (ex: pequeno.net)
        print("Uso: python3 A3_2.py <arquivo_grafo>", file=sys.stderr)
        sys.exit(1)

    file_path = sys.argv[1]
    
    # Lendo o grafo e criando a instância do HopcroftKarp
    hk_solver = read_bipartite_graph(file_path)
    
    if hk_solver is None:
        sys.exit(1)

    # Calculando o emparelhamento máximo
    matching_size, matching_edges = hk_solver.max_matching()

    # Saída esperada:
    # 1ª linha: Quantidade de emparelhamentos
    # 2ª linha: Arestas correspondentes separadas por vírgula e espaço (ex: 1-4, 2-5, 3-6)
    
    print(matching_size)
    print(", ".join(matching_edges))