package Gerir;

import java.io.*;

/**
 * Classe necessária para gerir o acesso a ficheiro de objeto.
 */

public class GereFicheiroObjeto {


    // acesso ficheiro
    File ficheiroLeitura;
    File ficheiroEscrita;
    // leitura ficheiro
    FileInputStream fis;
    ObjectInputStream ois;

    // escrita ficheiro
    FileOutputStream fos;
    ObjectOutputStream oos;

    /**
     * abertura do ficheiro para leitura.
     * @param aCaminho caminho onde se encontra o ficheiro.
     * @return boolean a confirmar a ação.
     */

    public boolean aberturaLeitura(String aCaminho) {

        if (aCaminho != null && aCaminho.length() > 0) {
            ficheiroLeitura = new File(aCaminho);

            if (ficheiroLeitura.exists()) {
                try {
                    fis = new FileInputStream(ficheiroLeitura);
                    ois = new ObjectInputStream(fis);
                    return true;
                } catch (IOException ioe) {
                    System.out.println("ficheiro n tem nada");
                }
            }
        }
        return false;
    }

    /**
     * Abertura do ficheiro para escrita.
     * @param aCaminho caminho onde se encontra o ficheiro.
     * @param aOp boolean para dar append ou n no ficheiro.
     * @return boolean a confirmar
     */

    public boolean aberturaEscrita(String aCaminho, boolean aOp) {

        if (aCaminho != null && aCaminho.length() > 0) {
            ficheiroEscrita = new File(aCaminho);
            try {
                if (ficheiroEscrita.exists()) {
                    fos = new FileOutputStream(ficheiroEscrita, aOp);
                    oos = new ObjectOutputStreamExtended(fos);
                } else {
                    fos = new FileOutputStream(ficheiroEscrita);
                    oos = new ObjectOutputStream(fos);
                }
                return true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
        return false;
    }

    /**
     * Encerrar a leitura.
     * @return boolean a confirmar a ação.
     */

    public boolean encerraLeitura() {
        try {
            if (ois != null) {
                ois.close();
            }

            if (fis != null) {
                fis.close();
            }
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    /**
     * Encerrar a escrita.
     * @return boolean a confirmar a ação.
     */

    public boolean encerraEscrita() {
        try {
            if (oos != null) {
                oos.close();
            }

            if (fos != null) {
                fos.close();
            }
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    /**
     * metodo para ler ficheiro GerirRegistoJogos.
     * @return um objeto da classe GerirRegistoJogos.
     */

    public GerirRegistoJogos leituraFicheiro() {
        GerirRegistoJogos temp = null;
        if (ois != null) {
            try {
                while (true) {
                    temp = (GerirRegistoJogos) ois.readObject();
                }
            } catch(EOFException eofe) {
                return temp;
            } catch (ClassNotFoundException cnfe){
                cnfe.printStackTrace();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        return null;
    }

    /**
     * Metodo para escrever no ficheiro aberto anteriormente.
     * @param aConteudo conteudo a escrever na forma de objeto.
     * @return boolean a confirmar a ação.
     */

    public boolean escreveFicheiro (Object aConteudo) {

        if (oos != null) {
            if (aConteudo != null) {
                try {
                    oos.writeObject( aConteudo );
                    oos.flush();
                    return true;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return false;
    }

}
