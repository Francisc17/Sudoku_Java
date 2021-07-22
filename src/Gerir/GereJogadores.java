package Gerir;

import modelo.Jogador;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/**
 * Classe para gerir os jogadores.
 */

public class GereJogadores {

    private Properties prop;
    private GereFicheirosTexto gft;
    private Vector<Jogador> listaJogadores;

    /**
     * Construtor da classe, onde são iniciados o ficheiro properties "prop", uma instância da classe
     * GereFicheirosTexto "gft" e um vector com objetos do tipo Jogador "listaJogadores".
     */

    public GereJogadores() {
        prop = new Properties();
        gft = new GereFicheirosTexto();
        listaJogadores = new Vector<Jogador>();
    }

    /**
     * GETTER para o vetor com todos os jogadores
     * @return vetor com os jogadores.
     */

    public Vector<Jogador> getListaJogadores() {
        return listaJogadores;
    }

    /**
     * ler os jogadores do documento de texto e criar um objeto da classe Jogador com os dados lidos, ainda
     * neste metodo será chamado outro método para fazer a inserção do jogador criado no vetor.
     * @return boolean a confirmar a leitura.
     */

    public boolean lerJogadores() {
        if (gft.aberturaLeitura("jogadores.txt")) {
            try {
                if (listaJogadores != null) {
                    String line = gft.br.readLine();
                    listaJogadores.clear();                    //evitar jogadores duplicados
                    line = gft.br.readLine();                 //dar skip á linha criada pelo properties ao fazer store
                    while (line != null) {
                        String[] Separacao = line.split("[=]");
                        String[] Separacao2 = Separacao[1].split("[,]");
                        if (!inserirUtilizador(new Jogador(Separacao[0],Integer.valueOf(Separacao2[0]),
                                                Integer.valueOf(Separacao2[1]),Long.valueOf(Separacao2[2])))){
                            return false;
                        }
                        line = gft.br.readLine();
                    }
                    return true;
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Inserir um utilizador no vetor
     * @param jogador Jogador a adicionar.
     * @return boolean a confirmar a adição.
     */

    public boolean inserirUtilizador(Jogador jogador) {
        if (listaJogadores != null)
            return (listaJogadores.add(jogador));
        return false;
    }

    /**
     * Metodo para obter informação de todos os jogadores no vetor de jogadores.
     * @return String com a informação de todos os jogadores.
     */

    public String MostrarUtilizadores() {
        StringBuilder strb = new StringBuilder();
        if (listaJogadores != null && listaJogadores.size() > 0) {
            Enumeration<Jogador> utzEnumeration = listaJogadores.elements();
            while (utzEnumeration.hasMoreElements())
                strb.append(utzEnumeration.nextElement() + "\n");

            return strb.toString();
        }
        return null;
    }

    /**
     * Metodo que faz uso do Collection sort que vai utilizar o metodo CompareTo presente na classe Jogador para
     * organizar os vários jogadores alfabeticamente ( de forma crescente e descrescente ).
     * @param crescente boolean para diferenciar listagens crescentes e decrescentes.
     * @return boolean a confirmar a ordenação.
     */
    
    public boolean ordernarJogadores(boolean crescente) {
        if (listaJogadores != null && listaJogadores.size() > 1) {
            if (crescente)
                Collections.sort(listaJogadores);
            else
                Collections.sort(listaJogadores,Collections.reverseOrder());

            return true;
        }
        return false;
    }

    /**
     * Metodo que tem como obejtivo ir ao ficheiro de texto com os jogadores verificar se existe algum
     * com o nickname dado, fazendo para isso uso do properties e das suas propriedades.
     * @param nickname nickname do jogador a verificar.
     * @return Jogador gerado.
     */

    public Jogador verificarExistenciaJogador(String nickname){
            try {
                prop.load(new FileInputStream("jogadores.txt"));
                String nickDados = prop.getProperty(nickname);
                if (nickDados != null && nickDados.length() != 0){
                    String dadosSplit[] = nickDados.split(",");
                    gft.encerraLeitura();
                    return new Jogador(nickname, Integer.valueOf(dadosSplit[0]),Integer.valueOf(dadosSplit[1]),
                                       Integer.valueOf(dadosSplit[2]));
                }
            }catch (FileNotFoundException e) {
                System.out.println("ficheiro n existia, foi agora criado!");
            }catch (IOException e) {
                e.printStackTrace();
            }

            Jogador jogador = new Jogador(nickname,0,0,0);

            if (guardarNovoJogador(jogador))
                return jogador;
            else
                return null;
    }

    /**
     * metodo para guardar novo jogador no documento, este vai ser chamado pelo metodo de verificação de um jogador
     * caso o nickname fornecido no inicio do jogo seja a primeiro vez que é utilizado, havendo assim necessidade
     * de o acrescentar no documento de texto.
     * @param jogador Jogador a guardar.
     * @return boolean a confirmar.
     */

    public boolean guardarNovoJogador(Jogador jogador){
        String informacao = jogador.getNickname() + "=" + jogador.getNumeroJogos() + "," + jogador.getNumeroVitorias() +
                            "," + jogador.getTempoJogo() + "\n";

        if (!gft.aberturaEscrita("jogadores.txt",true))
            return false;

        if (!gft.escreveFicheiro(informacao))
            return false;

        gft.encerraEscrita();
        return true;
    }

    /**
     * Metodo para alterar o dado de um jogador através da key que é o nickname e do value que é
     * (nrJogos,nrVitorias,TempoTotalJogo).
     * @param key nickname do jogador.
     * @param value valor no formato x,y,z.
     * @return boolean a confirmar a alteração.
     */

    public boolean AlterarDadoJogador(String key,String value){
        if (!gft.aberturaLeitura("jogadores.txt"))
            return false;

        try {
            prop.load(new FileInputStream("jogadores.txt"));
            prop.setProperty(key,value);
            prop.store(new FileOutputStream("jogadores.txt"),null);
            gft.encerraLeitura();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
