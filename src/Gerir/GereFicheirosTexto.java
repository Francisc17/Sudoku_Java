package Gerir;

import java.io.*;

/**
 * Classe responsável por conter os metodos necessários para gerir ficheiros de texto.
 */

public class GereFicheirosTexto {

    //atributos

    // acesso ficheiro

    File ficheiroLeitura;
    File ficheiroEscrita;
    // leitura ficheiro
    FileReader fr;
    public BufferedReader br;

    // escrita ficheiro

    FileWriter fw;
    BufferedWriter bw;


    //Metodos

    /**
     * Abrir ficheiro de text para leitura.
     * @param aCaminho localização do ficheiro a abrir.
     * @return boolean a confirmar a abertura do ficheiro.
     */

    public  boolean aberturaLeitura(String aCaminho) {

        if (aCaminho != null && aCaminho.length() > 0) {
            ficheiroLeitura = new File( aCaminho );
            if (ficheiroLeitura.exists()) {
                try {
                    fr = new FileReader( ficheiroLeitura );
                    br = new BufferedReader( fr );
                    return true;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Abrir o ficheiro para escrita
     * @param aCaminho localização do ficheiro.
     * @param aOp parametro boolean que serve para decidir entre acresentar ou reescrever o conteudo no ficheiro.
     * @return boolean a confirmar a ação.
     */

    public boolean aberturaEscrita(String aCaminho, boolean aOp) {

        if (aCaminho != null && aCaminho.length() > 0) {
            ficheiroEscrita = new File( aCaminho );
            try {
                fw = new FileWriter( ficheiroEscrita , aOp);
                bw = new BufferedWriter( fw );
                return true;
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

        }
        return false;
    }

    /**
     * encerrar o ficheiro aberto anteriormente para leitura.
     * @return boolean a confirmar a ação.
     */

    // encerramento dos ficheiros
    public boolean encerraLeitura() {
        try {
            if (br != null) {
                br.close();
            }

            if(fr != null) {
                fr.close();
            }
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    /**
     * Encerra o ficheiro aberto anteriormente para escrita.
     * @return boolean a confirmar a ação.
     */

    public boolean encerraEscrita() {
        try {
            if (bw != null) {
                bw.close();
            }

            if(fw != null) {
                fw.close();
            }
            return true;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return false;
        }
    }

    /**
     * Metodo para escrever no ficheiro depois deste ser aberto para escrita.
     * @param aConteudo conteudo a escrever.
     * @return boolean a confirmar a escrita no ficheiro.
     */

    // escrita ficheiro
    public boolean escreveFicheiro(String aConteudo) {

        if (bw != null) {
            if (aConteudo != null && aConteudo.length() > 0) {
                try {
                    bw.write( aConteudo );
                    return true;
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
        return false;
    }
}
