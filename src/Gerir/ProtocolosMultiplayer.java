package Gerir;

import Interacao.Aplicacao;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Vector;

/**
 * Classe com metodos essenciais para cumprir todos os protocolos do multiplayer.
 */

public class ProtocolosMultiplayer {

    private String command = "";

    /**
     * Metodo necessario para obter os valores necessários presentes num comando para
     * o mesmo ser interpretado.
     * @return List de Strings com os vários valores presentes num comando separados em cada posição da lista.
     */

    public List<String> obterValoresCommand(){
        List<String> valoresCommand = new Vector<>();

        String[] values = command.split(">");
        for (String str : values){
            valoresCommand.add(str.trim().substring(1));
        }

        return valoresCommand;
    }

    /**
     * Faz uso dos valores na lista retornada em obterValoresCommand para interpretar parte do comando e retornar o tipo
     * de comando em string para este ser tratado posteriormente em outro método.
     * @param comando commando recebido na ligação socket.
     * @return String com parte do comando necessária para o mesmo ser posteriormente interpretado.
     */

    public String distribuiPorValor(String comando){
        if (comando != null && comando.length() > 0) {
            command = comando;
            List<String> obterValoresCommand = obterValoresCommand();
            if (obterValoresCommand.size() >= 2) { //se a lista tiver size >= 2 então tem lá o que é preciso
                String str = obterValoresCommand.get(1);
                if (str.equalsIgnoreCase("hello"))
                    return "hello";
                else if (str.equalsIgnoreCase("bye"))
                    return "bye";
                else if (str.equalsIgnoreCase("setup"))
                    return "setup";
                else if (str.equalsIgnoreCase("start"))
                    return "start";
                else if (str.equalsIgnoreCase("play"))
                    return "play";
                else if (str.equalsIgnoreCase("result"))
                    return "result";
                else if (str.equalsIgnoreCase("status"))
                    return "status";
                else if (str.equalsIgnoreCase("new"))
                    return "new";
                else if (str.equalsIgnoreCase("withdraw"))
                    return "withdraw";
                else if (str.equalsIgnoreCase("ready")){
                    return "ready";
                }
            }
        }
        return null;
    }

    /**
     * obter nickname presente num comando.
     * @return String com o nickname.
     */

    public String obterNickname(){
        List<String> valoresCommand = obterValoresCommand();

        if (valoresCommand != null && valoresCommand.size() > 0)
            return valoresCommand.get(0);
        return null;
    }

    /**
     * Metodo para retornar Hello.
     * @param nickname nickname do jogador.
     * @return String com o comando pronto a enviar pela ligação do socket.
     */

    public String retornarHello(String nickname){
        if (nickname != null)
            return "<"+nickname+"> "+"<hello>;";
        return "<\"\">;";
    }

    /**
     * Obter Host e Ip da máquina local.
     * @param ap instância da classe Aplicação, necessário para apresentar tanto o ip como o nome do Host.
     */

    public void obterIp(Aplicacao ap){
        InetAddress inetAddress = null;
        try {
            inetAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            ap.imprimirMensagem("nao e possivel obter ip");
        }
        ap.imprimirMensagem("IP Address:- " + inetAddress.getHostAddress());
        ap.imprimirMensagem("Host Name:- " + inetAddress.getHostName());
    }

}
