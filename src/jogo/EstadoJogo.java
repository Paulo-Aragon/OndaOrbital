package jogo;

/**
 * Os possíveis estados da partida. O GamePanel decide o que atualizar e
 * desenhar de acordo com o estado atual.
 */
public enum EstadoJogo {
    MENU,
    TRANSICAO,
    JOGANDO,
    PAUSADO,
    GAME_OVER,
    VITORIA
}
