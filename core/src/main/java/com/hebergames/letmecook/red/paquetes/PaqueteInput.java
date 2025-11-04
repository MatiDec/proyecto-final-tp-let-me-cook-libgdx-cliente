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

    public int getIdJugador() {return this.ID_JUGADOR;}

    public boolean isArriba() {return this.ARRIBA;}

    public boolean isAbajo() {return this.ABAJO;}

    public boolean isIzquierda() {
        return this.IZQUIERDA;
    }

    public boolean isDerecha() {
        return this.DERECHA;
    }

    public boolean isCorrer() {
        return this.CORRER;
    }

}
