package modelo;

import java.io.Serializable;

/**
 * Classe que representa uma jogada de sudoku.
 */

public class Jogada implements Serializable {
   private int posicaoX;
   private int posicaoY;
   private int value;
   private boolean fixo;

    /**
     * Construtor da classe Jogada.
     * @param posicaoX posição da linha no tabuleiro.
     * @param posicaoY poisção da coluna no tabuleiro.
     * @param value valor a colocar no tabuleiro nas posições dadas.
     * @param fixo valor boolean para representar as jogadas iniciais que não podem ser alteradas.
     */

    public Jogada(int posicaoX, int posicaoY, int value, boolean fixo) {
        this.posicaoX = posicaoX;
        this.posicaoY = posicaoY;
        this.value = value;
        this.fixo = fixo;
    }

    /**
     * GETTER para a linha.
     * @return valor da linha.
     */

    public int getPosicaoX() {
        return posicaoX;
    }

    /**
     * GETTER para a coluna.
     * @return valor da coluna
     */

    public int getPosicaoY() {
        return posicaoY;
    }

    /**
     * GETTER para o valor presente naquele "quadrado" do tabuleiro.
     * @return valor presente no "quadrado" do tabuleiro.
     */

    public int getValue() {
        return value;
    }

    /**
     * GETTER para o valor boolean que nos diz se a jogada é fixa ou não.
     * @return valor boolean associado á jogada.
     */

    public boolean isFixo() {
        return fixo;
    }

    @Override
    public String toString() {
        return "Jogada{" +
                "posicaoX=" + posicaoX +
                ", posicaoY=" + posicaoY +
                ", value=" + value +
                ", fixo=" + fixo +
                '}';
    }
}
