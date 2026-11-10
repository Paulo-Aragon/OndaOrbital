package jogo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Painel principal: contém o loop do jogo (javax.swing.Timer), trata o
 * teclado (KeyListener), aplica as regras e desenha tudo (paintComponent).
 */
public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int DURACAO_TRANSICAO = 120; // ~2 segundos
    private static final int DURACAO_MORTE = 90;
    private static final int TICKS_INVULNERAVEL = 120;
    private static final double CONSUMO_ENERGIA = 0.04; // ~40 segundos por onda

    private static final Font FONTE_TITULO = new Font(Font.MONOSPACED, Font.BOLD, 56);
    private static final Font FONTE_GRANDE = new Font(Font.MONOSPACED, Font.BOLD, 32);
    private static final Font FONTE_NORMAL = new Font(Font.MONOSPACED, Font.BOLD, 18);
    private static final Font FONTE_PEQUENA = new Font(Font.MONOSPACED, Font.PLAIN, 15);

    private final Timer timer;
    private final Random random = new Random();
    private final Nave nave = new Nave();
    private final List<Inimigo> inimigos = new ArrayList<>();
    private final List<Tiro> tiros = new ArrayList<>();
    private final List<Inimigo> exemplosMenu = new ArrayList<>();
    private final GerenciadorDeOndas gerenciador = new GerenciadorDeOndas();
    private final BarraDeEnergia energia = new BarraDeEnergia(CONSUMO_ENERGIA);
    private final FundoEstrelado fundo = new FundoEstrelado(90);
    private final HUD hud = new HUD();

    private EstadoJogo estado = EstadoJogo.MENU;
    private int pontos;
    private int recorde;
    private int vidas;
    private int onda;
    private int contadorTransicao;
    private boolean atirando;
    private String mensagem = "";
    private String submensagem = "";

    public GamePanel() {
        setPreferredSize(new Dimension(Config.LARGURA, Config.ALTURA));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        criarExemplosMenu();

        timer = new Timer(Config.ATRASO_MS, this);
        timer.start();
    }

    private void criarExemplosMenu() {
        exemplosMenu.add(new InimigoZigueZague(250, 300, 0, 1));
        exemplosMenu.add(new InimigoRicochete(252, 340, 0));
        exemplosMenu.add(new InimigoMergulhador(251, 385, 0, nave, 0));
        exemplosMenu.add(new InimigoCadente(253, 425, 0));
    }

    // ------------------------------------------------------------------
    // Controle da partida
    // ------------------------------------------------------------------

    private void novoJogo() {
        pontos = 0;
        vidas = Config.VIDAS_INICIAIS;
        onda = 0;
        nave.pararMovimento();
        nave.tornarInvulneravel(0);
        atirando = false;
        proximaOnda("");
    }

    private void proximaOnda(String textoBonus) {
        onda++;
        tiros.clear();
        inimigos.clear();

        if (onda > GerenciadorDeOndas.TOTAL_ONDAS) {
            onda = GerenciadorDeOndas.TOTAL_ONDAS;
            atualizarRecorde();
            estado = EstadoJogo.VITORIA;
            return;
        }

        inimigos.addAll(gerenciador.criarOnda(onda, nave));
        energia.recarregar();
        nave.reposicionar();

        mensagem = "ONDA " + onda + " - " + inimigos.get(0).getNome().toUpperCase();
        submensagem = textoBonus;
        contadorTransicao = DURACAO_TRANSICAO;
        estado = EstadoJogo.TRANSICAO;
    }

    private void ondaConcluida() {
        int bonus = (int) energia.getAtual() * 10;
        pontos += bonus;
        proximaOnda("Bônus de energia: +" + bonus);
    }

    private void perderVida(String motivo) {
        vidas--;
        tiros.clear();

        if (vidas <= 0) {
            atualizarRecorde();
            estado = EstadoJogo.GAME_OVER;
            return;
        }

        energia.recarregar();
        nave.reposicionar();
        nave.tornarInvulneravel(TICKS_INVULNERAVEL);
        mensagem = motivo;
        submensagem = "Vidas restantes: " + vidas;
        contadorTransicao = DURACAO_MORTE;
        estado = EstadoJogo.TRANSICAO;
    }

    private void atualizarRecorde() {
        recorde = Math.max(recorde, pontos);
    }

    // ------------------------------------------------------------------
    // Loop do jogo (chamado pelo Timer a cada ~16 ms)
    // ------------------------------------------------------------------

    @Override
    public void actionPerformed(ActionEvent e) {
        if (estado != EstadoJogo.PAUSADO) {
            fundo.atualizar();
        }

        switch (estado) {
            case JOGANDO:
                atualizarJogo();
                break;
            case TRANSICAO:
                contadorTransicao--;
                if (contadorTransicao <= 0) {
                    estado = EstadoJogo.JOGANDO;
                }
                break;
            default:
                break;
        }
        repaint();
    }

    private void atualizarJogo() {
        nave.atualizar();
        if (atirando && !existeTiroDoJogador()) {
            tiros.add(nave.atirar()); // só um tiro do jogador por vez
        }

        for (Inimigo inimigo : inimigos) {
            inimigo.atualizar();
        }
        inimigosAtiram();
        for (Tiro tiro : tiros) {
            tiro.atualizar();
        }

        verificarColisoes();
        tiros.removeIf(t -> !t.isAtivo());
        inimigos.removeIf(i -> !i.isAtivo());

        if (estado != EstadoJogo.JOGANDO) {
            return; // perdeu uma vida durante as colisões
        }

        energia.atualizar();
        if (energia.isEsgotada()) {
            perderVida("ENERGIA ESGOTADA!");
        } else if (inimigos.isEmpty()) {
            ondaConcluida();
        }
    }

    private boolean existeTiroDoJogador() {
        for (Tiro tiro : tiros) {
            if (tiro.isDoJogador()) {
                return true;
            }
        }
        return false;
    }

    private void inimigosAtiram() {
        int tirosInimigos = 0;
        for (Tiro tiro : tiros) {
            if (!tiro.isDoJogador()) {
                tirosInimigos++;
            }
        }

        int maximo = gerenciador.maximoTirosInimigos(onda);
        double chance = gerenciador.chanceDeTiro(onda);
        for (Inimigo inimigo : inimigos) {
            if (tirosInimigos >= maximo) {
                return;
            }
            if (inimigo.podeAtirar() && random.nextDouble() < chance) {
                tiros.add(new Tiro(inimigo.getCentroX() - 3, inimigo.getY() + inimigo.getAltura(),
                        gerenciador.velocidadeTiro(onda), false));
                tirosInimigos++;
            }
        }
    }

    private void verificarColisoes() {
        // Tiros do jogador contra inimigos
        for (Tiro tiro : tiros) {
            if (!tiro.isDoJogador() || !tiro.isAtivo()) {
                continue;
            }
            for (Inimigo inimigo : inimigos) {
                if (tiro.colideCom(inimigo)) {
                    tiro.desativar();
                    inimigo.desativar();
                    pontos += inimigo.getPontos();
                    break;
                }
            }
        }

        if (nave.isInvulneravel()) {
            return;
        }

        // Tiros inimigos contra a nave
        for (Tiro tiro : tiros) {
            if (!tiro.isDoJogador() && tiro.colideCom(nave)) {
                perderVida("NAVE ATINGIDA!");
                return;
            }
        }

        // Inimigos encostando na nave
        for (Inimigo inimigo : inimigos) {
            if (inimigo.colideCom(nave)) {
                inimigo.desativar();
                perderVida("COLISÃO!");
                return;
            }
        }
    }

    // ------------------------------------------------------------------
    // Desenho
    // ------------------------------------------------------------------

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        fundo.desenhar(g2);

        if (estado == EstadoJogo.MENU) {
            desenharMenu(g2);
            return;
        }

        desenharJogo(g2);

        switch (estado) {
            case TRANSICAO:
                textoCentralizado(g2, mensagem, 240, FONTE_GRANDE, Color.WHITE);
                textoCentralizado(g2, submensagem, 280, FONTE_NORMAL, Color.YELLOW);
                break;
            case PAUSADO:
                textoCentralizado(g2, "PAUSADO", 250, FONTE_GRANDE, Color.WHITE);
                textoCentralizado(g2, "P para continuar  |  ESC para o menu", 290, FONTE_PEQUENA, Color.LIGHT_GRAY);
                break;
            case GAME_OVER:
                desenharTelaFinal(g2, "FIM DE JOGO", new Color(255, 80, 60));
                break;
            case VITORIA:
                desenharTelaFinal(g2, "VOCÊ VENCEU!", new Color(80, 220, 120));
                break;
            default:
                break;
        }
    }

    private void desenharJogo(Graphics2D g) {
        for (Inimigo inimigo : inimigos) {
            inimigo.desenhar(g);
        }
        for (Tiro tiro : tiros) {
            tiro.desenhar(g);
        }
        if (estado != EstadoJogo.GAME_OVER) {
            nave.desenhar(g);
        }
        hud.desenhar(g, pontos, Math.max(recorde, pontos), onda, vidas, energia);
    }

    private void desenharMenu(Graphics2D g) {
        textoCentralizado(g, Config.TITULO.toUpperCase(), 140, FONTE_TITULO, new Color(90, 200, 255));
        textoCentralizado(g, "Destrua todas as " + GerenciadorDeOndas.TOTAL_ONDAS
                + " ondas antes que a energia acabe!", 190, FONTE_PEQUENA, Color.LIGHT_GRAY);

        g.setFont(FONTE_NORMAL);
        for (Inimigo exemplo : exemplosMenu) {
            exemplo.desenhar(g);
            g.setColor(Color.WHITE);
            g.drawString(String.format("%-12s %3d pts", exemplo.getNome(), exemplo.getPontos()),
                    310, (int) exemplo.getY() + 19);
        }

        textoCentralizado(g, "SETAS ou A/D: mover    ESPAÇO: atirar    P: pausar", 490, FONTE_PEQUENA, Color.LIGHT_GRAY);
        if (recorde > 0) {
            textoCentralizado(g, "RECORDE: " + recorde, 250, FONTE_NORMAL, Color.YELLOW);
        }
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            textoCentralizado(g, "Pressione ENTER para começar", 540, FONTE_NORMAL, Color.WHITE);
        }
    }

    private void desenharTelaFinal(Graphics2D g, String titulo, Color cor) {
        g.setColor(new Color(0, 0, 0, 170));
        g.fillRect(0, 0, Config.LARGURA, Config.AREA_JOGO);
        textoCentralizado(g, titulo, 220, FONTE_TITULO, cor);
        textoCentralizado(g, "Pontuação: " + pontos, 280, FONTE_NORMAL, Color.WHITE);
        if (pontos >= recorde && pontos > 0) {
            textoCentralizado(g, "NOVO RECORDE!", 310, FONTE_NORMAL, Color.YELLOW);
        }
        textoCentralizado(g, "ENTER: jogar novamente    ESC: menu", 370, FONTE_PEQUENA, Color.LIGHT_GRAY);
    }

    private void textoCentralizado(Graphics2D g, String texto, int y, Font fonte, Color cor) {
        g.setFont(fonte);
        g.setColor(cor);
        FontMetrics metricas = g.getFontMetrics();
        int x = (Config.LARGURA - metricas.stringWidth(texto)) / 2;
        g.drawString(texto, x, y);
    }

    // ------------------------------------------------------------------
    // Teclado
    // ------------------------------------------------------------------

    @Override
    public void keyPressed(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                nave.setMovendoEsquerda(true);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                nave.setMovendoDireita(true);
                break;
            case KeyEvent.VK_SPACE:
                atirando = true;
                break;
            case KeyEvent.VK_ENTER:
                if (estado == EstadoJogo.MENU || estado == EstadoJogo.GAME_OVER || estado == EstadoJogo.VITORIA) {
                    novoJogo();
                }
                break;
            case KeyEvent.VK_P:
                if (estado == EstadoJogo.JOGANDO) {
                    estado = EstadoJogo.PAUSADO;
                } else if (estado == EstadoJogo.PAUSADO) {
                    estado = EstadoJogo.JOGANDO;
                }
                break;
            case KeyEvent.VK_ESCAPE:
                if (estado != EstadoJogo.MENU) {
                    atualizarRecorde();
                    estado = EstadoJogo.MENU;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                nave.setMovendoEsquerda(false);
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                nave.setMovendoDireita(false);
                break;
            case KeyEvent.VK_SPACE:
                atirando = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // não utilizado
    }
}
