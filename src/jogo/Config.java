package jogo;

/**
 * Constantes globais do jogo (tamanho da tela, velocidade do loop etc.).
 * Classe utilitária: não pode ser instanciada.
 */
public final class Config {

    private Config() {
    }

    public static final String TITULO = "Onda Orbital";

    /** Largura e altura da janela, em pixels. */
    public static final int LARGURA = 800;
    public static final int ALTURA = 600;

    /** Linha que separa a área de jogo do painel de informações (HUD). */
    public static final int AREA_JOGO = 540;

    /** Intervalo do Timer do Swing (~60 quadros por segundo). */
    public static final int ATRASO_MS = 16;

    public static final int VIDAS_INICIAIS = 3;
}
