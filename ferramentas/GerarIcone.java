import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import jogo.Icone;

/**
 * Ferramenta (não faz parte do jogo): gera icone/icone.png e icone/icone.ico
 * a partir do desenho da classe jogo.Icone. O .ico é usado pelo jpackage no OndaOrbital.exe.
 *
 * Uso: java -cp out GerarIcone
 */
public class GerarIcone {

    public static void main(String[] args) throws IOException {
        File pasta = new File("icone");
        pasta.mkdirs();

        ImageIO.write(Icone.criar(256), "png", new File(pasta, "icone.png"));

        // Cada tamanho vira um PNG embutido no .ico (formato aceito desde o Windows Vista).
        List<byte[]> pngs = new ArrayList<>();
        for (int tamanho : Icone.TAMANHOS) {
            BufferedImage img = Icone.criar(tamanho);
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bytes);
            pngs.add(bytes.toByteArray());
        }

        int quantidade = pngs.size();
        int deslocamento = 6 + 16 * quantidade;
        ByteBuffer cabecalho = ByteBuffer.allocate(deslocamento).order(ByteOrder.LITTLE_ENDIAN);
        cabecalho.putShort((short) 0); // reservado
        cabecalho.putShort((short) 1); // tipo: ícone
        cabecalho.putShort((short) quantidade);
        for (int i = 0; i < quantidade; i++) {
            int tamanho = Icone.TAMANHOS[i];
            cabecalho.put((byte) (tamanho >= 256 ? 0 : tamanho)); // largura (0 = 256)
            cabecalho.put((byte) (tamanho >= 256 ? 0 : tamanho)); // altura
            cabecalho.put((byte) 0); // paleta
            cabecalho.put((byte) 0); // reservado
            cabecalho.putShort((short) 1); // planos
            cabecalho.putShort((short) 32); // bits por pixel
            cabecalho.putInt(pngs.get(i).length);
            cabecalho.putInt(deslocamento);
            deslocamento += pngs.get(i).length;
        }

        try (FileOutputStream saida = new FileOutputStream(new File(pasta, "icone.ico"))) {
            saida.write(cabecalho.array());
            for (byte[] png : pngs) {
                saida.write(png);
            }
        }
        System.out.println("Gerados icone/icone.png e icone/icone.ico");
    }
}
