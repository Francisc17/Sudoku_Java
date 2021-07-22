package Gerir;

import Gerir.GereFicheirosTexto;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

/**
 * Classe que representa e gere o log.
 */

public class Log {

    private GereFicheirosTexto gft;

    /**
     * Contrutor da classe Log, onde se inicia o parâmetro gft que é uma instancia da classe GereFicheirosTexto.
     */

    public Log() {
        gft = new GereFicheirosTexto();
    }

    /**
     * Metodo responsável por criar um registo no ficheiro do log: log.txt.
     * @param msg mensagem a registar.
     * @param nickname nickname do jogador que fez a ação registada.
     */

    public void criarLog(String nickname,String msg) {

        SimpleDateFormat formatter= new SimpleDateFormat("<yyyy-MM-dd> <HH:mm:ss>");
        Date date = new Date(System.currentTimeMillis());
        String msgLog = formatter.format(date) + ' ' + '<' + nickname + '>'+ ' ' + '<' + msg + '>' + '\n';

          if (!gft.escreveFicheiro(msgLog))
            System.out.println("Erro ao escrever log");
        }


    /**
     * metodo para fechar a escrita no ficheiro do log.
     */

    public void fecharLog(){
        gft.encerraEscrita();
    }

    /**
     * metodo para limpar o log aquando do inicio da aplicação.
     */

    public void limparLog(){
        gft.aberturaEscrita("log.txt",false);
        gft.escreveFicheiro(null);                    //limpar log ao fim de cada sessão
        gft.encerraEscrita();
        gft.aberturaEscrita("log.txt",false);
    }

    /**
     * metodo para obter log em format de string.
     * @return String com o log presente no ficheiro log.txt.
     */

    public String obterLog() {
        gft.encerraEscrita();
        StringBuilder strb = new StringBuilder();
        String line;
        if (gft.aberturaLeitura("log.txt")) {
            try {
                while ((line = gft.br.readLine()) != null) {
                    strb.append(line + "\n");
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            gft.encerraLeitura();
        } else
            System.out.println("Erro ao consultar o log");

        gft.aberturaEscrita("log.txt", true);
        if (strb != null && strb.length() > 0)
            return strb.toString();
        return null;
    }

    /**
     * Metodo responsável por mostrar o log associado a um jogador.
     * @param nickname nickname do jogador que queremos que seja mostrado o log.
     */

    public void mostrarLogUtilizador(String nickname){
        String[] conteudo = obterLog().split("\n");
        for (String valor : conteudo){
            String[] novosValores = valor.split(">");
            if (novosValores[2].trim().substring(1).equalsIgnoreCase(nickname)){
                System.out.println(valor);
            }
        }
    }

    /**
     * Metodo para mostrar o log no ecra, 10 paginas de cada vez.
     */

    public void mostrarLog(){
        int delimitador = 0;
        String[] conteudo = obterLog().split("\n");
        for (String valor : conteudo){
            delimitador++;
            System.out.println(valor);
            if (delimitador%10 == 0)
                pressAnyKeyToContinue();
        }
    }

    /**
     * Esperar que o utilizador clique enter para continuar.
     */

    private static void pressAnyKeyToContinue() {
        System.out.println("Clique Enter Para Continuar...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }
}
