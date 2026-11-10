package jogo;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Anda na horizontal ondulando para cima e para baixo. Ao sair por um lado
 * da tela, reaparece do outro.
 */
public class InimigoZigueZague extends Inimigo {

    private static final Color COR = new Color(80, 220, 120);

    private final double baseY;
    private final int direcao;
    private final double fase;

    public InimigoZigueZague(double x, double y, double velocidade, int direcao) {
        super(x, y, 32, 22, velocidade, 20);
        this.baseY = y;
        this.direcao = direcao;
        this.fase = x * 0.02;
    }

    @Override
    protected void mover() {
        x += velocidade * direcao;
        y = baseY + Math.sin(tempo * 0.08 + fase) * 14;

        // Atravessa as bordas da tela.
        if (x > Config.LARGURA) {
            x -= Config.LARGURA + largura;
        } else if (x < -largura) {
            x += Config.LARGURA + largura;
        }
    }

    @Override
    public String getNome() {
        return "Zigue-zague";
    }

    @Override
    public void desenhar(Graphics2D g) {
        int cx = (int) x;
        int cy = (int) y;
        g.setColor(COR);
        g.fillPolygon(
                new int[] { cx + 16, cx + 32, cx + 16, cx },
                new int[] { cy, cy + 11, cy + 22, cy + 11 },
                4);
        g.setColor(Color.BLACK);
        g.fillRect(cx + 10, cy + 8, 4, 4);
        g.fillRect(cx + 18, cy + 8, 4, 4);
    }
}
