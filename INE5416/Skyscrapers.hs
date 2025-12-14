-- Trabalho I – Programação Funcional (Haskell)
-- Puzzle escolhido: Wolkenkratzer (Skyscrapers) 6x6
-- Estratégia: backtracking + checagens de pistas por linha e coluna.
-- Entrada: pistas embutidas no código (permitido no enunciado).
-- Como trocar o tabuleiro? Altere 'exampleN' e as listas exTop/exBottom/exLeft/exRight.

import Data.List (permutations, transpose)
-- 'permutations' gera todas as permutações possíveis de uma lista (usado para criar linhas candidatas).
-- 'transpose' inverte linhas e colunas (usado para checar restrições por coluna).

-- Tipo que representa as pistas do puzzle.
-- Cada pista pode ou não estar presente, por isso usa-se 'Maybe Int':
--  Just k → existe pista com valor k
--  Nothing → sem pista
type Clues = ([Maybe Int], [Maybe Int], [Maybe Int], [Maybe Int])
-- A tupla representa as pistas (topo, fundo, esquerda, direita), cada uma sendo uma lista de tamanho N.

-- ==========================================================
-- Função que conta quantos prédios são visíveis da esquerda.
-- ==========================================================
visibleCount :: [Int] -> Int
visibleCount xs = go xs 0 0
  where
    go [] _ cnt = cnt                                   -- Caso base: lista vazia, retorna a contagem acumulada.
    go (y:ys) highest cnt
      | y > highest = go ys y (cnt + 1)                 -- Novo prédio mais alto visível → incrementa a contagem.
      | otherwise   = go ys highest cnt                 -- Caso contrário, prédio escondido atrás de outro maior.

-- ==========================================================
-- Verifica se uma linha respeita as pistas da esquerda e direita.
-- ==========================================================
okRowClues :: Maybe Int -> Maybe Int -> [Int] -> Bool
okRowClues mLeft mRight row =
  -- Se não há pista (Nothing), retorna True.
  -- Se há pista (Just k), compara o número de prédios visíveis com 'k'.
  maybe True (== visibleCount row) mLeft &&
  maybe True (== visibleCount (reverse row)) mRight

-- ==========================================================
-- Verifica parcialmente se uma coluna não ultrapassa a pista do topo.
-- (Poda usada durante o backtracking para evitar caminhos impossíveis.)
-- ==========================================================
okPartialTop :: Maybe Int -> [Int] -> Bool
okPartialTop Nothing _    = True                        -- Se não há pista no topo, é sempre válido.
okPartialTop (Just t) col = visibleCount col <= t        -- Se já excede o máximo visível, poda (False).

-- ==========================================================
-- Verifica se uma coluna completa respeita as pistas de topo e fundo.
-- ==========================================================
okColClues :: Maybe Int -> Maybe Int -> [Int] -> Bool
okColClues mTop mBottom col =
  maybe True (== visibleCount col) mTop &&               -- Checa do topo.
  maybe True (== visibleCount (reverse col)) mBottom     -- Checa do fundo (coluna invertida).

-- ==========================================================
-- Função principal de resolução (backtracking)
-- ==========================================================
solve :: Int -> Clues -> Maybe [[Int]]
solve n (tops, bottoms, lefts, rights) =
  backtrack 0 []                                         -- Começa o processo recursivo na linha 0 com acumulador vazio.
  where
    allRows = permutations [0..n-1]                      -- Gera todas as permutações possíveis de uma linha (0 a n-1).

    -- Função recursiva que tenta preencher linha por linha.
    backtrack :: Int -> [[Int]] -> Maybe [[Int]]
    backtrack r acc
      | r == n =                                          -- Caso base: todas as linhas foram preenchidas.
          let grid = reverse acc                          -- Reverte o acumulador (pois as linhas foram empilhadas ao contrário).
              cols = transpose grid                       -- Transforma linhas em colunas.
              colsOk = and [ okColClues (tops!!c) (bottoms!!c) (cols!!c) | c <- [0..n-1] ]
                                                           -- Verifica todas as colunas contra as pistas topo/fundo.
          in if colsOk then Just grid else Nothing         -- Retorna o grid se válido, senão Nothing.
      | otherwise = go candidates                         -- Caso recursivo: tenta preencher a próxima linha.
      where
        -- Gera todas as linhas candidatas para a linha r, aplicando podas.
        candidates =
          [ row
          | row <- allRows
          , okRowClues (lefts!!r) (rights!!r) row          -- Verifica se a linha respeita as pistas esquerda/direita.
          , columnsOkSoFar row acc                         -- Verifica restrições parciais de coluna.
          ]

        -- Tenta cada linha candidata recursivamente.
        go :: [[Int]] -> Maybe [[Int]]
        go []     = Nothing                               -- Nenhum candidato leva a solução.
        go (x:xs) =
          case backtrack (r+1) (x:acc) of                 -- Tenta adicionar 'x' e prossegue para a próxima linha.
            Just sol -> Just sol                          -- Se encontrou solução, retorna.
            Nothing  -> go xs                             -- Caso contrário, tenta o próximo candidato.

    -- ======================================================
    -- Verifica se, ao adicionar uma nova linha, as colunas
    -- ainda respeitam as restrições (sem repetição e sem
    -- ultrapassar a pista do topo).
    -- ======================================================
    columnsOkSoFar :: [Int] -> [[Int]] -> Bool
    columnsOkSoFar newRow currentRows =
      and [ okCol j | j <- [0..n-1] ]
      where
        okCol j =
          let colSoFarTopDown = map (!! j) (reverse currentRows)  -- Coluna atual já construída (do topo para baixo).
              v               = newRow !! j                       -- Valor novo que será adicionado.
              noRepeat        = v `notElem` colSoFarTopDown       -- Garante que não há repetição na coluna.
              partialTopDown  = colSoFarTopDown ++ [v]            -- Coluna atualizada com o novo valor.
          in noRepeat && okPartialTop (tops!!j) partialTopDown    -- Verifica unicidade e restrição parcial do topo.

-- ==========================================================
-- Exemplo 6x6 (pistas de exemplo com solução conhecida)
-- ==========================================================
-- Solução esperada:
-- 3 0 1 2 5 4
-- 4 1 2 3 0 5
-- 0 3 4 5 2 1
-- 1 4 5 0 3 2
-- 2 5 0 1 4 3
-- 5 2 3 4 1 0

exampleN :: Int
exampleN = 6                                             -- Tamanho do tabuleiro (6x6).

-- Pistas de cada direção (topo, fundo, esquerda, direita).
exTop, exBottom, exLeft, exRight :: [Maybe Int]
exTop    = map Just [3,4,4,3,1,2]
exBottom = map Just [1,2,2,2,3,2]
exLeft   = map Just [2,2,3,3,2,1]
exRight  = map Just [2,1,3,3,3,3]

-- Agrupa todas as pistas em uma única tupla.
exampleClues :: Clues
exampleClues = (exTop, exBottom, exLeft, exRight)

-- ==========================================================
-- Função de impressão da grade (tabuleiro final).
-- ==========================================================
printGrid :: [[Int]] -> IO ()
printGrid g = do
  putStrLn ""                                           -- Linha em branco antes.
  mapM_ (putStrLn . unwords . map show) g               -- Converte os números para texto e imprime linha por linha.
  putStrLn ""                                           -- Linha em branco depois.

-- ==========================================================
-- Função principal (ponto de entrada do programa).
-- ==========================================================
main :: IO ()
main = do
  putStrLn "Skyscrapers 6x6 – backtracking em Haskell"  -- Mensagem inicial.
  case solve exampleN exampleClues of                   -- Chama o resolvedor com as pistas de exemplo.
    Just grid -> do
      putStrLn "Solução encontrada:"                    -- Caso tenha solução:
      printGrid grid                                    -- Imprime o tabuleiro resolvido.
    Nothing ->
      putStrLn "Nenhuma solução encontrada (verifique as pistas)." -- Caso não exista solução possível.
