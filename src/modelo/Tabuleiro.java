package modelo;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

/**
 * Classe que representa o tabuleiro de sudoku onde se vai jogar.
 */

public class Tabuleiro {
    private final int tamanholinhaColuna = 9;
    private Integer [][] tabuleiro;
    private HashMap <String, Boolean> posicaoValor = new HashMap<String, Boolean>();

    /**
     * Construtor da classe Tabuleiro onde se inicia o tabuleiro como array bidemensional 9x9.
     */

    public Tabuleiro() {
        this.tabuleiro = new Integer[9][9];
    }

    /**
     * Metodo responsável por fazer a representação visual do tabuleiro ao jogador.
     */

    public void printTab(){
        int x = 1;
        System.out.print("       1   2   3       4   5   6      7    8   9\n   --------------------------------------------------\n");
        for (int row = 0; row < tamanholinhaColuna;row++){
            for (int col = 0; col < tamanholinhaColuna; col++){
                if (col == 0)
                    System.out.print(x++ + "  |   ");
                if (tabuleiro[row][col] == null)
                    System.out.print(".   ");
                else
                    System.out.print(tabuleiro[row][col] + "   ");
                if ((col+1)%3 == 0)
                    System.out.print ("|   ");
            }
            if ((row+1)%3 == 0)
                System.out.print("\n   --------------------------------------------------");
            System.out.println();
        }
    }

    /**
     * Metodo responsável por dar um valor random entre os parametros concedidos.
     * @param min numero minimo possivel.
     * @param max numero maximo possivel.
     * @return qualquer numero incluido entre min e max (inclusive estes dois).
     */

    public int rand(int min, int max)   //try catch para o erro do throw
    {
        if (min > max || (max - min + 1 > Integer.MAX_VALUE)) {
            throw new IllegalArgumentException("Invalid range");
        }

        return new Random().nextInt(max - min + 1) + min;
    }

    /**
     * gerar jogada aleatória através da função rand que permite obter numeros aleatorios para
     * linha, coluna e valor a inserir.
     * @return Jogada com valores aleatórias nos seus parêmtros, valores entre 0 e 8.
     */

    public Jogada gerarJogadaAleatoria(){
        return new Jogada(rand(0,8),rand(0,8),rand(1,9),true);
    }

    /**
     * Metodo responsável por inserir a jogada, aqui a jogada é apenas inserida se não for colocada num local com
     * numero fixo, para perceber se no local o numero é fixo uso um hashmap que tem como key o valor de linnha e
     * coluna, e como value um boolean para dizer se é fixa ou n, as jogadas sao inseridas no tabuleiro e no hashmap.
     * @param jogada Jogada a ser inserida
     * @return boolean a informar se a jogada foi inserida com sucesso.
     */

    public boolean inserirJogada(Jogada jogada){
        if (jogada == null)
            return false;

        Boolean fixo = posicaoValor.get(""+jogada.getPosicaoX()+jogada.getPosicaoY());

        if (fixo == null || !fixo) {
            if (jogada.getValue() == 0)
                tabuleiro[jogada.getPosicaoX()][jogada.getPosicaoY()] = null;
            else
                tabuleiro[jogada.getPosicaoX()][jogada.getPosicaoY()] = jogada.getValue();
            if (fixo == null) { //jogada não está lá
                posicaoValor.put("" + jogada.getPosicaoX() + jogada.getPosicaoY(), jogada.isFixo());
            }
            return true;
        }
        return false;
    }

    /**
     * Verificar se é possivel colocar o valor dado na posição de linha dada.
     * @param posicaoLinha posição da linha onde pretende inserir.
     * @param value valor a inserir.
     * @return boolean para informar se ocorreu com sucesso.
     */

    public boolean verificarLinha(int posicaoLinha,int value){
        for (int x = 0; x < tamanholinhaColuna; x++){
            if (tabuleiro[posicaoLinha][x] != null)
                if (tabuleiro[posicaoLinha][x] == value)
                    return false;
        }
        return true;
    }

    /**
     * Verificar se é possivel colocar o valor dado na posição de coluna dada..
     * @param posicaoColuna posição da coluna onde quer inserir.
     * @param value valor a inserir.
     * @return boolean para informar se ocorreu com sucesso.
     */

    public boolean verificarColuna(int posicaoColuna,int value){
        for (int x = 0; x < tamanholinhaColuna; x++){
            if (tabuleiro[x][posicaoColuna] != null)
                if (tabuleiro[x][posicaoColuna] == value)
                    return false;
        }
        return true;
    }

    /**
     * Verificar se no espaço de 3v3 onde o valor pertence já não existe um valor igual.
     * @param posicaoLinha posicao da linha.
     * @param posicaoColuna posicao da coluna.
     * @param valor valor a colocar.
     * @return boolean a informar o sucesso da ação.
     */

    public boolean verificarQuadrado (int posicaoLinha,int posicaoColuna,int valor){
        int subLinhaInicio = (posicaoLinha/3) * 3;
        int subLinhaFim = subLinhaInicio + 3;

        int subColunaInicio = (posicaoColuna/3) * 3;
        int subColunaFim = subColunaInicio + 3;

        for (int x = subLinhaInicio; x < subLinhaFim; x++)
            for (int z = subColunaInicio; z < subColunaFim; z++)
                if (tabuleiro[x][z] != null)
                    if (tabuleiro[x][z] == valor)
                        return false;

         return true;
    }

    /**
     * verificar se o tabuleiro está completo.
     * @return boolean a informar se o tabuleiro está completo.
     */

    public boolean tabuleiroCompleto(){
        if (tabuleiro == null)
            return false;

        for(int x = 0; x < tamanholinhaColuna; x++)
            for (int y = 0; y < tamanholinhaColuna; y++)
                if (tabuleiro[x][y] == null)
                    return false;

        return true;
    }

    /**
     * Representação textual de um tabuleiro.
     * @return String com informações do tabuleiro.
     */

    @Override
    public String toString() {
        return "model.Tabuleiro{" +
                "tabuleiro=" + Arrays.toString(tabuleiro) +
                '}';
    }
}
