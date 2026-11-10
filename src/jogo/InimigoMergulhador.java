package jogo;

import java.awt.Color;
import java.awt.Graphics2D;

/**
 * Fica em formação balançando de um lado para o outro e, de vez em quando,
 * mergulha em direção à posição da nave. Ao sair por baixo, volta pelo topo.
 */
public class InimigoMergulhador extends Inimigo {

    private enum Fase {
        FORMACAO, MERGULHO, RETORNO
    }

    private static final Color COR = new Color(255, 150, 40);
    private static final double AMPLITUDE = 60;

    private final double baseX;
    private final double baseY;
    private final Nave alvo;
    private final double chanceMergulho;

    private Fase fase = Fase.FORMACAO;
    private double alvoX;

    public InimigoMergulhador(double x, double y, double velocidade, Nave alvo, double chanceMergulho) {
        super(x, y, 30, 24, velocidade, 40);
        this.baseX = x;
        this.baseY = y;
        this.alvo = alvo;
        this.chanceMergulho = chanceMergulho;
    }

    private double posicaoFormacaoX() {
        return baseX + Math.sin(tempo * 0.03) * AMPLITUDE;
    }

    @Override
    protected void mover() {
        switch (fase) {
            case FORMACAO:
                x = posicaoFormacaoX();
                y = baseY;
                if (RANDOM.nextDouble() < chanceMergulho) {
                    fase = Fase.MERGULHO;
                    alvoX = alvo.getCentroX();
                }
                break;

            case MERGULHO:
                y += velocidade;
                double distancia = alvoX - getCentroX();
                x += Math.signum(distancia) * Math.min(Math.abs(distancia), velocidade * 0.6);
                if (y > Config.AREA_JOGO) {
                    y = -altura;
                    fase = Fase.RETORNO;
                }
                break;

            case RETORNO:
                y += velocidade * 0.5;
                x += (posicaoFormacaoX() - x) * 0.05;
                if (y >= baseY) {
                    y = baseY;
                    fase = Fase.FORMACAO;
                }
                break;

            default:
                break;
        }
    }

    @Override
    public boolean podeAtirar() {
        return fase == Fase.FORMACAO;
    }

    @Override
    public String getNome() {
        return "Mergulhador";
    }

    @Override
    public void desenhar(Graphics2D g) {
        int cx = (int) x;
        int cy = (int) y;
        g.setColor(COR);
        g.fillPolygon(
                new int[] { cx, cx + 30, cx + 15 },
                new int[] { cy, cy, cy + 24 },
                3);
        g.setColor(Color.YELLOW);
        g.fillRect(cx + 12, cy + 4, 6, 6);
        // "Asas" que aparecem durante o mergulho
        if (fase == Fase.MERGULHO) {
            g.setColor(Color.RED);
            g.fillRect(cx - 4, cy, 4, 8);
            g.fillRect(cx + 30, cy, 4, 8);
        }
    }
}
