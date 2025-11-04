package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

public class PaqueteConexion extends PaqueteRed {
    private final int ID_JUGADOR;
    private final boolean ES_APROBADO;

    public PaqueteConexion(int ID_JUGADOR, boolean ES_APROBADO) {
        this.ID_JUGADOR = ID_JUGADOR;
        this.ES_APROBADO = ES_APROBADO;
    }

    @Override
    public TipoPaquete getTipo() { return TipoPaquete.CONEXION; }

    public int getIdJugador() { return this.ID_JUGADOR; }
    public boolean esAprobado() { return this.ES_APROBADO; }
}

