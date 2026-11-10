package jogo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

/**
 * Painel de informações na parte de baixo da tela: pontos, recorde,
 * onda atual, vidas restantes e barra de energia.
 */
public class HUD {

    private static final Font FONTE = new Font(Font.MONOSPACED, Font.BOLD, 16);

    public void desenhar(Graphics2D g, int pontos, int recorde, int onda, int vidas, BarraDeEnergia energia) {
        int topo = Config.AREA_JOGO;

        g.setColor(new Color(10, 10, 30));
        g.fillRect(0, topo, Config.LARGURA, Config.ALTURA - topo);
        g.setColor(new Color(90, 200, 255));
        g.drawLine(0, topo, Config.LARGURA, topo);

        g.setFont(FONTE);
        g.setColor(Color.WHITE);
        g.drawString(String.format("PONTOS %06d", pontos), 20, topo + 22);
        g.drawString(String.format("RECORDE %06d", recorde), 210, topo + 22);
        g.drawString(String.format("ONDA %d/%d", onda, GerenciadorDeOndas.TOTAL_ONDAS), 430, topo + 22);

        // Vidas como pequenas naves
        g.drawString("VIDAS", 580, topo + 22);
        g.setColor(new Color(90, 200, 255));
        for (int i = 0; i < vidas; i++) {
            int vx = 645 + i * 24;
            int vy = topo + 8;
            g.fillPolygon(new int[] { vx + 8, vx + 16, vx }, new int[] { vy, vy + 14, vy + 14 }, 3);
        }

        g.setColor(Color.WHITE);
        g.drawString("ENERGIA", 20, topo + 48);
        energia.desenhar(g, 110, topo + 36, Config.LARGURA - 130, 14);
    }
}
