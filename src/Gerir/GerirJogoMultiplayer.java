package Gerir;

import Interacao.Aplicacao;
import main.Main;
import modelo.*;

import java.io.*;
import java.net.Socket;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Properties;

/**
 * Classe focada em tudo relacionado com o jogo multiplayer, é aqui que se inicia a comunicação
 *  e se relaliza o jogo multiplayer.
 */

public class GerirJogoMultiplayer {
    private Aplicacao ap = new Aplicacao();
    private Tabuleiro tab1;
    private Tabuleiro tab2;
    private int nrJogadas;
    private RegistoJogo rj;
    private Jogada jgdTemp;

    /**
     * Metodo associado ao cliente pois a este vai ser o host e porta da máquina a que se vai ligar
     * e irá ser feita a tentativa de ligação enquanto o servidor espera pela ligação.
     * @param jogador Jogador que se está a ligar.
     * @param pm instância da classe Protocolos multiplayer.
     */

    public void juntarAoJogo(Jogador jogador,ProtocolosMultiplayer pm){
        Client client = new Client(Aplicacao.lerString("Host: "),Aplicacao.lerInteiro("Port:"));
        if(client.estadoConexao()) {
            System.out.println("Conectado...");
            Main.log.criarLog(jogador.getNickname(),"conectou-se ao servidor e juntou-se a um jogo.");
            comecarComoCliente(client, pm, jogador);
        }
    }

    /**
     * Metodo associado ao servidor que vai iniciar uma instância da classe Server e vai esperar por uma ligação.
     * @param jogador Jogador que está á espera da ligação.
     * @param pm instânca da classe Protocolos multiplayer.
     */

    public void esperarLigacao(Jogador jogador, ProtocolosMultiplayer pm){
        Server server = new Server(Aplicacao.lerInteiro("Port"));
        Socket socket = server.obterSocketLigacao();
        if (socket != null && socket.isConnected()){
            Client cliente = new Client(socket);
            if (cliente.estadoConexao()) {
                System.out.println("Conectado...");
                Main.log.criarLog(jogador.getNickname(),"houve um jogador que se conectou e o jogo foi iniciado");
                comecarComoServer(cliente,pm,jogador);
            }
        }
    }

    /**
     * Método responsáel por tudo tudo que é interpretação de mensagens, é um dos metodos principais desta classe, é
     * aqui que quase tudo será feito desde chamar funções essenciais para cumprir alguns objetios da aplicação
     * como obterIp() e esperarHelloRetorno() mas também é feita a distribuição dos valores recebido no socket
     * e de acordo com esse valor é feito algo especifico. Este método está associado ao server e o modo de
     * funcionamento é baseado na regra das ligações de socket: a regra diz que quando um dos lados está
     * a escrever e vai enviar algo o outro lado deve estar á escuta.
     * @param cliente Cliente da ligação (o servidor é simultaneamente servidor e cliente).
     * @param pm instância da classe ProtocolosMultiplayer.
     * @param jgd Jogador que está a jogar.
     */

    public void comecarComoServer(Client cliente, ProtocolosMultiplayer pm,Jogador jgd){
        rj = new RegistoJogo();
        rj.registarData();
        rj.setNicknameServer(jgd.getNickname());
        pm.obterIp(ap);
        Main.log.criarLog(jgd.getNickname(),"foi mostrado o ip e o host da maquina local.");
        String comando;
        esperarHelloRetorno(cliente,pm,jgd);
        Jogada[] recebeJogadas = ap.inseriJogadasIniciaisSocket();
        cliente.escrever(inserirComandoJogadasIniciais(jgd,recebeJogadas));
        criarTabuleiroPlayer2(recebeJogadas);
        LocalDateTime tempoInicioTotal = LocalDateTime.now();
        while (true) {
            if (cliente != null && cliente.estadoConexao()) {
                if (tab1 != null && tab2 != null)
                    if (tab2.tabuleiroCompleto()){
                        cliente.escrever("<"+jgd.getNickname()+"> <status> <win>;");
                    }
                    if ((comando = cliente.ler()) == null){
                        ap.imprimirMensagem("O seu adversario se deconectou");
                        cliente.close();
                        break;
                    }
                    ap.imprimirMensagem("comando recebido -> "+comando);
                    String x = pm.distribuiPorValor(comando);
                    if (x.equalsIgnoreCase("bye")) {
                        ap.imprimirMensagem("O seu adversario se deconectou");
                        cliente.close();
                    }
                    if (x.equalsIgnoreCase("setup")) {
                        setupServer(cliente,pm,jgd);
                    }
                    if (x.equalsIgnoreCase("start")){
                        fazerEscolha(cliente,jgd);
                    }
                    if (x.equalsIgnoreCase("play")){
                        System.out.println(pm.obterValoresCommand().get(2));
                        validarJogadaRecebida(pm.obterValoresCommand().get(2),cliente,jgd,pm.obterNickname());
                    }
                    if (x.equalsIgnoreCase("result")){
                        opcoesResult(pm.obterValoresCommand().get(2),cliente,jgd);
                    }
                    if (x.equalsIgnoreCase("status")){
                        Duration duracao = Duration.between(tempoInicioTotal.atZone(ZoneId.systemDefault()).toInstant(), Instant.now());
                        tratarStatus(pm.obterValoresCommand().get(2),cliente,jgd,false,duracao);
                    }
                    if (x.equalsIgnoreCase("new")){
                        tratarNew(pm.obterValoresCommand().get(2),cliente,jgd);
                    }
                    if (x.equalsIgnoreCase("withdraw")){
                        Duration duracao = Duration.between(tempoInicioTotal.atZone(ZoneId.systemDefault()).toInstant(), Instant.now());
                        tratarWithdraw(cliente,jgd,pm,false,duracao);
                    }
                    if (x.equalsIgnoreCase("ready")){
                        cliente.escrever("<"+jgd.getNickname()+"> <new> <?>;");
                    }
            }else
                break;
        }
    }

    /**
     * Método responsáel por tudo tudo que é interpretação de mensagens, é um dos metodos principais desta classe, é
     * aqui que quase tudo será feito desde chamar funções essenciais para cumprir alguns objetivos da aplicação
     * como obterIp() mas também é feita a distribuição dos valores recebido no socket
     * e de acordo com esse valor é feito algo especifico. Este método está associado ao cliente na ligação socket
     * e o modo de  funcionamento é baseado na regra das ligações de socket: a regra diz que quando um dos lados está
     * a escrever e vai enviar algo o outro lado deve estar á escuta.
     * @param cliente Cliente associado á ligação socket.
     * @param pm instância da classe Protocolos multiplayer.
     * @param jgd Jogador associado.
     */

    public void comecarComoCliente(Client cliente, ProtocolosMultiplayer pm, Jogador jgd) {
        rj = new RegistoJogo();
        rj.registarData();
        rj.setNicknameCliente(jgd.getNickname());
        pm.obterIp(ap);
        Main.log.criarLog(jgd.getNickname(),"mostrou o ip e host da máquina local.");
        LocalDateTime tempoInicioTotal = LocalDateTime.now();
        String comando;
            while (true) {
                    if (cliente != null && cliente.estadoConexao()) {
                        if (tab1 != null && tab2 != null)
                            if (tab2.tabuleiroCompleto()) {
                                cliente.escrever("<" + jgd.getNickname() + "> <status> <win>;");
                            }
                        if ((comando = cliente.ler())==null){
                            ap.imprimirMensagem("Seu adversario se desconectou");
                            cliente.close();
                            pressAnyKeyToContinue();
                            break;
                        }
                        ap.imprimirMensagem("comando recebido -> "+comando);
                        String x = pm.distribuiPorValor(comando);
                        if (x.equalsIgnoreCase("hello")) {
                            cliente.escrever("<"+jgd.getNickname()+"> <hello>;");
                            ap.imprimirMensagem("Nickname do adversário -> " + pm.obterNickname());
                            rj.setNicknameServer(pm.obterNickname());
                        }
                        if (x.equalsIgnoreCase("setup")) {
                            setupCliente(cliente, pm, jgd);
                        }
                        if (x.equalsIgnoreCase("bye")){
                            ap.imprimirMensagem("O seu adversario se deconectou");
                            cliente.close();
                        }
                        if (x.equalsIgnoreCase("start")) {
                            String valor = pm.obterValoresCommand().get(2);
                            if (valor.equalsIgnoreCase("0"))
                                fazerEscolha(cliente, jgd);
                            else {
                                cliente.escrever("<" + jgd.getNickname() + "> <start> <ok>;");
                            }
                        }
                        if (x.equalsIgnoreCase("play")) {
                            validarJogadaRecebida(pm.obterValoresCommand().get(2), cliente, jgd,pm.obterNickname());
                        }
                        if (x.equalsIgnoreCase("result")) {
                            opcoesResult(pm.obterValoresCommand().get(2), cliente, jgd);
                        }
                        if (x.equalsIgnoreCase("status")) {
                            Duration duracao = Duration.between(tempoInicioTotal.atZone(ZoneId.systemDefault()).toInstant(), Instant.now());
                            tratarStatus(pm.obterValoresCommand().get(2), cliente, jgd, true, duracao);
                        }
                        if (x.equalsIgnoreCase("new")) {
                            tratarNew(pm.obterValoresCommand().get(2), cliente, jgd);
                        }
                        if (x.equalsIgnoreCase("withdraw")) {
                            Duration duracao = Duration.between(tempoInicioTotal.atZone(ZoneId.systemDefault()).toInstant(), Instant.now());
                            tratarWithdraw(cliente, jgd, pm, true, duracao);
                        }
                    } else
                        break;

            }
    }

    /**
     * Metodo para o servidor esperar o comando hello de volta por parte do client, enquanto não obtiver o hello
     * ele irá enviar novamente hello para o cliente até este responder com o comando certo.
     * @param cliente Cliente da ligação.
     * @param pm instância da classe ProtocolosMultiplayer.
     * @param jgd Jogador associado.
     */

    public void esperarHelloRetorno(Client cliente,ProtocolosMultiplayer pm,Jogador jgd){
        String comando;
        while (true)
            if (cliente != null && cliente.estadoConexao()){
                cliente.escrever(pm.retornarHello(jgd.getNickname()));
                comando = cliente.ler();
                String x = pm.distribuiPorValor(comando);
                if (x.equalsIgnoreCase("hello") && x != null) {
                    System.out.println(comando);
                    ap.imprimirMensagem("Nickname do adversário ->  " + pm.obterNickname());
                    rj.setNicknameCliente(pm.obterNickname());
                    Main.log.criarLog(jgd.getNickname(),"recebeu o comando: "+comando);
                    break;
                }
            }else
                break;
    }

    /**
     * Metodo responsável por criar o comando com as jogadas iniciais já prontas a serem enviadas
     * para a ligação do socket.
     * @param jgd Jogador associado ás jogadas
     * @param recebeJogadas Vetor que contem as jogadas iniciais criadas.
     * @return String com o comando pronto a enviar na ligação socket.
     */

    public String inserirComandoJogadasIniciais(Jogador jgd,Jogada[] recebeJogadas){
        rj.adicionarJogada(recebeJogadas[0]);
        rj.adicionarJogada(recebeJogadas[1]);

        return  "<"+jgd.getNickname()+"> "+"<setup> "+"<l"+recebeJogadas[0].getPosicaoX()+"|c"+
                                recebeJogadas[0].getPosicaoY()+":v"+recebeJogadas[0].getValue()+" l"+
                                recebeJogadas[1].getPosicaoX()+"|c"+recebeJogadas[1].getPosicaoY()+":v"+
                                recebeJogadas[1].getValue()+">;";
    }

    /**
     * Metodo responsável por chamar o metodo onde é apresentado os menus para criação das varias jogadas iniciais e
     * posteriormente serão devolvidas essas mesmas jogads criadas num vetor.
     * @param pm instância da classe ProtocolosMultiplayer.
     * @return Vetor com as jogadas pretendidas.
     */

    public Jogada[] receberJogadasIniciais(ProtocolosMultiplayer pm){
       List<String> listString = pm.obterValoresCommand();
       String valor = listString.get(2);


       Jogada jogadaInicial1 = new Jogada((Character.getNumericValue(valor.charAt(1))),
                                        (Character.getNumericValue(valor.charAt(4))),
                                        (Character.getNumericValue(valor.charAt(7))),true);
       Jogada jogadaInicial2 = new Jogada(Character.getNumericValue(valor.charAt(10)),
                                          Character.getNumericValue(valor.charAt(13)),
                                          Character.getNumericValue(valor.charAt(16)),true);

       Jogada[] jogadas = {jogadaInicial1,jogadaInicial2};

       rj.adicionarJogada(jogadaInicial1);
       rj.adicionarJogada(jogadaInicial2);

       return jogadas;
    }

    /**
     * Criar tabuleiro para o o jogador 1, neste caso o jogador 1 é sempre o jogador que está a jogar no máquina
     * local.
     * @param jogadas1 Jogadas iniciais para colocar no tabuleiro.
     * @return boolean a confirmar a criação.
     */

    public boolean criarTabuleiroPlayer1(Jogada jogadas1[]){
        tab1 = new Tabuleiro();
        System.out.println(jogadas1[0].getPosicaoX()+" "+jogadas1[1].getPosicaoY());
        if (tab1.inserirJogada(jogadas1[0]) && tab1.inserirJogada(jogadas1[1]))
            return true;
        return false;
    }

    /**
     * Criar tabuleiro para o o jogador 2, neste caso o jogador 2 é sempre o adversário que está a jogar no máquina
     * remota.
     * @param jogadas2 Jogadas iniciais para colocar no tabuleiro.
     * @return boolean a confirmar a criação.
     */

    public boolean criarTabuleiroPlayer2(Jogada jogadas2[]){
        tab2 = new Tabuleiro();
        if (tab2.inserirJogada(jogadas2[0]) && tab2.inserirJogada(jogadas2[1]))
            return true;
        return false;
    }

    /**
     * Mostrar tabuleiros
     */

    public void mostrarTabuleiros(){
        System.out.println("SEU TABULEIRO: \n");
        tab1.printTab();
        System.out.println("TABULEIRO DO SEU ADVERSARIO: \n");
        tab2.printTab();
    }

    /**
     * Metodo que organiza algumas funções iniciais do cliente.
     * @param cliente cliente da ligação.
     * @param pm instância da classe ProtocolosMultiplayer.
     * @param jgd jogador associado.
     */

    public void setupCliente(Client cliente, ProtocolosMultiplayer pm,Jogador jgd){
        criarTabuleiroPlayer1(receberJogadasIniciais(pm));
        Jogada[] jogadasadv = ap.inseriJogadasIniciaisSocket();
        cliente.escrever(inserirComandoJogadasIniciais(jgd,jogadasadv));
        criarTabuleiroPlayer2(jogadasadv);
        mostrarTabuleiros();
    }

    /**
     * Metodo que organiza algumas funções iniciais do server.
     * @param cliente cliente associado.
     * @param pm instância da classe ProtocolosMultiplayer.
     * @param jgd jogador associado.
     */

    public void setupServer(Client cliente, ProtocolosMultiplayer pm,Jogador jgd){
        criarTabuleiroPlayer1(receberJogadasIniciais(pm));
        mostrarTabuleiros();
        int x = tab1.rand(0,1);
        rj.setPrimeiroJogar(x);
        cliente.escrever("<"+jgd.getNickname()+"> "+"<start> "+"<"+x+">;");
    }

    /**
     * Metodo que obriga o utilizador a clicar no enter para conseguir continuar.
     */

    private static void pressAnyKeyToContinue() {
        System.out.println("Clique Enter Para Continuar...");
        try {
            System.in.read();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo que distribui as várias opções presentes no menu de escolha para cada jogada que o jogador faz.
     * @param cliente cliente associado.
     * @param jgd jogador associado.
     */

    public void fazerEscolha(Client cliente,Jogador jgd){
        int escolha = ap.menuInserirJogadaDesistir();
        switch (escolha){
            case 1:
                enviarJogada(cliente,jgd);
                break;
            case 2:
                mostrarTabuleiros();
                pressAnyKeyToContinue();
                fazerEscolha(cliente,jgd);
                break;
            case 3:
                desistir(cliente,jgd);
                break;
            default:
                ap.imprimirMensagem("valor invalido");
                fazerEscolha(cliente,jgd);
        }
    }

    /**
     * Metodo que distribui as várias opções que podem vir no comando status.
     * @param valor Valor lido depois do comando status.
     * @param cliente cliente associado.
     * @param jgd jogador associado.
     * @param iscliente boolean para perceber se quem está a utilizar o metodo é servidor ou cliente.
     * @param tempoJogo tempo de jogo associado ao jogador (necessário para ser passado para outro metodo).
     */

    public void tratarStatus(String valor,Client cliente,Jogador jgd,boolean iscliente,Duration tempoJogo){
        if (valor.equalsIgnoreCase("win")){
            alterarDadosJogador(jgd,false,tempoJogo);
            cliente.escrever("<"+jgd.getNickname()+"> <status> <ok>;");
        }
        if (valor.equalsIgnoreCase("ok") && iscliente){
            alterarDadosJogador(jgd,true,tempoJogo);
            cliente.escrever("<"+jgd.getNickname()+"> <status> <ack>;");
        }

        if (valor.equalsIgnoreCase("ok") && !iscliente){
            alterarDadosJogador(jgd,true,tempoJogo);
            cliente.escrever("<"+jgd.getNickname()+"> <new> <?>;");
        }

        if (valor.equalsIgnoreCase("ack")){
            cliente.escrever("<"+jgd.getNickname()+"> <new> <?>;");
        }
    }

    /**
     * Metodo que distribui as várias opções que podem vir no comando new.
     * @param valor valor lido depois do comando new.
     * @param cliente cliente associado.
     * @param jgd jogador associado.
     */

    public void tratarNew(String valor, Client cliente, Jogador jgd){
        if (valor.equalsIgnoreCase("?")){
            int opcao = ap.menuJogarDnv();
            switch (opcao){
                case 1:
                    cliente.escrever("<"+jgd.getNickname()+"> <new> <y>;");
                    comecarComoCliente(cliente,new ProtocolosMultiplayer(),jgd);
                    break;
                case 2:
                    cliente.escrever("<"+jgd.getNickname()+"> <new> <n>;");
                    break;
                default:
                    tratarNew(valor,cliente,jgd);
            }
        }
        if (valor.equalsIgnoreCase("y")){
            comecarComoServer(cliente,new ProtocolosMultiplayer(),jgd);
        }

        if (valor.equalsIgnoreCase("n")){
            cliente.escrever(("<"+jgd.getNickname()+"> <bye>"));
            cliente.close();
        }
    }

    /**
     * Metodo responsável por enviar uma jogada para a ligação socket e incrementar o numero de jogadas.
     * @param cliente cliente associado.
     * @param jgd jogador associado.
     */

    public void enviarJogada(Client cliente,Jogador jgd){
        int linha = ap.lerLinhaColunaValor("linha: ");
        int col = ap.lerLinhaColunaValor("coluna: ");
        int valor = ap.lerValorSinglePlayer("valor: ");
        jgdTemp = new Jogada(linha-1,col-1,valor,false);

        cliente.escrever( "<"+jgd.getNickname()+"> <play> <"+linha+"|"+col+":"+valor+">;");

        nrJogadas++;
        Main.log.criarLog(jgd.getNickname(),"inseriu uma nova jogada na linha: "+linha++ +" , coluna: "+col++ +" , valor: "+valor);
    }

    /**
     * Metodo para validar jogada recebida na linha, coluna, quadrado e se é ou n uma posição fixa, se tudo correr bem
     * é enviada uma mensagem a dizer que a jogada é valida, se não é enviada uma mensagem a dizer que não é válida.
     * @param comandoJogada comando recebido (jogada recebida em forma de comando).
     * @param cliente cliente associado.
     * @param jgd jogador associado.
     */

    public void validarJogadaRecebida(String comandoJogada,Client cliente,Jogador jgd,String nomeAdv){
        int linha = Character.getNumericValue(comandoJogada.charAt(0))-1;
        int col = Character.getNumericValue(comandoJogada.charAt(2))-1;
        int valor = Character.getNumericValue(comandoJogada.charAt(4));

        if (tab2.verificarLinha(linha,valor)) {
            if (tab2.verificarColuna(col,valor)) {
                if (tab2.verificarQuadrado(linha,col,valor)) {
                    if (tab2.inserirJogada(new Jogada(linha,col,valor,false))) {
                        Main.log.criarLog(nomeAdv,"inseriu uma jogada na linha: "+linha++ +" , coluna: "+col++ +" , valor: "+valor);
                        cliente.escrever("<" + jgd.getNickname() + "> <result> <valid>;");
                        Main.log.criarLog(jgd.getNickname(),"jogada recebida validada");
                        rj.adicionarJogada(new Jogada(linha-1,col-1,valor,false));
                        return;
                    }else
                        System.out.println("problema a inserir");
                }else
                    System.out.println("problema no quadrado");
            }else
                System.out.println("problema na coluna");
        }else
            System.out.println("problema na linha");

        Main.log.criarLog(nomeAdv,"inseriu uma jogada na linha: "+linha++ +" , coluna: "+col++ +" , valor: "+valor);
        Main.log.criarLog(jgd.getNickname(),"jogada recebida rejeitada");
        cliente.escrever("<"+jgd.getNickname()+"> <result> <notvalid>;");
    }

    /**
     * Metodo que distribui as várias opções que podem vir no comando result
     * @param commandParametro valor lido depois do comando result.
     * @param cliente cliente associado.
     * @param jgd jogador associado.
     */

    public void opcoesResult(String commandParametro,Client cliente,Jogador jgd){
        if (commandParametro.equalsIgnoreCase("valid")) {
            tab1.inserirJogada(jgdTemp);
            rj.adicionarJogada(jgdTemp);
            cliente.escrever("<" + jgd.getNickname() + "> <result> <ack>;");
        }
        else if (commandParametro.equalsIgnoreCase("notvalid"))
            enviarJogada(cliente,jgd);
        else if (commandParametro.equalsIgnoreCase("ack"))
            fazerEscolha(cliente,jgd);
    }

    /**
     * Metodo que permite a um jogador desistir de um jogador.
     * @param cliente cliente associado.
     * @param jgd jogador associado.
     */

    public void desistir(Client cliente,Jogador jgd){
        cliente.escrever("<"+jgd.getNickname()+"> <withdraw>;");
        Main.log.criarLog(jgd.getNickname(),"desistiu do jogo");
    }

    /**
     * Metodo que distribui as várias opções que podem vir no comando Withdraw
     * @param cliente cliente associado.
     * @param jgd jogador associado.
     * @param pm instância da classe ProtocolosMultiplayer.
     * @param isclient boolean para saber se quem está a aceder ao método é cliente ou servidor.
     * @param duration duração do jogo.
     */

    public void tratarWithdraw(Client cliente,Jogador jgd,ProtocolosMultiplayer pm,boolean isclient,Duration duration){
        if (pm.obterValoresCommand().get(2) == null || pm.obterValoresCommand().get(2).isEmpty()){
            cliente.escrever("<"+jgd.getNickname()+"> <withdraw> <ack>;");
            alterarDadosJogador(jgd,true,duration);
        }else {
            if (!isclient)
                cliente.escrever("<" + jgd.getNickname() + "> <new> <?>;");
            else
                cliente.escrever("<" + jgd.getNickname() + "> <ready>;");

            alterarDadosJogador(jgd,false,duration);
            rj.setVencedor(pm.obterNickname());
        }
    }

    /**
     * Metodo que permite a atualização dos dados do jogador depois que termina um jogo.
     * @param jgd jogador associado.
     * @param vencedor boolean que diz se este foi ou não vencedor.
     * @param tempoJogo tempo total de jogo.
     */

    public void alterarDadosJogador(Jogador jgd, boolean vencedor, Duration tempoJogo){
        GereJogadores gj = new GereJogadores();
        String key = jgd.getNickname();
        int nrJogos,nrVitorias;
        long totalTempoJogo;
        nrJogos = jgd.getNumeroJogos() + 1;
        if (vencedor) {
            rj.setVencedor(jgd.getNickname());
            nrVitorias = jgd.getNumeroVitorias() + 1;
            Main.log.criarLog(jgd.getNickname(),"venceu a partida");
        } else {
            nrVitorias = jgd.getNumeroVitorias();
            Main.log.criarLog(jgd.getNickname(),"perdeu a partida");
        }

        totalTempoJogo = jgd.getTempoJogo() + tempoJogo.toMinutesPart();

        String value = +nrJogos+","+nrVitorias+","+totalTempoJogo;

        if (gj.AlterarDadoJogador(key,value))
            jgd = gj.verificarExistenciaJogador(key);

        statsJogo(jgd,tempoJogo.toMinutesPart());
    }

    /**
     * Metodo para atualizar as stats caso algum recorde seja batido.
     * @param jgd jogador associado.
     * @param duracaoJogo duração do jogo.
     */

    public void statsJogo(Jogador jgd,long duracaoJogo){
        Main.grj.adicionarRegisto(rj);
        System.out.println(Main.grj.getRegistoJogos().size());
        GereFicheirosTexto gft = new GereFicheirosTexto();
        if (!gft.aberturaLeitura("jogoStats.txt"))
            return;
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream("jogoStats.txt"));
            String jogoMaisRapido = prop.getProperty("mais_rapido");
            String maisVitorias = prop.getProperty("mais_vitorias");
            String numeroJogadas = prop.getProperty("menos_jogadas");

            if (duracaoJogo < Long.valueOf(jogoMaisRapido)) {
                ap.imprimirMensagem("Notificacao: Parabens! voce bateu o recorde de jogo mais rapido!");
                Main.log.criarLog(jgd.getNickname(),"bateu o recorde de jogo mais rapido!");
                pressAnyKeyToContinue();
                alterarDadoJogo("mais_rapido", String.valueOf(duracaoJogo));
            }

            if (jgd.getNumeroVitorias() > Long.valueOf(maisVitorias)) {
                System.out.println("Notificacao: Parabens! voce agora e o jogador com maior numero de vitorias");
                Main.log.criarLog(jgd.getNickname(),"bateu o recorde de maior numero de vitorias!");
                pressAnyKeyToContinue();
                alterarDadoJogo("mais_vitorias", String.valueOf(jgd.getNumeroVitorias()));
            }

            if (nrJogadas < Integer.valueOf(numeroJogadas)) {
                ap.imprimirMensagem("Notificacao: Parabens! voce bateu o recorde de jogo com o menor numero de jogadas!");
                ap.imprimirMensagem("NUMERO DE JOGADAS:"+nrJogadas);
                Main.log.criarLog(jgd.getNickname(),"bateu o recorde de jogo com o menor numero de jogadas");
                pressAnyKeyToContinue();
                alterarDadoJogo("menos_jogadas", String.valueOf(nrJogadas));
            }

            gft.encerraLeitura();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo responsável por alterar um dados sobre estatitisticas relacionadas ao jogo caso algumas delas seja
     * ultrapassada e tenha um novo valor recorde.
     * @param key key da propriedade, nome da propriedaade batida.
     * @param value novo valor para essa propriedade.
     */

    public void alterarDadoJogo(String key,String value){
        Properties prop = new Properties();

        try {
            prop.load(new FileInputStream("jogoStats.txt"));
            prop.setProperty(key,value);
            prop.store(new FileOutputStream("jogoStats.txt"),null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
