package jogo;

import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 * Classe base abstrata para tudo que aparece na tela e se move
 * (nave, tiros e inimigos). Guarda posição e tamanho e oferece a
 * detecção de colisão por retângulos.
 */
public abstract class Entidade {

    protected double x;
    protected double y;
    protected int largura;
    protected int altura;
    protected boolean ativo = true;

    protected Entidade(double x, double y, int largura, int altura) {
        this.x = x;
        this.y = y;
        this.largura = largura;
        this.altura = altura;
    }

    /** Atualiza a posição/estado da entidade a cada quadro. */
    public abstract void atualizar();

    /** Desenha a entidade na tela. */
    public abstract void desenhar(Graphics2D g);

    public Rectangle getLimites() {
        return new Rectangle((int) x, (int) y, largura, altura);
    }

    /** Verifica se os retângulos das duas entidades se sobrepõem. */
    public boolean colideCom(Entidade outra) {
        return ativo && outra.ativo && getLimites().intersects(outra.getLimites());
    }

    public double getCentroX() {
        return x + largura / 2.0;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public int getAltura() {
        return altura;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void desativar() {
        ativo = false;
    }
}
