package jogo;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Cai do alto da tela balançando de um lado para o outro. Não atira, mas
 * destrói a nave se encostar nela. Ao passar do fundo, reaparece no topo.
 */
public class InimigoCadente extends Inimigo {

    private static final Color COR = new Color(140, 200, 255);
    private static final int MARGEM = 50;

    private double baseX;
    private final double fase;

    public InimigoCadente(double x, double y, double velocidade) {
        super(x, y, 26, 26, velocidade, 50);
        this.baseX = x;
        this.fase = RANDOM.nextDouble() * Math.PI * 2;
    }

    @Override
    protected void mover() {
        y += velocidade;
        x = baseX + Math.sin(tempo * 0.05 + fase) * 40;

        if (y > Config.AREA_JOGO) {
            y = -altura - RANDOM.nextInt(150);
            baseX = MARGEM + RANDOM.nextInt(Config.LARGURA - 2 * MARGEM - largura);
        }
    }

    @Override
    public boolean podeAtirar() {
        return false;
    }

    @Override
    public String getNome() {
        return "Cadente";
    }

    @Override
    public void desenhar(Graphics2D g) {
        // Estrela de 8 pontas que gira enquanto cai.
        double centroX = x + largura / 2.0;
        double centroY = y + altura / 2.0;
        int pontas = 8;
        int[] xs = new int[pontas * 2];
        int[] ys = new int[pontas * 2];
        for (int i = 0; i < pontas * 2; i++) {
            double raio = (i % 2 == 0) ? 13 : 6;
            double angulo = tempo * 0.05 + i * Math.PI / pontas;
            xs[i] = (int) (centroX + Math.cos(angulo) * raio);
            ys[i] = (int) (centroY + Math.sin(angulo) * raio);
        }
        g.setColor(COR);
        g.fillPolygon(xs, ys, xs.length);
        g.setColor(Color.WHITE);
        g.fillOval((int) centroX - 3, (int) centroY - 3, 6, 6);
    }
}
