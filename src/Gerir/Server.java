package Gerir;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Classe que representa o servidor na conexão por sockets.
 */

public class Server{
    private ServerSocket server;

    /**
     * Construtor de Server, onde é iniciado o ServerSocket.
     * @param port porta á qual o cliente se vai ligar.
     */

    public Server(int port) {
        try {
            server = new ServerSocket(port);
        } catch(IOException i) {
            System.out.println(i);
        }
    }

    /**
     * Obter ligação socket, fica á espera da ligação do cliente.
     * @return Socket da ligação estabelecida (caso se estabeleça).
     */

    public Socket obterSocketLigacao() { //FALTA O TIMEOUT!!!!!!!
        try {
            return server.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * fechar o socket servidor.
     * @return boolean a confirmar a ação.
     */

    public boolean close(){          //FALTA VER ISTO TAMBÉM!
        if (server != null && !server.isClosed())
            try {
                server.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        return false;
    }
}
