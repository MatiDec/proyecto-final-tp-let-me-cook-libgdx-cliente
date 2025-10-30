package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

// === PAQUETE CONEXIÓN ===
public class PaqueteConexion extends PaqueteRed {
    private int idJugador;
    private boolean esAprobado;

    public PaqueteConexion(int idJugador, boolean esAprobado) {
        this.idJugador = idJugador;
        this.esAprobado = esAprobado;
    }

    @Override
    public TipoPaquete getTipo() { return TipoPaquete.CONEXION; }

    public int getIdJugador() { return idJugador; }
    public boolean esAprobado() { return esAprobado; }
}

