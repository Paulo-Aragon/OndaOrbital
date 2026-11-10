package jogo;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 * Ponto de entrada: cria a janela (JFrame) e coloca o painel do jogo dentro dela.
 */
public class Main {

    public static void main(String[] args) {
        // Toda criação de interface Swing deve acontecer na thread de eventos (EDT).
        SwingUtilities.invokeLater(() -> {
            JFrame janela = new JFrame(Config.TITULO);
            GamePanel painel = new GamePanel();

            janela.setIconImages(Icone.criarTodos());
            janela.add(painel);
            janela.pack();
            janela.setResizable(false);
            janela.setLocationRelativeTo(null);
            janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            janela.setVisible(true);

            painel.requestFocusInWindow();
        });
    }
}
