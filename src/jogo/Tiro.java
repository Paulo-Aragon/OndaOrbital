package jogo;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Projétil disparado pelo jogador (sobe) ou por um inimigo (desce).
 */
public class Tiro extends Entidade {

    private final double velocidadeY;
    private final boolean doJogador;

    public Tiro(double x, double y, double velocidadeY, boolean doJogador) {
        super(x, y, doJogador ? 4 : 6, doJogador ? 14 : 10);
        this.velocidadeY = velocidadeY;
        this.doJogador = doJogador;
    }

    @Override
    public void atualizar() {
        y += velocidadeY;
        // Saiu da área de jogo: some.
        if (y + altura < 0 || y > Config.AREA_JOGO) {
            desativar();
        }
    }

    public boolean isDoJogador() {
        return doJogador;
    }

    @Override
    public void desenhar(Graphics2D g) {
        if (doJogador) {
            g.setColor(Color.YELLOW);
            g.fillRect((int) x, (int) y, largura, altura);
        } else {
            g.setColor(new Color(255, 80, 60));
            g.fillOval((int) x, (int) y, largura, altura);
        }
    }
}
