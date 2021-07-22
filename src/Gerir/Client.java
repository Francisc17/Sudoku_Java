package Gerir;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;

/**
 * Classe que represente o client numa ligação socket
 */

public class Client {

    private BufferedReader br;
    private PrintWriter pw;
    private Socket socket;

    /**
     * Construtor de Cliente, inicia o socket, bufferedReader e PrintWriter.
     * @param host host da maquina ao qual se vai ligar
     * @param port porta da maquina ao qual se vai ligar
     */

    public Client(String host, int port) {
        try {
            socket = new Socket(host, port);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            pw = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Construtor de Client, este construtor é usado pelo servidor, que é simultaneamente servidor e cliente.
     * @param socket Socket da ligação.
     */

    public Client(Socket socket) {
        this.socket = socket;

        try {
            pw = new PrintWriter(socket.getOutputStream(), true);
            br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Escrever na ligação de sockets
     * @param msg mensagem a escrever.
     */

    public void escrever(String msg) {
        if (socket != null && socket.isConnected() && msg != null && pw != null) {
            pw.println(msg);
        }
    }

    /**
     * Ler o que foi escrito na ligação socket.
     * @return String com o conteudo.
     */

    public String ler() {
        if (socket != null && socket.isConnected() && br != null) {
            try {
                StringBuilder sb = new StringBuilder();
                String inputLine;
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                    if (inputLine.contains(";")) break;
                }

                return sb.toString();
            }catch (SocketException se){
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Fechar ligação do socket.
     * @return boolean a confirmar a ação.
     */

    public boolean close(){
        if (socket != null && !socket.isClosed()){
            try {
                socket.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    /**
     * verificar o estado da conexão
     * @return boolean a informar o estado da conexão.
     */

    public boolean estadoConexao(){
        if (socket == null)
            return false;
        return socket.isConnected() && !socket.isClosed();
    }

}

