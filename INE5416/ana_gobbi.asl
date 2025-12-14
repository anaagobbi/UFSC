// Agent Ana in project stones
// Aluna: Ana Gobbi
// Estratégia Misere Nim - implementação simplificada para Jason
// https://www.youtube.com/watch?v=LSliURTIL8U

!start.

/* Plans */

+!start : true
    <- .print("hello world.");
       .date(Y,M,D); .time(H,Min,Sec,MilSec);
       +started(Y,M,D,H,Min,Sec).

/* Percepts */

+start: .my_name(Me) & .term2string(Me, MeStr) & player(MeStr) <- 
    .print("Percebi que o jogo iniciou e sou um jogador.").

// Quando é a vez da Ana jogar
+round(N, WhoPlays): .my_name(Me) & .term2string(Me, WhoPlays) <- 
    .print("It's round ", N, " I'm the player");
    !jogar_ana.

// Quando não é a vez da Ana
+round(N, WhoPlays): .my_name(Me) & .term2string(Me, MeStr) & MeStr \== WhoPlays.

// Estratégia Misere Nim: remove 1 pedra de qualquer torre disponível
+!jogar_ana : tower(T, S) & S > 0
    <- .print("Ana: Removendo 1 pedra da torre ", T);
       play(T, 1).

{ include("$jacamo/templates/common-cartago.asl") }
{ include("$jacamo/templates/common-moise.asl") }
