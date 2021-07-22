package modelo;

/**
 * Classe que representa um jogador.
 */

public class Jogador implements Comparable<Jogador> {

    private String nickname;
    private int numeroJogos;
    private int numeroVitorias;
    private long tempoJogo; //minutos

    /**
     * Contrutor da classe Jogador.
     * @param nickname nome do jogador que vai ser usado ao longo do jogo e para estatisticas.
     * @param numeroJogos numero de jogos que o jogador já fez desde a primeira vez que jogou.
     * @param numeroVitorias numero de vitorias que o jogador já teve desde a primeira vez que jogou.
     * @param tempoJogo tempo total de jogo que o jogador acumulou desde a primeira vez que jogou.
     */

    public Jogador(String nickname, int numeroJogos, int numeroVitorias, long tempoJogo) {
        this.nickname = nickname;
        this.numeroJogos = numeroJogos;
        this.numeroVitorias = numeroVitorias;
        this.tempoJogo = tempoJogo;
    }

    /**
     * GETTER para o nickname do jogador.
     * @return nickname do jogador.
     */

    public String getNickname() {
        return nickname;
    }

    /**
     * GETTER para o numero de jogos.
     * @return numero de jogos.
     */

    public int getNumeroJogos() {
        return numeroJogos;
    }

    /**
     * GETTER para o numero de vitorias.
     * @return numero de vitorias.
     */

    public int getNumeroVitorias() {
        return numeroVitorias;
    }

    /**
     * GETTER para o tempo total de jogo.
     * @return tempo total de jogo.
     */

    public long getTempoJogo() {
        return tempoJogo;
    }

    /**
     * Metodo responsável por dar um representação textual da classe Jogador.
     * @return String com todas as informações do jogador.
     */

    @Override
    public String toString() {
        return "Jogador{" +
                "nickname='" + nickname + '\'' +
                ", numeroJogos=" + numeroJogos +
                ", numeroVitorias=" + numeroVitorias +
                ", tempoJogo=" + tempoJogo +
                '}';
    }

    /**
     * Metodo compareTo usado para ser possivel usar o metodo Collection.sort nesta classe.
     * Para ser possivel utilizar este metodo é necessário a classe ser comparable, implement Comparable.
     * @param jgd jogador
     * @return inteiro
     */

    public int compareTo(Jogador jgd) {
            return nickname.compareToIgnoreCase(jgd.nickname);
    }


}
