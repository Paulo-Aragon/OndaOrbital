package jogo;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Energia da nave: diminui com o tempo. Se zerar antes de a onda acabar,
 * o jogador perde uma vida. A energia que sobra no fim da onda vira bônus.
 */
public class BarraDeEnergia {

    private static final double MAXIMO = 100;

    private final double consumoPorQuadro;
    private double atual = MAXIMO;

    public BarraDeEnergia(double consumoPorQuadro) {
        this.consumoPorQuadro = consumoPorQuadro;
    }

    public void atualizar() {
        atual = Math.max(0, atual - consumoPorQuadro);
    }

    public void recarregar() {
        atual = MAXIMO;
    }

    public boolean isEsgotada() {
        return atual <= 0;
    }

    public double getAtual() {
        return atual;
    }

    public double getPercentual() {
        return atual / MAXIMO;
    }

    public void desenhar(Graphics2D g, int x, int y, int largura, int altura) {
        double pct = getPercentual();
        Color cor;
        if (pct > 0.5) {
            cor = new Color(80, 220, 100);
        } else if (pct > 0.25) {
            cor = new Color(240, 210, 60);
        } else {
            cor = new Color(240, 70, 60);
        }
        g.setColor(new Color(40, 40, 40));
        g.fillRect(x, y, largura, altura);
        g.setColor(cor);
        g.fillRect(x, y, (int) (largura * pct), altura);
        g.setColor(Color.GRAY);
        g.drawRect(x, y, largura, altura);
    }
}
