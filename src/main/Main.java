package main;

import Gerir.*;
import Interacao.Aplicacao;
import modelo.*;

import java.time.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;
import java.util.Vector;

/**
 * Class Main.
 */

public class Main {

    private static Aplicacao ap = new Aplicacao();
    private static GereJogadores gj = new GereJogadores();
    private static Tabuleiro tab = new Tabuleiro();
    private static Jogador jogador;
    public static Log log = new Log();
    public static GerirRegistoJogos grj = new GerirRegistoJogos();
    static TempoExecucao tp = new TempoExecucao();
    private static GereFicheiroObjeto go = new GereFicheiroObjeto();

    public static void main(String[] args) {
        System.out.println("\n" +
                ".d8888. db    db d8888b.  .d88b.  db   dD db    db \n" +
                "88'  YP 88    88 88  `8D .8P  Y8. 88 ,8P' 88    88 \n" +
                "`8bo.   88    88 88   88 88    88 88,8P   88    88 \n" +
                "  `Y8b. 88    88 88   88 88    88 88`8b   88    88 \n" +
                "db   8D 88b  d88 88  .8D `8b  d8' 88 `88. 88b  d88 \n" +
                "`8888Y' ~Y8888P' Y8888D'  `Y88P'  YP   YD ~Y8888P' \n\n");
        congratularJogadorMaisVitorias();
        lerObj();
        PedirNickname();
    }

    /**
     * Metodo responsável por chamar o menu de pedir o nickname e chamar a função de verificação do mesmo.
     * Caso esteja tudo ok, este vai chamar o menu inicial.
     */

    public static void PedirNickname() {
        log.limparLog();
        jogador = gj.verificarExistenciaJogador(ap.pedirNickname());
        System.out.println("Bem vindo "+jogador.getNickname());
        pressAnyKeyToContinue();

        if (jogador != null) {
            log.criarLog(jogador.getNickname(),"iniciou sessão");
            escolherOpcaoMenuInicial();
        }
    }

    /**
     * recebe a opcao escolhida pelo utilizador e chama o metodo certo de acordo
     * com a opção escolhida pelo utilizador.
     */

    public static void escolherOpcaoMenuInicial() {
        int x = ap.telaInicial();
        switch (x) {
            case 1:
                comecarJogo(jogador);
                break;
            case 2:
                jogoSinglePlayer();
                break;
            case 3:
                organizarListagens();
                break;
            case 4:
                organizarReplays();
                break;
            case 5:
                organizarLog();
                break;
            case 6:
                System.out.println("saindo..");
                tp.calcularTempoExecucao();
                pressAnyKeyToContinue();
                log.fecharLog();
                escreverObj();
                System.exit(0);
                break;
            default:
                System.out.println("valor invalido");
                break;
        }
    }

    /**
     * Metodo responsavel por tudo envolvido no jogo Single Player.
     */

    public static void jogoSinglePlayer() {
        boolean desistiu = false;

        log.criarLog(jogador.getNickname(),"entrou num jogo single player");

        LocalDateTime tempoInicioTotal = LocalDateTime.now();
        int nrJogadas = 1, somaDuracao = 0;

        for (int x = 0; x < 2; x++) {
            if (verificarJogada(tab.gerarJogadaAleatoria()) != null)
                x--;
        }

        while (!tab.tabuleiroCompleto()) {
            LocalDateTime tempoInicio = LocalDateTime.now();

            tab.printTab();

            if (ap.lerInteiro("colocar jogada? (1 para continuar, 0 para desistir)") == 0){
                desistiu = true;
                break;
            }

            Jogada jogada = new Jogada(ap.lerLinhaColunaValor("linha (1 a 9):") - 1,
                    ap.lerLinhaColunaValor("coluna (1 a 9)") - 1,
                    ap.lerValorSinglePlayer("valor (0 a 9)"), false);

            String erro = verificarJogada(jogada);

            if (erro != null)
                System.out.println(erro);

            Duration duracao = Duration.between(tempoInicio.atZone(ZoneId.systemDefault()).toInstant(), Instant.now());
            somaDuracao += duracao.toSecondsPart();
            int tempoMedio = somaDuracao / nrJogadas;
            System.out.println("tempo total = " + somaDuracao);
            System.out.println("tempo da jogada: " + duracao.toSecondsPart() + " segundos");
            System.out.println("tempo medio das jogadas: " + tempoMedio + " segundos");
            System.out.println("numero de jogadas: " + nrJogadas++ + "\n");
            pressAnyKeyToContinue();
        }

        if (desistiu)
            System.out.println("Nao completou o tabuleiro!");
        else
            System.out.println("Completou o tabuleiro");

        Duration duracao = Duration.between(tempoInicioTotal.atZone(ZoneId.systemDefault()).toInstant(), Instant.now());
        System.out.println("Duracao do jogo: " + duracao.toSecondsPart());
        log.criarLog(jogador.getNickname(),"terminou jogo single player");
        pressAnyKeyToContinue();
        tab = new Tabuleiro();
        escolherOpcaoMenuInicial();
    }

    /**
     * Metodo que faz uso dos varios metodos para verificar linha, coluna e quadrado para verificar se
     * determinada jogada é possivel ser feita.
     * @param jogada Jogada a verificar.
     * @return String null caso seja introduzida com sucesso ou então String com msg de erro.
     */

    public static String verificarJogada(Jogada jogada) {

        if (tab.verificarLinha(jogada.getPosicaoX(), jogada.getValue())) {
            if (tab.verificarColuna(jogada.getPosicaoY(), jogada.getValue())) {
                if (tab.verificarQuadrado(jogada.getPosicaoX(), jogada.getPosicaoY(), jogada.getValue())) {
                    if (!tab.inserirJogada(jogada))
                        return "Valor Inicial (valor fixo)";
                } else
                    return "Valor repetido no quadrado";
            } else
                return "Valor reptido na coluna";
        } else
            return "valor repetido no linha";

        log.criarLog(jogador.getNickname(),"inseriu uma nova jogada na linha: "+jogada.getPosicaoX()+" , coluna: "+
                                        jogada.getPosicaoY()+" , valor: "+jogada.getValue());
        return null;
    }

    /**
     * Metodo que faz o utilizador clicar no enter para conseguir continuar.
     */

    private static void pressAnyKeyToContinue() {
        System.out.println("Clique Enter Para Continuar...");
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
    }

    /**
     * Metodo para começar jogo, interpreta a escolha feita no menu por parte do utilizador e chama
     * o metodo correspondente á escolha.
     * @param jogador
     */

    private static void comecarJogo(Jogador jogador) {
        GerirJogoMultiplayer gjm = new GerirJogoMultiplayer();
        ProtocolosMultiplayer pm = new ProtocolosMultiplayer();
        int escolha = ap.escolhaServerClient();
        switch (escolha) {
            case 1:
                gjm.juntarAoJogo(jogador, pm);
                break;
            case 2:
                gjm.esperarLigacao(jogador, pm);
                break;
        }
        escolherOpcaoMenuInicial();
    }

    /**
     * Metodo de distribuição das listagens de acordo com a escolha do utilizador.
     */

    public static void organizarListagens() {
        int opcao = ap.menuListagens();
        gj.lerJogadores();
        switch (opcao) {
            case 1:
                listarAlfabeticamente();
                break;
            case 2:
                listarNrVitorias();
                break;
            case 3:
                listarTempoJogo();
                break;
            case 4:
                listarJogosRealizados();
                break;
            case 5:
            default:
                escolherOpcaoMenuInicial();
        }
        organizarListagens();
    }

    /**
     * Metodo de distribuição das funções do log de acordo com a escolha do utilizador.
     */

    public static void organizarLog(){
        int opcao = ap.menuLog();
        switch (opcao){
            case 1:
                log.mostrarLog();
                break;
            case 2:
               String nome = ap.pedirNickname();
               log.mostrarLogUtilizador(nome);
               break;
            case 3:
            default:
                escolherOpcaoMenuInicial();
        }
        pressAnyKeyToContinue();
        organizarLog();
    }


    /**
     * Metodo de distribuição das funções de replay de acordo com a escolha do utilizador.
     */


    public static void organizarReplays(){
        int opcao = ap.menuReplay();
        switch(opcao){
            case 1:
                mostrarSequênciaJogadas(jogoAselecionar(ObterMeusJogos()));
                break;
            case 2:
                simulacaoJogadas(jogoAselecionar(ObterMeusJogos()));
                break;
            case 3:
            default:
                escolherOpcaoMenuInicial();
        }
        organizarReplays();
    }

    /**
     * Listar utilizadores alfabeticamente (de forma crescente ou decrescente).
     */

    public static void listarAlfabeticamente() {
        int opcao = ap.ordemListagem();
        switch (opcao) {
            case 1:
                if (gj.ordernarJogadores(true)) {
                    mostrarConteudo10linhas(gj.MostrarUtilizadores());
                    log.criarLog(jogador.getNickname(),"listou os utilizador por ordem crescente com sucesso");
                }
                else {
                    ap.imprimirMensagem("Erro ao ordenar jogadores");
                    log.criarLog(jogador.getNickname(),"Tentou listar os utilizador por ordem crescente sem sucesso");
                }

                break;
            case 2:
                if (gj.ordernarJogadores(false)) {
                    mostrarConteudo10linhas(gj.MostrarUtilizadores());
                    log.criarLog(jogador.getNickname(),"listou os utilizador por ordem decrescente com sucesso");
                }
                else {
                    ap.imprimirMensagem("Erro ao ordenar jogadores");
                    log.criarLog(jogador.getNickname(),"Tentou listar os utilizador por ordem decrescente sem sucesso");
                }

                break;
            default:
                organizarListagens();
        }
        pressAnyKeyToContinue();
    }

    /**
     * Listar utilizadores por numero de vitorias (de forma crescente ou decrescente).
     */

    public static void listarNrVitorias() {
        int opcao = ap.ordemListagem();
        switch (opcao) {
            case 1:
                Collections.sort(gj.getListaJogadores(), Comparator.comparing(Jogador::getNumeroVitorias));
                mostrarConteudo10linhas(gj.MostrarUtilizadores());
                log.criarLog(jogador.getNickname(),"listou os utilizador por numero de vitorias de forma crescente com sucesso");
                break;
            case 2:
                Collections.sort(gj.getListaJogadores(), Comparator.comparing(Jogador::getNumeroVitorias).reversed());
                mostrarConteudo10linhas(gj.MostrarUtilizadores());
                log.criarLog(jogador.getNickname(),"listou os utilizador por numero de vitorias de forma decrescente com sucesso");
                break;
            default:
                organizarListagens();
        }
        pressAnyKeyToContinue();
    }

    /**
     * Listar utilizadores por tempo de jogo (de forma crescente ou decrescente).
     */

    public static void listarTempoJogo() {
        int opcao = ap.ordemListagem();
        switch (opcao) {
            case 1:
                Collections.sort(gj.getListaJogadores(), Comparator.comparing(Jogador::getTempoJogo));
                mostrarConteudo10linhas(gj.MostrarUtilizadores());
                log.criarLog(jogador.getNickname(),"listou os utilizador por tempo de jogo de forma crescente com sucesso");
                break;
            case 2:
                Collections.sort(gj.getListaJogadores(), Comparator.comparing(Jogador::getTempoJogo).reversed());
                mostrarConteudo10linhas(gj.MostrarUtilizadores());
                log.criarLog(jogador.getNickname(),"listou os utilizador por tempo de jogo decrescente com sucesso");
                break;
            default:
                organizarListagens();
        }
        pressAnyKeyToContinue();
    }

    /**
     * Listar utilizadores por numero de jogos realizados (de forma crescente ou decrescente).
     */

    public static void listarJogosRealizados() {
        int opcao = ap.ordemListagem();
        switch (opcao) {
            case 1:
                Collections.sort(gj.getListaJogadores(), Comparator.comparing(Jogador::getNumeroJogos));
                mostrarConteudo10linhas(gj.MostrarUtilizadores());
                log.criarLog(jogador.getNickname(),"listou os utilizador por numero de jogos realizados de forma crescente " +
                                                        "com sucesso");
                break;
            case 2:
                Collections.sort(gj.getListaJogadores(), Comparator.comparing(Jogador::getNumeroJogos).reversed());
                mostrarConteudo10linhas(gj.MostrarUtilizadores());
                log.criarLog(jogador.getNickname(),"listou os utilizador por numero de jogos realizados de forma " +
                                                        "decrescente com sucesso");
                break;
            default:
                organizarListagens();
        }
        pressAnyKeyToContinue();
    }

    /**
     * Mostrar conteudo 10 linhas de cada vez.
     * @param dados dados a mostrar
     */

    public static void mostrarConteudo10linhas(String dados){
        String[] conteudo = dados.split("\n");
        for (int x = 0; x<conteudo.length; x++){
            if (conteudo[x] != null)
                ap.imprimirMensagem(conteudo[x]);
            else
                break;

            if ((x+1)%10==0){
                pressAnyKeyToContinue();
            }
        }
    }

    /**
     * Metodo responsável por congratular o jogador com mais vitórias no arranque da aplicação.
     */

    public static void congratularJogadorMaisVitorias(){
        gj.lerJogadores();
        Vector<Jogador> jogadores = gj.getListaJogadores();
            if (jogadores == null || jogadores.size() == 0){
                System.out.println("Crie os ficheiros");
                return;
            }
        Collections.sort(jogadores, Comparator.comparing(Jogador::getNumeroVitorias).reversed());
        ap.imprimirMensagem("Parabens \""+jogadores.get(0).getNickname()+"\" es o jogador com mais vitorias");
    }

    /**
     * Função para ler Objetos do tipo GereRegistoJogos.
     */

    private static void lerObj() {
        if (go.aberturaLeitura("registoJogos")) {
            grj = go.leituraFicheiro();
            if (grj != null) {
                System.out.println("Dados lidos com sucesso!");
            }
        } else
            System.out.println("ERRO!,objetos n foram lidos!");
        go.encerraLeitura();
    }

    /**
     * Função para guardar objetos do tipo GereRegistoJogos
     */

    private static void escreverObj() {
        if (go.aberturaEscrita("registoJogos", true)) {
            go.escreveFicheiro(grj);
            go.encerraEscrita();
            System.out.println("Objetos escritos com sucesso");
        } else
            System.out.println("Objetos n foram escritos!");
    }

    /**
     * metodo para obter os jogos que pertencem ao utilizador que está atualmente a jogar.
     * @return
     */

    private static Vector<RegistoJogo> ObterMeusJogos(){
        if (grj.getRegistoJogos() == null){
            System.out.println("nao existem jogos gravados");
        }
        Vector<RegistoJogo> meusJogos = new Vector<>();
        for (RegistoJogo rj : grj.getRegistoJogos()){
            if (rj.getNicknameCliente().equalsIgnoreCase(jogador.getNickname()) ||
                    rj.getNicknameServer().equalsIgnoreCase(jogador.getNickname())){
                meusJogos.add(rj);
            }
        }
        if (meusJogos != null && meusJogos.size()>0)
            return meusJogos;
        return null;
    }

    /**
     * Metodo que permite ao uitlizador escolher o jogo que pretende.
     * @param meusJogos Vetor com todos os jogos daquele utilizador
     * @return um objeto do RegistoJogo com o jogo pretendido pelo utilizador.
     */

    public static RegistoJogo jogoAselecionar(Vector<RegistoJogo> meusJogos){
        int x = 1;
        if (meusJogos == null || meusJogos.size() == 0)
            System.out.println("nao existem jogos registados");
        else {
            for (RegistoJogo jogo : meusJogos) {
                System.out.println("Jogo " + x++ + " na data " + jogo.getDataJogo());
            }
            int escolha;
            do {
                escolha = ap.lerInteiro("Qual deseja selecionar?(0 para sair)");
            }while (escolha < 0 || escolha > meusJogos.size());

            if (escolha== 0)
                escolherOpcaoMenuInicial();

            return meusJogos.get(escolha-1);
        }
        return null;
    }

    /**
     * Função para mostrar a sequência de jogadas de um determinado jogo.
     * @param jogo instância da classe RegistoJogo que tem o jogo pretendido.
     */

    public static void mostrarSequênciaJogadas(RegistoJogo jogo) {
        if (jogo == null)
            return;
        if (jogo.getJogadas() == null || jogo.getJogadas().size() == 0){
            System.out.println("Nao e possivel mostrar sequencia de jogadas");
         }else{
            int x = 0;
            for (Jogada jogada : jogo.getJogadas()){
                if (x == 0 || x== 1 || (x%2 == 0 && x != 2))
                   System.out.println(jogo.getNicknameServer()+" -> "+jogada);
                else if (x==2 || x==3 || x%2 !=0)
                    System.out.println(jogo.getNicknameCliente()+" -> "+jogada);

                x++;
                pressAnyKeyToContinue();
            }
        }
    }

    /**
     * Simulacao das jogadas dos utilizadores atraves da representação de cada jogada em tabuleiro.
     * @param jogo Jogo que se pretende simular.
     */

    public static void simulacaoJogadas(RegistoJogo jogo){
        if (jogo == null)
            return;
        if (jogo.getJogadas() == null || jogo.getJogadas().size() == 0)
            System.out.println("Nao e possivel mostrar a simulacao de jogadas");
        else{
            boolean inverter = true;
            int x = 0;
            Tabuleiro tabPlayer1 = new Tabuleiro();
            Tabuleiro tabPlayer2 = new Tabuleiro();
            for (Jogada jogada : jogo.getJogadas()){
                if (x == 0 || x == 1){
                    tabPlayer1.inserirJogada(jogada);
                }else if (x ==2 || x== 3) {
                    tabPlayer2.inserirJogada(jogada);
                } else if (jogo.getPrimeiroJogar() == 0){
                  if (inverter)
                      tabPlayer2.inserirJogada(jogada);
                  else
                      tabPlayer1.inserirJogada(jogada);
                  inverter = !inverter;

                } else if (jogo.getPrimeiroJogar() == 1){
                    if (!inverter)
                        tabPlayer2.inserirJogada(jogada);
                    else
                        tabPlayer1.inserirJogada(jogada);
                    inverter = !inverter;
                }
                x++;
                System.out.println("tabuleiro de "+jogo.getNicknameServer()+":");
                tabPlayer1.printTab();
                System.out.println("Tabuleiro de "+jogo.getNicknameCliente()+":");
                tabPlayer2.printTab();
                pressAnyKeyToContinue();
            }
            System.out.println("vencedor: "+jogo.getVencedor());
        }
    }
}
