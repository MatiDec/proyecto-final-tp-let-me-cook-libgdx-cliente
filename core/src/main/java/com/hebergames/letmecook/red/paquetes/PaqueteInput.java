package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

// === PAQUETE INPUT ===
public class PaqueteInput extends PaqueteRed {
    private int idJugador;
    private boolean arriba, abajo, izquierda, derecha, correr;

    public PaqueteInput(int idJugador, boolean arriba, boolean abajo,
                        boolean izquierda, boolean derecha, boolean correr) {
        this.idJugador = idJugador;
        this.arriba = arriba;
        this.abajo = abajo;
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.correr = correr;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.INPUT_JUGADOR;
    }

    public int getIdJugador() {
        return idJugador;
    }

    public boolean isArriba() {
        return arriba;
    }

    public boolean isAbajo() {
        return abajo;
    }

    public boolean isIzquierda() {
        return izquierda;
    }

    public boolean isDerecha() {
        return derecha;
    }

    public boolean isCorrer() {
        return correr;
    }
}
