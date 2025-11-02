package com.hebergames.letmecook.red;

import java.io.*;

public abstract class PaqueteRed implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoPaquete {
        CONEXION,
        ESTADO_JUEGO,
        INPUT_JUGADOR,
        PING,
        DESCONEXION,
        INTERACCION,
        INICIO_PARTIDA,
        CAMBIO_NIVEL
    }

    public abstract TipoPaquete getTipo();

    public byte[] serializar() throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bos);
        oos.writeObject(this);
        oos.flush();
        return bos.toByteArray();
    }

    public static PaqueteRed deserializar(byte[] datos) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bis = new ByteArrayInputStream(datos);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (PaqueteRed) ois.readObject();
    }
}
