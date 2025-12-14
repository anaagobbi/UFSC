:- use_module(library(clpfd)).
:- use_module(library(lists)).

% ============================
% Puzzle: Lateinische Summen Nr. 51
% ============================
initial_board([
  [0, 11,  9,  0, 14,  0,  0,   0],
  [ 0,  0, 0, 17,  0,  0, 18,  12],
  [0, 17, 18,  0,  0, 14,  0,   0],
  [12,  0, 16,  0, 16,  0,  0,  0],
  [ 0,  0,  0, 15,  0, 16,  0,  9],
  [12,  0,  0, 0, 10,  0, 18,   0],
  [ 0, 20,  0, 20,  0, 16,  0,  0],
  [ 6,  0,  0,  0,  0, 0, 11,   9]
]).

% ============================
% Configuração Automática
% ============================
latin_size(N) :-
    initial_board(B),
    nth0(0,B,Row0),
    include(=(0), Row0, Zeros),
    length(Zeros, N).

dimension(D) :- initial_board(B), length(B, D).
dim_minus_1(D1) :- dimension(D), D1 is D - 1.

collect_white_positions(WhitePos) :-
    dim_minus_1(D1),
    initial_board(B),
    findall((R,C),
        ( between(0,D1,R),
          between(0,D1,C),
          nth0(R,B,Row),
          nth0(C,Row,Cell),
          Cell =:= 0
        ),
        WhitePos).

index_of_pos(Pos, List, Index) :- nth0(Index, List, Pos).
get_var_by_idx(Vars, I, V) :- nth0(I, Vars, V).

% ============================
% Agrupamento (Linhas/Colunas)
% ============================
vars_by_row(WhitePos, WhiteVars, RowsVars) :-
    dim_minus_1(D1),
    numlist(0, D1, Range),
    maplist(get_row_vars(WhitePos, WhiteVars), Range, RowsVars).

get_row_vars(WhitePos, WhiteVars, R, RowVars) :-
    findall(I, nth0(I, WhitePos, (R,_)), Indices),
    maplist(get_var_by_idx(WhiteVars), Indices, RowVars).

vars_by_col(WhitePos, WhiteVars, ColsVars) :-
    dim_minus_1(D1),
    numlist(0, D1, Range),
    maplist(get_col_vars(WhitePos, WhiteVars), Range, ColsVars).

get_col_vars(WhitePos, WhiteVars, C, ColVars) :-
    findall(I, nth0(I, WhitePos, (_,C)), Indices),
    maplist(get_var_by_idx(WhiteVars), Indices, ColVars).

% ============================
% Mapa de Variáveis
% ============================
build_pos_var_map([], [], []).
build_pos_var_map([Pos|Ps], [V|Vs], [Pos-V|Map]) :-
    build_pos_var_map(Ps, Vs, Map).

% ============================
% Coleta Segura de Vizinhos
% ============================
neighbor_offsets([
    (-1,-1),(-1,0),(-1,1),
    (0,-1),        (0,1),
    (1,-1),(1,0),(1,1)
]).

collect_neighbors_safe(_, _, [], _, []).
collect_neighbors_safe(R, C, [(DR,DC)|RestOps], PosVarMap, Neighbors) :-
    NR is R + DR,
    NC is C + DC,
    ( member((NR,NC)-Var, PosVarMap) ->
        Neighbors = [Var | RestN],
        collect_neighbors_safe(R, C, RestOps, PosVarMap, RestN)
    ;
        collect_neighbors_safe(R, C, RestOps, PosVarMap, Neighbors)
    ).

get_real_neighbors(R, C, PosVarMap, NeighVars) :-
    neighbor_offsets(Offsets),
    collect_neighbors_safe(R, C, Offsets, PosVarMap, NeighVars).

% ============================
% Restrições (CORREÇÃO DO FORALL AQUI)
% ============================
apply_constraints(WhitePos, WhiteVars) :-
    vars_by_row(WhitePos, WhiteVars, RowsVars),
    maplist(apply_domain_and_distinct, RowsVars),

    vars_by_col(WhitePos, WhiteVars, ColsVars),
    maplist(apply_domain_and_distinct, ColsVars).

apply_domain_and_distinct(Vars) :-
    latin_size(N),
    Vars ins 1..N,
    all_distinct(Vars).

% Predicado recursivo para aplicar restrições sem perder o estado
apply_recursive_sum_constraints([], _).
apply_recursive_sum_constraints([black(R,C,Cell)|Rest], PosVarMap) :-
    get_real_neighbors(R, C, PosVarMap, NeighVars),
    ( NeighVars = [] -> true ; sum(NeighVars, #=, Cell) ),
    apply_recursive_sum_constraints(Rest, PosVarMap).

apply_black_sum_constraints(WhitePos, WhiteVars) :-
    build_pos_var_map(WhitePos, WhiteVars, PosVarMap),
    initial_board(B),
    dim_minus_1(D1),
    
    % Passo 1: Coletamos todas as células pretas numa lista (sem aplicar regra ainda)
    findall(black(R,C,Cell),
        ( between(0,D1,R), between(0,D1,C),
          nth0(R,B,Row), nth0(C,Row,Cell),
          Cell > 0
        ),
        BlackCells),
        
    % Passo 2: Aplicamos a lista recursivamente (isso fixa as restrições na memória)
    apply_recursive_sum_constraints(BlackCells, PosVarMap).

% ============================
% Impressão
% ============================
reconstruct_board(InitialBoard, WhiteCellsPos, WhiteVars, SolvedBoard) :-
    dim_minus_1(D1),
    findall(RowSolved,
        ( between(0,D1,R),
          findall(CellSolved,
            ( between(0,D1,C),
              ( index_of_pos((R,C), WhiteCellsPos, I)
                -> nth0(I, WhiteVars, V), CellSolved = white(V)
                ; nth0(R, InitialBoard, Row0), nth0(C, Row0, V), CellSolved = black(V)
              )
            ),
            RowSolved
          )
        ),
        SolvedBoard).

print_board(Board) :-
    nl, write('-----------------------------------------'), nl,
    forall(member(Row,Board),
        ( forall(member(Cell, Row),
            ( Cell = black(V) -> format(' [~|~`0t~d~2+] ', [V])
            ; Cell = white(V) -> format('  ~|~` t~d~2+  ', [V])
            )),
          nl
        )),
    write('-----------------------------------------'), nl.

% ============================
% Main
% ============================
lateinische_solve(SolvedBoard) :-
    initial_board(B),
    collect_white_positions(WhitePos),
    length(WhitePos, Nvars),
    length(WhiteVars, Nvars),

    latin_size(N),
    WhiteVars ins 1..N,

    apply_constraints(WhitePos, WhiteVars),
    % AQUI: A nova chamada sem forall
    apply_black_sum_constraints(WhitePos, WhiteVars),

    labeling([ff], WhiteVars),
    reconstruct_board(B, WhitePos, WhiteVars, SolvedBoard).

solve :-
    ( lateinische_solve(Solved) ->
        writeln('Solução encontrada:'), print_board(Solved)
    ; writeln('Nenhuma solução encontrada.')
    ).