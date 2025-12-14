;; Define o namespace principal do projeto.
(ns kakuro-solver.core
  (:gen-class))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 1. O "FISCAL" (Verificador de Regras)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Esta função (privada) verifica se o tabuleiro *atual* é válido
;; de acordo com *todas* as regras (clues) do puzzle.
(defn- is-valid?
  "Verifica se um dado tabuleiro obedece a todas as regras do Kakuro."
  [board clues] ;; Recebe o tabuleiro (mapa) e a lista de todas as regras.
  
  ;; (every? ...) significa: "Verifique se a função a seguir
  ;; retorna 'true' para CADA item (cada 'clue') na lista 'clues'".
  ;; Se UMA regra falhar, o 'every?' para e retorna 'false'.
  (every?
    (fn [clue] ;; Função anônima que roda para CADA regra.
      
      ;; Pega os dados de dentro da regra atual (que é um mapa).
      (let [sum-target (:sum clue)            ;; O alvo da soma (ex: 16)
            cells (:cells clue)            ;; A lista de células (ex: '([1 2] [1 3]))
            
            ;; Pega os valores atuais do tabuleiro para essas células.
            ;; Ex: (map board '([1 2] [1 3])) -> (2 nil) se [1 2] é 2 e [1 3] está vazia.
            values (map board cells)
            
            ;; Filtra apenas os números, removendo os 'nil' (células vazias).
            ;; Ex: (filter number? (2 nil)) -> (2)
            filled-values (filter number? values)]
        
        ;; Se nenhuma célula desta regra foi preenchida ainda,
        ;; ela é válida por padrão.
        (if (empty? filled-values)
          true
          
          ;; Se temos números, vamos checá-los:
          (let [;; Soma os números que já estão lá.
                current-sum (reduce + 0 filled-values)]
            
            ;; 'and' junta todas as 3 regras do Kakuro:
            (and
              ;; REGRA 1: Não pode ter números repetidos na mesma pista.
              (apply distinct? filled-values)
              
              ;; REGRA 2: A soma atual NÃO PODE ser MAIOR que o alvo.
              (<= current-sum sum-target)
              
              ;; REGRA 3: Se TODAS as células da pista estão cheias...
              (if (= (count filled-values) (count cells))
                ;; ...a soma DEVE SER EXATAMENTE IGUAL ao alvo.
                (= current-sum sum-target)
                ;; ...se não estão todas cheias, a regra 3 passa.
                true))))))
  
   clues)) ;; A lista 'clues' é o último argumento para o 'every?'

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 2. O "RESOLVEDOR" (Backtracking / Tentativa e Erro)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Esta é a função recursiva principal que resolve o puzzle.
(defn solve-kakuro
  "Tenta resolver o puzzle recursivamente usando backtracking."
  [board clues cells-to-fill]
  
  ;; 1. CASO BASE (O FIM DA RECURSÃO):
  ;; Se a lista de células para preencher está vazia, é porque
  ;; conseguimos preencher tudo! Retornamos o 'board' completo.
  (if (empty? cells-to-fill)
    board
    
    ;; 2. CASO RECURSIVO (O TRABALHO):
    ;; Se ainda temos células para preencher...
    (let [;; Pega a *primeira* célula da lista de tarefas.
          cell (first cells-to-fill)
          ;; Pega *todo o resto* da lista.
          remaining-cells (rest cells-to-fill)]
      
      ;; Inicia um loop para tentar os números de 1 a 9.
      (loop [n 1]
        ;; Continua apenas enquanto n <= 9
        (when (<= n 9)
          
          ;; Cria um *novo* tabuleiro de teste, colocando 'n' na 'cell' atual.
          (let [new-board (assoc board cell n)]
            
            ;; CHAMA O "FISCAL": Este movimento é válido?
            (if (is-valid? new-board clues)
              
              ;; Se for válido, tenta resolver o RESTO do puzzle.
              ;; Esta é a CHAMADA RECURSIVA (ir mais fundo).
              (if-let [solution (solve-kakuro new-board clues remaining-cells)]
                ;; Se a chamada recursiva achou uma solução,
                ;; retorne-a (passando-a para cima).
                solution
                
                ;; Se a chamada recursiva *falhou* (retornou nil),
                ;; significa que 'n' estava errado.
                ;; Faz o BACKTRACK: tenta o próximo número (n+1).
                (recur (inc n)))
              
              ;; Se o movimento NÃO foi válido (o 'is-valid?' deu false),
              ;; Faz o BACKTRACK: tenta o próximo número (n+1).
              (recur (inc n)))))))))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 3. OS DADOS (O Puzzle)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

;; Esta é a definição do puzzle, transcrita manualmente.
;; É uma lista (vetor) de mapas. Cada mapa é uma "regra" (clue).
;; Formato: {:sum X, :cells '(lista de coordenadas [linha coluna])}
(def puzzle-10x10-valido
  [
   ;; --- Pistas Horizontais (Across) ---
   {:sum 3, :cells '([1 2] [1 3])}
   {:sum 9, :cells '([1 7] [1 8])}
   {:sum 13, :cells '([2 1] [2 2] [2 3] [2 4])}
   {:sum 13, :cells '([2 6] [2 7] [2 8] [2 9])}
   {:sum 16, :cells '([3 1] [3 2])}
   {:sum 19, :cells '([3 4] [3 5] [3 6])}
   {:sum 17, :cells '([3 8] [3 9])}
   {:sum 4,  :cells '([4 2] [4 3])}
   {:sum 29, :cells '([4 5] [4 6] [4 7] [4 8])}
   {:sum 16, :cells '([5 3] [5 4] [5 5] [5 6] [5 7])}
   {:sum 10, :cells '([6 2] [6 3] [6 4] [6 5])}
   {:sum 16, :cells '([6 7] [6 8])}
   {:sum 16, :cells '([7 1] [7 2])}
   {:sum 16, :cells '([7 4] [7 5] [7 6])}
   {:sum 10, :cells '([7 8] [7 9])}
   {:sum 14, :cells '([8 1] [8 2] [8 3] [8 4])}
   {:sum 12, :cells '([8 6] [8 7] [8 8] [8 9])}
   {:sum 17, :cells '([9 2] [9 3])}
   {:sum 17, :cells '([9 7] [9 8])}

   ;; --- Pistas Verticais (Down) ---
   {:sum 11, :cells '([2 1] [3 1])}
   {:sum 13, :cells '([7 1] [8 1])}
   {:sum 13, :cells '([1 2] [2 2] [3 2] [4 2])}
   {:sum 18, :cells '([6 2] [7 2] [8 2] [9 2])}
   {:sum 4, :cells '([1 3] [2 3])}
   {:sum 7, :cells '([4 3] [5 3] [6 3])}
   {:sum 16, :cells '([8 3] [9 3])}
   {:sum 16,  :cells '([2 4] [3 4])}
   {:sum 10,  :cells '([5 4] [6 4] [7 4] [8 4])}
   {:sum 34, :cells '([3 5] [4 5] [5 5] [6 5] [7 5])}
   {:sum 11, :cells '([2 6] [3 6] [4 6] [5 6])}
   {:sum 4, :cells '([7 6] [8 6])}
   {:sum 3, :cells '([1 7] [2 7])}
   {:sum 19, :cells '([4 7] [5 7] [6 7])}
   {:sum 13, :cells '([8 7] [9 7])}
   {:sum 30, :cells '([1 8] [2 8] [3 8] [4 8])}
   {:sum 29, :cells '([6 8] [7 8] [8 8] [9 8])}
   {:sum 13, :cells '([2 9] [3 9])}
   {:sum 3, :cells '([7 9] [8 9])}
  ])

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 4. FUNÇÕES AJUDANTES (Helpers)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;

(defn get-all-cells
  "Pega a lista de regras e extrai uma lista única de todas as
  células brancas que precisam ser preenchidas."
  [clues]
  (->> clues ;; Pega a lista de 'clues'
       ;; Extrai todas as listas :cells e junta numa só.
       (mapcat :cells) 
       ;; Converte para um Set (conjunto) para remover duplicatas.
       (into #{})
       ;; Converte de volta para uma lista ordenada.
       (sort)))

;; Função para imprimir a solução final de forma bonita.
(defn print-solution
  "Imprime o tabuleiro final no console."
  [board width height] ;; Recebe a solução e as dimensões.
  
  (println "\n=== SOLUÇÃO ENCONTRADA ===")
  ;; Loop por cada linha (de 0 até height-1)
  (doseq [row (range height)]
    (print "  ") ;; Adiciona um recuo
    ;; Loop por cada coluna (de 0 até width-1)
    (doseq [col (range width)]
      
      ;; Tenta pegar o valor da coordenada [linha coluna] no mapa da solução
      (if-let [val (get board [row col])]
        ;; Se existir (for uma célula branca preenchida), imprime o número.
        (print (format "%2d " val))
        ;; Se for 'nil' (for uma célula preta), imprime um bloco.
        (print "██ ")))
    (println)) ;; Pula para a próxima linha
  (println "==========================\n"))

;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
;; 5. O PONTO DE PARTIDA (Main)
;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;;
  
;; Esta é a função que é executada quando você roda `lein run`.
(defn -main [& args]
  ;; Mudei o título para 10x10 para ficar correto.
  (println "TRABALHO II - RESOLVEDOR DE KAKURO 10x10")
  (println "===============================================")

  ;; Bloco de setup: prepara todas as variáveis.
  (let [
        ;; Carrega a definição do puzzle.
        clues puzzle-10x10-valido
        ;; Define as dimensões (só para a impressão).
        width 10
        height 10
        ;; Chama o 'get-all-cells' para saber quais células preencher.
        cells-to-fill (get-all-cells clues)
        ;; Cria o tabuleiro inicial (mapa vazio).
        empty-board {}]

    ;; Imprime informações úteis.
    (println "Estatísticas:")
    (println (format "- Células: %d" (count cells-to-fill)))
    (println (format "- Pistas: %d" (count clues)))
    (println "\nResolvendo... (Testando um puzzle válido)")

    ;; Bloco de execução: cronometra e roda o resolvedor.
    (let [;; Pega o tempo de início.
          start-time (System/currentTimeMillis)
          
          ;; !! A GRANDE CHAMADA !!
          ;; Chama o 'solve-kakuro' para começar a resolver.
          solution (solve-kakuro empty-board clues cells-to-fill)
          
          ;; Pega o tempo de fim e calcula a duração.
          end-time (System/currentTimeMillis)
          time-taken (/ (- end-time start-time) 1000.0)]

      ;; A execução terminou. 'solution' é o tabuleiro completo ou é 'nil'.
      (if solution
        ;; Se 'solution' não for 'nil', encontramos!
        (do
          ;; Imprime a solução formatada.
          (print-solution solution width height)
          (println (format "Tempo: %.3f segundos" time-taken))
          (println "✅ Puzzle resolvido com sucesso!"))
        
        ;; Se 'solution' for 'nil', não há solução.
        (println "❌ Nenhuma solução encontrada.")))))
