package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

public class PaqueteInput extends PaqueteRed {
    private final int ID_JUGADOR;
    private final boolean ARRIBA;
    private final boolean ABAJO;
    private final boolean IZQUIERDA;
    private final boolean DERECHA;
    private final boolean CORRER;

    public PaqueteInput(int ID_JUGADOR, boolean ARRIBA, boolean ABAJO,
                        boolean IZQUIERDA, boolean DERECHA, boolean CORRER) {
        this.ID_JUGADOR = ID_JUGADOR;
        this.ARRIBA = ARRIBA;
        this.ABAJO = ABAJO;
        this.IZQUIERDA = IZQUIERDA;
        this.DERECHA = DERECHA;
        this.CORRER = CORRER;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.INPUT_JUGADOR;
    }

}
