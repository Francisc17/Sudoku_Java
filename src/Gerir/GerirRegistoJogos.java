package Gerir;

import java.io.Serializable;
import java.util.Vector;

    /**
    * Classe necessária para gerir objetos do tipo RegistoJogo.
    */

public class GerirRegistoJogos implements Serializable {

    private Vector<RegistoJogo> registoJogos = new Vector<>();

    /**
     * Metodo para adicionar um objeto RegistoJogo ao vetor.
     * @param registo Objeto a adicionar
     * @return boolean a confirmar.
     */

    public boolean adicionarRegisto(RegistoJogo registo){
       if (registoJogos.add(registo))
           return true;
       return false;
    }

    /**
     * GETTER para o vetor registoJogos.
     * @return Vector com o registo de jogos.
     */

    public Vector<RegistoJogo> getRegistoJogos() {
        return registoJogos;
    }
}
