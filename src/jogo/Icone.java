package jogo;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Desenha o ícone do jogo via código (Graphics2D), sem precisar de arquivos de imagem.
 * Usado na barra de título/barra de tarefas e pelo gerador do ícone do executável.
 */
public final class Icone {

    /** Tamanhos padrão de ícone usados pelo Windows. */
    public static final int[] TAMANHOS = { 16, 24, 32, 48, 64, 128, 256 };

    private Icone() {
    }

    public static List<Image> criarTodos() {
        List<Image> lista = new ArrayList<>();
        for (int tamanho : TAMANHOS) {
            lista.add(criar(tamanho));
        }
        return lista;
    }

    /** Cria o ícone no tamanho pedido. O desenho é feito numa grade de 256x256 e escalado. */
    public static BufferedImage criar(int tamanho) {
        BufferedImage imagem = new BufferedImage(tamanho, tamanho, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = imagem.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.scale(tamanho / 256.0, tamanho / 256.0);

        // Fundo: quadrado arredondado com degradê espacial e borda ciano.
        RoundRectangle2D fundo = new RoundRectangle2D.Double(8, 8, 240, 240, 56, 56);
        g.setPaint(new GradientPaint(0, 0, new Color(16, 14, 60), 0, 256, new Color(4, 4, 20)));
        g.fill(fundo);
        g.setStroke(new BasicStroke(8));
        g.setColor(new Color(90, 200, 255));
        g.draw(fundo);

        // Estrelas (omitidas nos tamanhos muito pequenos para não virar "sujeira").
        if (tamanho >= 32) {
            g.setColor(new Color(220, 220, 255));
            int[][] estrelas = { { 40, 40 }, { 200, 52 }, { 70, 120 }, { 210, 150 }, { 46, 190 }, { 160, 96 },
                    { 120, 34 } };
            for (int[] e : estrelas) {
                g.fillRect(e[0], e[1], 6, 6);
            }
        }

        // Inimigo zigue-zague no alto.
        g.setColor(new Color(80, 220, 120));
        g.fillPolygon(new int[] { 128, 176, 128, 80 }, new int[] { 40, 72, 104, 72 }, 4);
        g.setColor(Color.BLACK);
        g.fillRect(108, 64, 12, 12);
        g.fillRect(136, 64, 12, 12);

        // Tiro amarelo subindo.
        g.setColor(Color.YELLOW);
        g.fillRect(122, 118, 12, 34);

        // Chama do motor.
        g.setColor(Color.ORANGE);
        g.fillPolygon(new int[] { 108, 148, 128 }, new int[] { 210, 210, 244 }, 3);
        g.setColor(Color.YELLOW);
        g.fillPolygon(new int[] { 118, 138, 128 }, new int[] { 210, 210, 230 }, 3);

        // Nave do jogador (mesmo formato da classe Nave, ampliado).
        g.setColor(new Color(90, 200, 255));
        g.fillPolygon(
                new int[] { 128, 200, 156, 100, 56 },
                new int[] { 160, 232, 212, 212, 232 },
                5);
        g.setColor(Color.WHITE);
        g.fillOval(116, 182, 24, 24);

        g.dispose();
        return imagem;
    }
}
