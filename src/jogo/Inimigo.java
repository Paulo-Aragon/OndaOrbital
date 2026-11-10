package jogo;

import java.util.Random;

/**
 * Classe base abstrata dos inimigos. Cada subclasse define o seu próprio
 * padrão de movimento (método mover) e a sua aparência (método desenhar).
 *
 * O método atualizar() é final e segue o padrão "Template Method":
 * ele faz a parte comum (contar o tempo) e delega o movimento à subclasse.
 */
public abstract class Inimigo extends Entidade {

    protected static final Random RANDOM = new Random();

    protected final double velocidade;
    private final int pontos;

    /** Quantos quadros o inimigo já existe; usado nas funções de movimento. */
    protected int tempo;

    protected Inimigo(double x, double y, int largura, int altura, double velocidade, int pontos) {
        super(x, y, largura, altura);
        this.velocidade = velocidade;
        this.pontos = pontos;
    }

    @Override
    public final void atualizar() {
        tempo++;
        mover();
    }

    /** Padrão de movimento específico de cada tipo de inimigo. */
    protected abstract void mover();

    /** Nome do tipo de inimigo, mostrado no menu e no início da onda. */
    public abstract String getNome();

    /** Se o inimigo pode disparar neste momento. Pode ser sobrescrito. */
    public boolean podeAtirar() {
        return true;
    }

    public int getPontos() {
        return pontos;
    }
}
