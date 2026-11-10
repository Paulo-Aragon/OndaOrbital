package jogo;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Move-se na diagonal e ricocheteia nas bordas da metade de cima da tela.
 */
public class InimigoRicochete extends Inimigo {

    private static final Color COR = new Color(230, 80, 230);
    private static final int TOPO = 30;
    private static final int LIMITE_INFERIOR = (int) (Config.AREA_JOGO * 0.65);

    private double vx;
    private double vy;

    public InimigoRicochete(double x, double y, double velocidade) {
        super(x, y, 28, 28, velocidade, 30);
        this.vx = RANDOM.nextBoolean() ? velocidade : -velocidade;
        this.vy = (RANDOM.nextBoolean() ? velocidade : -velocidade) * 0.7;
    }

    @Override
    protected void mover() {
        x += vx;
        y += vy;

        if (x < 0) {
            x = 0;
            vx = Math.abs(vx);
        } else if (x + largura > Config.LARGURA) {
            x = Config.LARGURA - largura;
            vx = -Math.abs(vx);
        }

        if (y < TOPO) {
            y = TOPO;
            vy = Math.abs(vy);
        } else if (y + altura > LIMITE_INFERIOR) {
            y = LIMITE_INFERIOR - altura;
            vy = -Math.abs(vy);
        }
    }

    @Override
    public String getNome() {
        return "Ricochete";
    }

    @Override
    public void desenhar(Graphics2D g) {
        int cx = (int) x;
        int cy = (int) y;
        // Octógono
        g.setColor(COR);
        g.fillPolygon(
                new int[] { cx + 8, cx + 20, cx + 28, cx + 28, cx + 20, cx + 8, cx, cx },
                new int[] { cy, cy, cy + 8, cy + 20, cy + 28, cy + 28, cy + 20, cy + 8 },
                8);
        // "Olho" que gira
        g.setColor(Color.WHITE);
        g.fillOval(cx + 8, cy + 8, 12, 12);
        g.setColor(Color.BLACK);
        int ox = (int) (Math.cos(tempo * 0.15) * 3);
        int oy = (int) (Math.sin(tempo * 0.15) * 3);
        g.fillOval(cx + 11 + ox, cy + 11 + oy, 6, 6);
    }
}
