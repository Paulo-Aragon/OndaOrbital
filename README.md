# Onda Orbital

Jogo retrô de nave (*fixed shooter*) feito em **Java + Swing** para o Trabalho G2 de
Programação Orientada a Objetos.

A nave do jogador fica na parte de baixo da tela e precisa destruir **8 ondas** de
inimigos. Cada onda tem um tipo de inimigo com um padrão de movimento diferente, e uma
**barra de energia** vai esvaziando com o tempo: se ela zerar antes de a onda acabar,
o jogador perde uma vida.

## Como executar

Requisito: **JDK 17 ou superior**.

No Windows, basta dar dois cliques em `executar.bat`, ou pelo terminal:

```bash
javac -encoding UTF-8 -d out src/jogo/*.java
java -cp out jogo.Main
```

### Executável (.exe) com ícone

Rodando `gerar-exe.bat`, é criado `dist\OndaOrbital\OndaOrbital.exe`, que já vem com o
Java embutido (funciona em PCs sem Java instalado) e com o ícone do jogo. Para levar o
jogo para outro computador, copie a pasta `dist\OndaOrbital` inteira.

O ícone é desenhado por código na classe `Icone` (o mesmo desenho aparece na barra de
título da janela) e a ferramenta `ferramentas/GerarIcone.java` salva `icone/icone.png` e
`icone/icone.ico`.

Também dá para abrir a pasta no VS Code (com o *Extension Pack for Java*), IntelliJ ou
Eclipse e executar a classe `jogo.Main`.

## Controles

| Tecla              | Ação                          |
|--------------------|-------------------------------|
| ← → ou A / D       | Mover a nave                  |
| Espaço             | Atirar (um tiro por vez)      |
| Enter              | Começar / jogar novamente     |
| P                  | Pausar / continuar            |
| Esc                | Voltar ao menu                |

## Regras

- O jogador começa com **3 vidas**.
- Perde uma vida quando: é atingido por um tiro inimigo, encosta em um inimigo ou a
  energia acaba.
- Depois de perder uma vida, a nave fica invulnerável (piscando) por 2 segundos.
- **Pontuação:** cada inimigo vale pontos (tabela abaixo) e, ao terminar uma onda, a
  energia restante vira bônus (energia × 10).
- **Vitória:** destruir as 8 ondas. **Derrota:** perder todas as vidas.
- O recorde (maior pontuação) fica salvo enquanto o jogo estiver aberto.

| Inimigo      | Movimento                                                     | Pontos |
|--------------|---------------------------------------------------------------|--------|
| Zigue-zague  | Fileiras que andam para os lados ondulando e atravessam a tela | 20     |
| Ricochete    | Diagonal, quicando nas bordas da metade de cima                | 30     |
| Mergulhador  | Em formação; de vez em quando mergulha na direção da nave      | 40     |
| Cadente      | Cai do topo balançando; não atira, mas é perigoso encostar     | 50     |

As ondas 1 a 4 apresentam cada tipo; as ondas 5 a 8 repetem os tipos com mais velocidade
e mais tiros.

## Estrutura do projeto

```
src/jogo/
├── Main.java               Cria o JFrame e inicia o jogo
├── Config.java             Constantes (tamanho da tela, FPS, vidas)
├── EstadoJogo.java         enum: MENU, TRANSICAO, JOGANDO, PAUSADO, GAME_OVER, VITORIA
├── GamePanel.java          JPanel com o loop (Timer), teclado (KeyListener), regras e desenho
├── Entidade.java           Classe abstrata base: posição, tamanho, colisão
├── Nave.java               Nave do jogador
├── Tiro.java               Projétil do jogador ou do inimigo
├── Inimigo.java            Classe abstrata base dos inimigos
├── InimigoZigueZague.java  ┐
├── InimigoRicochete.java   │ Um tipo de inimigo por classe,
├── InimigoMergulhador.java │ cada um com seu movimento e desenho
├── InimigoCadente.java     ┘
├── GerenciadorDeOndas.java Monta cada onda e define a dificuldade
├── BarraDeEnergia.java     Energia que diminui com o tempo
├── HUD.java                Painel de pontos, recorde, onda, vidas e energia
├── FundoEstrelado.java     Fundo animado de estrelas
└── Icone.java              Desenha o ícone do jogo (janela e executável)

ferramentas/GerarIcone.java Gera icone/icone.png e icone/icone.ico
executar.bat                Compila e roda o jogo
gerar-exe.bat               Gera o OndaOrbital.exe com ícone (jpackage)
```

### Hierarquia de classes

```
Entidade (abstrata)
├── Nave
├── Tiro
└── Inimigo (abstrata)
    ├── InimigoZigueZague
    ├── InimigoRicochete
    ├── InimigoMergulhador
    └── InimigoCadente
```

### Como o jogo funciona

1. `Main` cria a janela (`JFrame`) e adiciona o `GamePanel`.
2. O `GamePanel` cria um `javax.swing.Timer` que dispara a cada 16 ms (~60 FPS). Em cada
   disparo, `actionPerformed` atualiza o jogo e chama `repaint()`.
3. `atualizarJogo()` move a nave, os inimigos e os tiros; sorteia tiros inimigos; verifica
   colisões; gasta energia; e confere se a onda acabou ou se uma vida foi perdida.
4. `paintComponent()` desenha o fundo, as entidades, o HUD e as mensagens, conforme o
   `EstadoJogo` atual.
5. O `KeyListener` (`keyPressed` / `keyReleased`) liga e desliga flags de movimento e de
   tiro, o que deixa o movimento suave enquanto a tecla está pressionada.

## Conceitos de POO aplicados

- **Abstração:** `Entidade` e `Inimigo` são classes abstratas que definem o que toda
  entidade/inimigo precisa ter (`atualizar`, `desenhar`, `mover`, `getNome`).
- **Herança:** `Nave`, `Tiro` e os inimigos reaproveitam posição, tamanho e colisão de
  `Entidade`.
- **Polimorfismo:** o `GamePanel` guarda todos os inimigos numa `List<Inimigo>` e chama
  `atualizar()` e `desenhar()` sem saber o tipo concreto; cada subclasse responde do seu
  jeito. `podeAtirar()` é sobrescrito por `InimigoMergulhador` e `InimigoCadente`.
- **Encapsulamento:** atributos `private`/`protected`, acessados por métodos
  (`getPontos()`, `isInvulneravel()`, `recarregar()`...).
- **Template Method:** `Inimigo.atualizar()` é `final`, faz a parte comum e chama o método
  abstrato `mover()` das subclasses.
- **Enum:** `EstadoJogo` controla a máquina de estados do jogo; `InimigoMergulhador`
  usa um enum interno para as fases do mergulho.
- **Responsabilidade única:** cada classe tem uma função clara (HUD só desenha
  informações, `GerenciadorDeOndas` só monta as ondas etc.).

## Recursos do Swing utilizados

`JFrame`, `JPanel`, `javax.swing.Timer`, `ActionListener`, `KeyListener`,
`paintComponent`, `Graphics2D` (polígonos, retângulos, ovais, texto, antialiasing),
`SwingUtilities.invokeLater`.
