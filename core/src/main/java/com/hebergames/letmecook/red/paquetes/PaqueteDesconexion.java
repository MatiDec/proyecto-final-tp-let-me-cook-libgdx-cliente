package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

public class PaqueteDesconexion extends PaqueteRed {
    private int idJugador;
    private String razon; // "CIERRE_SERVIDOR", "JUGADOR_ABANDONO", "TIMEOUT"

    public PaqueteDesconexion(int idJugador, String razon) {
        this.idJugador = idJugador;
        this.razon = razon;
    }

    @Override
    public PaqueteRed.TipoPaquete getTipo() {
        return TipoPaquete.DESCONEXION;
    }

    public int getIdJugador() { return idJugador; }
    public String getRazon() { return razon; }
}
