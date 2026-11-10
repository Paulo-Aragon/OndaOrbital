package jogo;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * A nave controlada pelo jogador. Só se move na horizontal, na parte de baixo da tela.
 */
public class Nave extends Entidade {

    private static final double VELOCIDADE = 6;
    private static final Color COR = new Color(90, 200, 255);

    private boolean movendoEsquerda;
    private boolean movendoDireita;
    private int ticksInvulneravel;
    private int animacao;

    public Nave() {
        super(0, Config.AREA_JOGO - 40, 40, 24);
        reposicionar();
    }

    @Override
    public void atualizar() {
        if (movendoEsquerda) {
            x -= VELOCIDADE;
        }
        if (movendoDireita) {
            x += VELOCIDADE;
        }
        // Mantém a nave dentro da tela.
        x = Math.max(0, Math.min(Config.LARGURA - largura, x));

        if (ticksInvulneravel > 0) {
            ticksInvulneravel--;
        }
        animacao++;
    }

    public Tiro atirar() {
        return new Tiro(getCentroX() - 2, y - 12, -10, true);
    }

    public void reposicionar() {
        x = (Config.LARGURA - largura) / 2.0;
        ativo = true;
    }

    public void tornarInvulneravel(int ticks) {
        ticksInvulneravel = ticks;
    }

    public boolean isInvulneravel() {
        return ticksInvulneravel > 0;
    }

    public void setMovendoEsquerda(boolean valor) {
        movendoEsquerda = valor;
    }

    public void setMovendoDireita(boolean valor) {
        movendoDireita = valor;
    }

    public void pararMovimento() {
        movendoEsquerda = false;
        movendoDireita = false;
    }

    @Override
    public void desenhar(Graphics2D g) {
        // Pisca enquanto está invulnerável.
        if (isInvulneravel() && (ticksInvulneravel / 6) % 2 == 0) {
            return;
        }
        int cx = (int) x;
        int cy = (int) y;

        // Chama do motor (alterna de tamanho para parecer animada).
        g.setColor(animacao % 6 < 3 ? Color.ORANGE : Color.YELLOW);
        g.fillRect(cx + 16, cy + 18, 8, animacao % 6 < 3 ? 8 : 5);

        g.setColor(COR);
        g.fillPolygon(
                new int[] { cx + 20, cx + 40, cx + 28, cx + 12, cx },
                new int[] { cy, cy + 24, cy + 18, cy + 18, cy + 24 },
                5);

        g.setColor(Color.WHITE);
        g.fillOval(cx + 16, cy + 8, 8, 8);
    }
}
