package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;
import java.util.ArrayList;

public class PaqueteInicioPartida extends PaqueteRed {
    private final ArrayList<String> NIVELES;

    public PaqueteInicioPartida(ArrayList<String> NIVELES) {
        this.NIVELES = NIVELES;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.INICIO_PARTIDA;
    }

    public ArrayList<String> getNiveles() {
        return this.NIVELES;
    }
}
