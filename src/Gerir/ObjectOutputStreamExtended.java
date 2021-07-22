package Gerir;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

/**
 * Classe para reescrever o metodo ObjectOutputStream.
 */

public class ObjectOutputStreamExtended extends ObjectOutputStream {

    /**
     * Construtor da classe
     * @param os OutputStream
     * @throws IOException
     */

    ObjectOutputStreamExtended(OutputStream os) throws IOException {
        super(os);
    }

    /**
     * writeStreamHeader
     * @throws IOException throws esta exceção.
     */

    protected void writeStreamHeader() throws IOException {
        reset();
    }
}
