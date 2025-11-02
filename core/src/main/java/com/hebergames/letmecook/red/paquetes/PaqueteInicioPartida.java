package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;
import java.util.ArrayList;

public class PaqueteInicioPartida extends PaqueteRed {
    private ArrayList<String> niveles;

    public PaqueteInicioPartida(ArrayList<String> niveles) {
        this.niveles = niveles;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.INICIO_PARTIDA;
    }

    public ArrayList<String> getNiveles() {
        return this.niveles;
    }
}
