# A3_1.py
# Implementação do Algoritmo de Edmonds-Karp

from collections import deque

def bfs(graph, s, t, parent):
    """
    Busca em Largura (BFS) para encontrar um caminho de aumento na rede residual.
    
    graph: Matriz de adjacência/capacidade da rede residual.
    s: Vértice de origem.
    t: Vértice de destino.
    parent: Array para armazenar o caminho.
    Retorna: True se um caminho é encontrado, False caso contrário.
    """
    V = len(graph)
    visited = [False] * V
    queue = deque()
    
    queue.append(s)
    visited[s] = True
    
    while queue:
        u = queue.popleft()
        
        # Itera sobre todos os vértices v
        for v in range(V):
            # Condição para aresta de avanço: capacidade > 0 e v não visitado
            if not visited[v] and graph[u][v] > 0:
                queue.append(v)
                visited[v] = True
                parent[v] = u # Armazena o predecessor
                if v == t:
                    return True
    return False

def edmonds_karp(capacity_matrix, s, t):
    """
    Implementação do Algoritmo de Edmonds-Karp para encontrar o fluxo máximo.
    
    capacity_matrix: Matriz de capacidade do grafo original (capacidade[u][v]).
    s: Vértice de origem (source).
    t: Vértice de destino (sink).
    Retorna: O valor do fluxo máximo.
    """
    V = len(capacity_matrix)
    
    # Criamos a rede residual, que inicialmente é igual à rede de capacidade.
    residual_graph = [row[:] for row in capacity_matrix]
    
    parent = [-1] * V # Array para armazenar o caminho (necessário para BFS)
    max_flow = 0
    
    # Enquanto existir um caminho de aumento na rede residual (encontrado por BFS)
    while bfs(residual_graph, s, t, parent):
        
        # Encontra a capacidade de gargalo (bottleneck) do caminho
        path_flow = float('inf')
        v = t
        while v != s:
            u = parent[v]
            path_flow = min(path_flow, residual_graph[u][v])
            v = u
            
        # Adiciona o fluxo do caminho ao fluxo total
        max_flow += path_flow
        
        # Atualiza as capacidades residuais (avançar e reverso)
        v = t
        while v != s:
            u = parent[v]
            
            # Aresta de avanço: subtrai o fluxo do caminho
            residual_graph[u][v] -= path_flow
            
            # Aresta de retorno (residual): adiciona o fluxo do caminho
            # A aresta de retorno na matriz residual representa a possibilidade 
            # de "desfazer" o fluxo.
            residual_graph[v][u] += path_flow 
            v = u
            
    return max_flow

# --- Função de Leitura de Grafo ---
def read_graph_instance(file_path):
    """
    Lê o grafo a partir do arquivo (assumindo o formato .net, que geralmente 
    começa com *vertices, *arcs ou *edges, e lista as arestas com pesos).
    
    ADAPTE ESTA FUNÇÃO para o formato de instância exato fornecido.
    A implementação abaixo é um esboço que usa uma matriz de adjacência/capacidade.
    """
    try:
        with open(file_path, 'r') as f:
            lines = f.readlines()
    except FileNotFoundError:
        print(f"Erro: Arquivo {file_path} não encontrado.")
        return None, None, None
        
    V = 0
    edges = []
    
    # Lógica de leitura (exemplo simplificado, ajuste para o formato .net)
    in_arcs_section = False
    for line in lines:
        line = line.strip()
        if not line or line.startswith('*'):
            if line.startswith('*vertices'):
                # Espera que a linha seguinte contenha o número de vértices (ou a seção de vértices)
                try:
                    V = int(lines[lines.index(line) + 1].split()[0])
                except:
                    # Em alguns .net, *vertices pode ser seguido diretamente pelo número ou lista
                    pass
            if line.startswith('*arcs') or line.startswith('*edges'):
                in_arcs_section = True
                continue
            continue

        if in_arcs_section:
            # Assumindo formato "u v capacity"
            try:
                parts = line.split()
                u, v, capacity = int(parts[0]), int(parts[1]), int(parts[2])
                edges.append((u, v, capacity))
            except:
                continue

    if V == 0:
        # Tenta inferir o número de vértices a partir das arestas
        max_v = 0
        for u, v, _ in edges:
            max_v = max(max_v, u, v)
        V = max_v
        
    # Os vértices devem ser mapeados para índices 0 a V-1
    # Se os vértices no arquivo começam em 1, subtraímos 1.
    capacity_matrix = [[0] * V for _ in range(V)]
    
    for u, v, capacity in edges:
        # Se os vértices começam em 1 (comum em arquivos de grafos), ajuste para índice 0
        u_idx, v_idx = u - 1, v - 1 
        
        if u_idx < V and v_idx < V:
            capacity_matrix[u_idx][v_idx] = capacity
        
    return capacity_matrix, V

# --- Execução Principal ---
import sys

if __name__ == "__main__":
    if len(sys.argv) != 4:
        # sys.argv[0] é o nome do programa
        # sys.argv[1] é o nome do arquivo do grafo (ex: pequeno.net)
        # sys.argv[2] é o vértice de origem s
        # sys.argv[3] é o vértice de destino t
        print("Uso: python3 A3_1.py <arquivo_grafo> <vertice_s> <vertice_t>", file=sys.stderr)
        sys.exit(1)

    file_path = sys.argv[1]
    
    # Lendo o grafo e a matriz de capacidade. 
    capacity_matrix, V = read_graph_instance(file_path)
    
    if capacity_matrix is None or V is None:
        sys.exit(1)
        
    try:
        # Lembre-se: os vértices de entrada (s e t) podem ser 1-based.
        # Ajustamos para 0-based para uso na matriz.
        s_input = int(sys.argv[2])
        t_input = int(sys.argv[3])
        s_idx = s_input - 1 
        t_idx = t_input - 1
        
        # Verificação básica de limites
        if s_idx < 0 or s_idx >= V or t_idx < 0 or t_idx >= V:
            print(f"Erro: Vértices s={s_input} ou t={t_input} fora do intervalo [1, {V}].", file=sys.stderr)
            sys.exit(1)
            
    except ValueError:
        print("Erro: Vértices s e t devem ser inteiros.", file=sys.stderr)
        sys.exit(1)


    # Calculando o fluxo máximo
    max_flow = edmonds_karp(capacity_matrix, s_idx, t_idx)

    # Saída esperada: valor do fluxo máximo (na primeira linha)
    print(max_flow)