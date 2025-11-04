package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

public class PaqueteDesconexion extends PaqueteRed {
    private final int ID_JUGADOR;
    private final String RAZON;

    public PaqueteDesconexion(int ID_JUGADOR, String RAZON) {
        this.ID_JUGADOR = ID_JUGADOR;
        this.RAZON = RAZON;
    }

    @Override
    public PaqueteRed.TipoPaquete getTipo() {
        return TipoPaquete.DESCONEXION;
    }

    public String getRazon() { return this.RAZON; }
}
