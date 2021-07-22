package Interacao;
import main.Main;
import modelo.Jogada;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Classe feita para gerir todos os menus do programa.
 * Tem como propósito apresentar informação ao utilizador e se necessário
 * correr algoritmos simples para obter determinado resulta.
 */

public class Aplicacao {

    private static Scanner scanner;

    /**
     * Construtor da classe aplicação que inicia a variável scanner.
     */

    public Aplicacao() {
        scanner = new Scanner(System.in);
    }

    /**
     * Metodo responsável por obter uma resposta do utilizador.
     * @param aMsg mensagem mostrada ao utilizador
     * @return String com a resposta do utilizador
     */

    public static String lerString(String aMsg) {
        System.out.println(aMsg);
        return scanner.nextLine();
    }

    /**
     * Metodo responsável por obter um valor inteiro do utilizador
     * @param aMsg mensagem mostrada ao uitlizador
     * @return Inteiro com a resposta do utilizador
     */

    public static int lerInteiro(String aMsg) {
        do {
            System.out.println(aMsg);
            try {
                int i = scanner.nextInt();
                scanner.nextLine();
                return i;
            } catch (InputMismatchException error) {
                scanner.nextLine();
                System.out.println("Introduzir um valor válido!.");
            }
        } while (true);
    }



    /**
     * Mostrar um pequeno "menu" e pedir nickname ao utilizador, nickname que será usado depois ao longo do jogo.
     * @return String com o nickname dado.
     */

    public String pedirNickname(){
        System.out.println("-------------------BEM VINDO AO JOGO SUDOKU---------------------\n");
        return lerString("Nickname:");
    }

    /**
     * Mostrar tela inicial onde o utilizador vai escolher as várias opções que a aplicação oferece.
     * @return inteiro com a resposta dada.
     */

    public int telaInicial(){
        System.out.println("--------------------SUDOKU--------------------\n\n");
        System.out.println("1- Jogar Multiplayer");
        System.out.println("2- Jogar Single-Player");
        System.out.println("3- Listagens");
        System.out.println("4- Replay de jogos");
        System.out.println("5- Log");
        System.out.println("6- Sair\n");

        return lerInteiro("Opcao:");
    }

    /**
     * menu associado ao log
     * @return inteiro com a opção pretendida.
     */

    public int menuLog(){
        System.out.println("--------------------LOG------------------");
        System.out.println("1- mostrar log geral");
        System.out.println("2- mostrar log de utilizador");
        System.out.println("3- voltar\n");

        return lerInteiro("Opcao:");
    }

    /**
     * Mostra meu com as opções de ser o cliente ou servidor.
     * @return inteiro com a escolha tomada.
     */

    public int escolhaServerClient(){
        System.out.println("----------------CLIENTE/SERVIDOR----------------\n\n");
        System.out.println("1- Cliente");
        System.out.println("2- Host");
        System.out.println("3- Voltar");

        return lerInteiro("Opcao:");
    }

    /**
     * Metodo responsável por ler os valores de linha, coluna e valor a colocar no tabuleiro
     * (entre 0 e 9 inclusive).
     * @param msg mensagem a mostrar ao utilizador
     * @return inteiro entre 0 e 9.
     */

    public int lerLinhaColunaValor(String msg){
        int valor;

        do {
            valor = lerInteiro(msg);
        }while (valor < 0 || valor > 9);

        return valor;
    }

    public int lerValorSinglePlayer(String aMsg){
        int valor;

        do {
            valor = lerInteiro(aMsg);
        }while (valor < -1 || valor > 9);

        return valor;
    }

    /**
     * imprimir uma mensagem no ecrã, serve para conseguir imprimir msgs em outras classes que não
     * são projetadas para isso mas há a necessidade de o fazer.
     * @param msg mensagem a imprimir
     */

    public void imprimirMensagem(String msg){
        System.out.println(msg);
    }

    /**
     * metodo responsável por inserir as jogadas iniciais num jogo de ligação por sockets.
     * @return Vetor com as jogadas iniciais
     */

    public Jogada[] inseriJogadasIniciaisSocket(){
        int linha, col, value;

        System.out.println("---------------INSERIR JOGADAS INICIAIS---------------\n\n");
        System.out.println("Jogada 1:\n");
        Jogada jogadaInicial1 = new Jogada(lerLinhaColunaValor("linha (1 a 9):") - 1,
                            lerLinhaColunaValor("coluna (1 a 9)") - 1,
                                    lerLinhaColunaValor("valor (1 a 9)"), true);
        Jogada jogadaInicial2 = new Jogada(lerLinhaColunaValor("linha (1 a 9):") - 1,
                                lerLinhaColunaValor("coluna (1 a 9)") - 1,
                                    lerLinhaColunaValor("valor (1 a 9)"), true);

        Jogada[] jogadas ={jogadaInicial1,jogadaInicial2};

        return jogadas;
    }

    /**
     * menu mostrado quando é chega a vez do jogador jogar, ele pode escolher entre jogar,
     * ver tabuleiros ou desistir.
     * @return inteiro com a opcao escolhida.
     */

    public int menuInserirJogadaDesistir() {
        System.out.println("-----------------SUA VEZ DE JOGAR----------------\n\n");
        System.out.println("1- Inserir jogada");
        System.out.println("2- Ver tabuleiros");
        System.out.println("3- Desistir\n");

        return lerInteiro("Opcao:");
    }

    /**
     * menu apresentado ao utilizador para saber se este quer jogar de novo.
     * @return inteiro com a resposta.
     */

    public int menuJogarDnv(){
        System.out.println("----------------COMEÇAR NOVO JOGO?---------------\n\n");
        System.out.println("1- SIM");
        System.out.println("2- NAO\n");

        return lerInteiro("Opcao:");
    }

    /**
     * menu onde é possivel escolher a listagem e o tipo de listagem a fazer.
     * @return inteiro com a opcao escolhida.
     */

    public int menuListagens(){
        System.out.println("-------------LISTAGENS--------------\n\n");
        System.out.println("1- Listar utilizadores por ordem alfabetica");
        System.out.println("2- Listar utilizadores por vitorias");
        System.out.println("3- Listar utilizadores por tempo de jogo");
        System.out.println("4- Listar utilizadores por numero de jogos");
        System.out.println("5- Voltar\n");

        return lerInteiro("Opcao:");
    }

    /**
     * menu apresentado ao utilizador para este escolher qual a ordem a ser apresentada uma listagem
     * @return inteiro com a opção escolhida
     */

    public int ordemListagem(){
        System.out.println("----------ORDEM DE LISTAGEM-------------");
        System.out.println("1- Crescente");
        System.out.println("2- Decrescente");
        System.out.println("3- Voltar\n");

        return lerInteiro("Opcao:");
    }

    public int menuReplay(){
        System.out.println("--------REPLAY DE JOGADAS----------");
        System.out.println("1- Ver sequencia de jogadas");
        System.out.println("2- Ver simulacao de um jogo ");
        System.out.println("3- Voltar");

        return lerInteiro("Opcao:");
    }

}
