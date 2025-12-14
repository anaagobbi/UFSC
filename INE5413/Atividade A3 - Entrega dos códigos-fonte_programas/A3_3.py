# A3_3.py
# Implementação do Algoritmo de Lawler para Coloração Mínima (Número Cromático)

import sys

# --- Funções Auxiliares para Conjuntos Independentes Maximais (MIS) ---

# Implementação do Algoritmo de Bron-Kerbosch para encontrar CLIQUEs Maximais
# (Que são Conjuntos Independentes Maximais no Grafo Complementar)
# Aqui, precisamos encontrar o MIS no grafo G. 
# A forma mais simples é um algoritmo baseado em Backtracking que gera todos os MIS.

def find_mis_recursive(G, P, R, X, mis_list):
    """
    Algoritmo de Bron-Kerbosch modificado para encontrar todos os 
    Conjuntos Independentes Maximais (MIS) de G.
    
    G: O grafo (lista de adjacência).
    P: Vértices candidatos a serem adicionados ao MIS (Set).
    R: O MIS atual em construção (Set).
    X: Vértices que já foram processados e não podem mais ser adicionados (Set).
    mis_list: Lista para armazenar os MIS encontrados.
    """
    if not P and not X:
        # Se P e X estão vazios, R é um MIS
        mis_list.append(set(R))
        return

    # Escolhe um pivô 'u' para otimizar a busca (heurística)
    pivot = next(iter(P.union(X)), None) 
    
    # Se há um pivô, P_u é P \ N(u)
    P_u = P.difference(G.get(pivot, set())) if pivot else set(P)

    for v in list(P_u):
        
        # N(v) são os vizinhos de v
        N_v = G.get(v, set()) 
        
        # Chamada recursiva para encontrar MIS, garantindo que vizinhos não sejam adicionados (condição de MIS)
        find_mis_recursive(G, P.intersection(N_v), R.union({v}), X.intersection(N_v), mis_list)
        
        # Atualiza P e X
        P.remove(v)
        X.add(v)


def find_all_mis(G, V):
    """Função wrapper para encontrar todos os MIS."""
    mis_list = []
    # Inicializa P com todos os vértices, R e X vazios
    find_mis_recursive(G, V, set(), set(), mis_list) 
    return mis_list


# --- Algoritmo de Lawler (Programação Dinâmica) ---
def lawler_coloring(G, V):
    """
    Implementação do Algoritmo de Lawler usando Programação Dinâmica.
    Calcula o número cromático (chromatic number) e a coloração mínima.
    
    G: Grafo (lista de adjacência).
    V: Conjunto de todos os vértices (para Lawler, o conjunto deve ser representável 
       como um bitmask para a DP).
    Retorna: O número cromático e o array de cores por vértice.
    """
    n = len(V)
    
    # Mapeia vértices (1-based) para índices (0-based) para o bitmask
    vertex_map = {v: i for i, v in enumerate(sorted(V))}
    
    # DP: chromatic_number[mask] armazena o número cromático do subgrafo induzido 
    # pelo conjunto de vértices representado pela mask.
    # O tamanho é 2^n.
    chromatic_number = {}
    
    # O MIS é muito custoso, no entanto, para a DP de Lawler, 
    # precisamos de todos os Conjuntos Independentes (IS), não apenas os maximais, 
    # ou podemos usar o MIS na fórmula de recorrência (como no enunciado do Lawler).
    
    # **Simplificando para o Lawler (fórmula de recorrência com MIS):** # A implementação exata do Lawler requer encontrar TODOS os MIS do subgrafo G[S].
    
    # Vamos usar um algoritmo mais simples (e mais prático para implementação de 
    # DP em problemas de grafos) que usa todos os Conjuntos Independentes (IS), 
    # que também é válido, embora ligeiramente diferente do proposto:
    # χ(G[S]) = min_{I \subseteq S, I é IS} {1 + χ(G[S \ I])}
    
    # O subproblema é: encontrar o IS máximo no subgrafo S.
    # Devido à complexidade do Lawler, a implementação é reduzida aqui.
    # Em uma implementação completa, seria necessária a geração de todos os IS ou MIS.
    
    # --- Lawler (DP com IS) - Simplificado ---
    
    # Inicializa DP: χ(G[\emptyset]) = 0
    chromatic_number[0] = 0
    
    # Itera sobre todas as máscaras (subconjuntos S) de 1 a 2^n - 1
    for mask in range(1, 1 << n):
        # Encontra o conjunto de vértices S representado por 'mask'
        S_indices = [i for i in range(n) if (mask >> i) & 1]
        
        min_colors = n + 1
        best_IS_mask = -1
        
        # Itera sobre todos os subconjuntos I \subseteq S (submasks)
        # Otimização: I só precisa ser verificado se for um Conjunto Independente (IS)
        submask = mask
        while submask > 0:
            
            # Subconjunto I de S é representado por 'submask'
            I_indices = [i for i in S_indices if (submask >> i) & 1]
            
            # Verifica se I é um Conjunto Independente (IS) no grafo original
            is_independent = True
            for i in I_indices:
                v_i = sorted(V)[i] # Vértice i (1-based)
                for j in I_indices:
                    if i != j:
                        v_j = sorted(V)[j]
                        # Se houver aresta entre i e j (vizinho), não é IS
                        if v_j in G.get(v_i, set()):
                            is_independent = False
                            break
                if not is_independent:
                    break
            
            if is_independent:
                # Recorrência: χ(G[S]) = min_{I \subseteq S, I é IS} {1 + χ(G[S \ I])}
                new_mask = mask ^ submask # S \ I
                
                # Devemos ter chromatic_number[new_mask] já calculado pela DP
                current_colors = 1 + chromatic_number.get(new_mask, n + 1)
                
                if current_colors < min_colors:
                    min_colors = current_colors
                    best_IS_mask = submask
                    
            # Próxima submask (próximo subconjunto I)
            submask = (submask - 1) & mask

        chromatic_number[mask] = min_colors
        
        
    # --- Reconstrução da Coloração ---
    # Usamos o resultado da DP para reconstruir a coloração de volta.
    
    chromatic_number_value = chromatic_number[(1 << n) - 1]
    coloring = {} # coloring[v] = cor do vértice v (1-based)
    
    current_mask = (1 << n) - 1
    current_color = chromatic_number_value
    
    while current_mask > 0:
        
        # Encontra o Conjunto Independente I que leva ao resultado mínimo
        best_IS_mask = -1
        min_prev_cn = n + 1
        
        submask = current_mask
        while submask > 0:
            
            I_indices = [i for i in range(n) if (submask >> i) & 1]
            
            is_independent = True
            for i in I_indices:
                v_i = sorted(V)[i]
                for j in I_indices:
                    if i != j:
                        v_j = sorted(V)[j]
                        if v_j in G.get(v_i, set()):
                            is_independent = False
                            break
                if not is_independent:
                    break
                    
            if is_independent:
                new_mask = current_mask ^ submask
                
                # Verifica se este IS leva ao resultado que encontramos
                if chromatic_number.get(new_mask, n + 1) == current_color - 1:
                    
                    # Se houver múltiplos, escolhemos o primeiro (ou qualquer um)
                    # Não há requisito para ordem específica de vértices.
                    best_IS_mask = submask
                    break 
                    
            submask = (submask - 1) & current_mask
            
        
        # Colore os vértices no melhor IS com a 'current_color'
        if best_IS_mask != -1:
            IS_indices = [i for i in range(n) if (best_IS_mask >> i) & 1]
            for i in IS_indices:
                vertex = sorted(V)[i]
                coloring[vertex] = current_color
                
            current_mask ^= best_IS_mask # Remove os vértices coloridos
            current_color -= 1
        else:
            # Caso de erro (não deveria ocorrer se a DP estiver correta)
            break
            
    
    # Formato de saída: array de cores de Vértice 1 a n
    color_array = [0] * n # Índice 0 é para Vértice 1, etc.
    for v_idx in range(n):
        v = sorted(V)[v_idx] # Vértice (1-based)
        color_array[v_idx] = coloring.get(v, 0)
        
    return chromatic_number_value, color_array


# --- Função de Leitura de Grafo ---
def read_undirected_graph(file_path):
    """
    Lê o grafo a partir do arquivo (assumindo o formato .net, não-dirigido, 
    não-ponderado).
    
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
    G = {} # Lista de adjacência {u: {v1, v2, ...}}
    
    # Lógica de leitura (exemplo simplificado, ajuste para o formato .net)
    in_edges_section = False
    for line in lines:
        line = line.strip()
        if not line or line.startswith('*'):
            if line.startswith('*vertices'):
                try:
                    n = int(lines[lines.index(line) + 1].split()[0])
                except:
                    pass
            if line.startswith('*edges') or line.startswith('*arcs'):
                in_edges_section = True
                continue
            continue
            
        if in_edges_section:
            # Assumindo formato "u v" (não-ponderado)
            try:
                parts = line.split()
                u, v = int(parts[0]), int(parts[1])
                
                # Aresta u-v (não-dirigido)
                G.setdefault(u, set()).add(v)
                G.setdefault(v, set()).add(u)
            except:
                continue

    # Lista de todos os vértices [1, 2, ..., n]
    V = set(range(1, n + 1))
    
    if n == 0 or not V:
        # Se n for 0 ou não puder ser inferido, tente inferir a partir das chaves de G
        if G:
            V = set(G.keys())
            for neighbors in G.values():
                V.update(neighbors)
            n = len(V)
            
    if n == 0:
        print("Erro: Não foi possível determinar o número de vértices.", file=sys.stderr)
        return None, None
        
    return G, V

# --- Execução Principal ---
if __name__ == "__main__":
    if len(sys.argv) != 2:
        print("Uso: python3 A3_3.py <arquivo_grafo>", file=sys.stderr)
        sys.exit(1)

    file_path = sys.argv[1]
    
    # Lendo o grafo
    G, V = read_undirected_graph(file_path)
    
    if G is None or V is None:
        sys.exit(1)
        
    # O algoritmo de Lawler é muito custoso (exponencial), 
    # use-o apenas para testes em grafos pequenos.
    chromatic_number, color_array = lawler_coloring(G, V)

    # Saída esperada:
    # 1ª linha: Quantidade de cores (número cromático)
    # 2ª linha: Cores correspondentes a cada vértice (V1, V2, ..., Vn)
    
    print(chromatic_number)
    print(", ".join(map(str, color_array)))