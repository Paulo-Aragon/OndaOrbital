package jogo;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Cria os inimigos de cada onda e define a dificuldade.
 * As ondas 1-4 apresentam os quatro tipos de inimigo; as ondas 5-8
 * repetem os tipos, mais rápidos e atirando mais.
 */
public class GerenciadorDeOndas {

    public static final int TOTAL_ONDAS = 8;

    private final Random random = new Random();

    public List<Inimigo> criarOnda(int numero, Nave nave) {
        List<Inimigo> lista = new ArrayList<>();
        double multiplicador = numero <= 4 ? 1.0 : 1.6;
        int tipo = (numero - 1) % 4;

        switch (tipo) {
            case 0: // Três fileiras que andam em sentidos alternados
                for (int linha = 0; linha < 3; linha++) {
                    int direcao = (linha % 2 == 0) ? 1 : -1;
                    for (int i = 0; i < 6; i++) {
                        lista.add(new InimigoZigueZague(
                                i * 135 + linha * 40, 60 + linha * 60, 2.0 * multiplicador, direcao));
                    }
                }
                break;

            case 1: // Inimigos espalhados que ricocheteiam
                for (int i = 0; i < 9; i++) {
                    double x = 40 + random.nextInt(Config.LARGURA - 100);
                    double y = 40 + random.nextInt(200);
                    lista.add(new InimigoRicochete(x, y, 2.5 * multiplicador));
                }
                break;

            case 2: // Duas fileiras em formação que mergulham
                for (int linha = 0; linha < 2; linha++) {
                    for (int i = 0; i < 5; i++) {
                        lista.add(new InimigoMergulhador(
                                110 + i * 135, 60 + linha * 60, 4.0 * multiplicador, nave, 0.003 * multiplicador));
                    }
                }
                break;

            default: // Chuva de inimigos cadentes
                for (int i = 0; i < 10; i++) {
                    double x = 60 + random.nextInt(Config.LARGURA - 150);
                    lista.add(new InimigoCadente(x, -40 - i * 60, 2.0 * multiplicador));
                }
                break;
        }
        return lista;
    }

    /** Probabilidade, a cada quadro, de um inimigo atirar. */
    public double chanceDeTiro(int numero) {
        return 0.002 + numero * 0.0008;
    }

    /** Quantos tiros inimigos podem existir ao mesmo tempo na tela. */
    public int maximoTirosInimigos(int numero) {
        return 2 + numero / 2;
    }

    /** Velocidade dos tiros inimigos. */
    public double velocidadeTiro(int numero) {
        return 4.5 + numero * 0.3;
    }
}
