package jogo;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Random;

/**
 * Fundo animado com estrelas descendo em velocidades diferentes (efeito de profundidade).
 */
public class FundoEstrelado {

    private final Random random = new Random();
    private final double[] xs;
    private final double[] ys;
    private final double[] velocidades;

    public FundoEstrelado(int quantidade) {
        xs = new double[quantidade];
        ys = new double[quantidade];
        velocidades = new double[quantidade];
        for (int i = 0; i < quantidade; i++) {
            xs[i] = random.nextInt(Config.LARGURA);
            ys[i] = random.nextInt(Config.AREA_JOGO);
            velocidades[i] = 0.3 + random.nextDouble() * 1.5;
        }
    }

    public void atualizar() {
        for (int i = 0; i < ys.length; i++) {
            ys[i] += velocidades[i];
            if (ys[i] > Config.AREA_JOGO) {
                ys[i] = 0;
                xs[i] = random.nextInt(Config.LARGURA);
            }
        }
    }

    public void desenhar(Graphics2D g) {
        for (int i = 0; i < xs.length; i++) {
            // Estrelas mais rápidas são mais brilhantes e maiores.
            int brilho = (int) (90 + velocidades[i] / 1.8 * 165);
            g.setColor(new Color(brilho, brilho, brilho));
            int tamanho = velocidades[i] > 1.2 ? 2 : 1;
            g.fillRect((int) xs[i], (int) ys[i], tamanho, tamanho);
        }
    }
}
