package Gerir;

import modelo.Jogada;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;

/**
 * Classe que representa o registo de um jogo.
 */

public class RegistoJogo implements Serializable {

    private String nicknameServer;
    private String nicknameCliente;
    private int primeiroJogar;
    private Vector<Jogada> jogadas;
    private String dataJogo;
    private String vencedor;

    /**
     * Ao criar um objeto desta classe inicia-se o vetor também.
     */

    public RegistoJogo() {
        jogadas = new Vector<>();
    }

    /**
     * Metodo responsável por obter a data de cada jogo.
     */

    public void registarData(){
        SimpleDateFormat formatter= new SimpleDateFormat("<yyyy-MM-dd> <HH:mm:ss>");
        Date date = new Date(System.currentTimeMillis());
        dataJogo = formatter.format(date);
    }

    /**
     * Metodo responsável por adicionar uma jogada ao vetor.
     * @param jogada Jogada a adicionar.
     * @return boolean a confirmar.
     */

    public boolean adicionarJogada(Jogada jogada){
        if (jogadas.add(jogada))
            return true;
        return false;
    }

    /**
     * GETTER para a data do jogo.
     * @return String com a data do jogo.
     */

    public String getDataJogo() {
        return dataJogo;
    }

    /**
     * GETTER para o nickname do server.
     * @return String com o nickname do server.
     */

    public String getNicknameServer() {
        return nicknameServer;
    }

    /**
     * GETTER para o nickname do cliente.
     * @return String com o nickname do cliente.
     */

    public String getNicknameCliente() {
        return nicknameCliente;
    }

    public String getVencedor() {
        return vencedor;
    }

    /**
     * GETTER para o valor de primeiro lugar
     * @return inteiro com valor.
     */



    public int getPrimeiroJogar() {
        return primeiroJogar;
    }

    /**
     * GETTER para o vetor de jogadas.
     * @return Vetor com as jogadas.
     */

    public Vector<Jogada> getJogadas() {
        return jogadas;
    }

    /**
     * SETTER para o nickname do server
     * @param nicknameServer nickname do server em String.
     */

    public void setNicknameServer(String nicknameServer) {
        this.nicknameServer = nicknameServer;
    }

    public void setVencedor(String vencedor) {
        this.vencedor = vencedor;
    }

    /**
     * SETTER para o nickname do cliente
     * @param nicknameCliente String com nickname do cliente.
     */




    public void setNicknameCliente(String nicknameCliente) {
        this.nicknameCliente = nicknameCliente;
    }

    /**
     * SETTER para o valor de primeiro a jogar
     * @param primeiroJogar inteiro com o valor de primeiro a jogar.
     */

    public void setPrimeiroJogar(int primeiroJogar) {
        this.primeiroJogar = primeiroJogar;
    }
}
