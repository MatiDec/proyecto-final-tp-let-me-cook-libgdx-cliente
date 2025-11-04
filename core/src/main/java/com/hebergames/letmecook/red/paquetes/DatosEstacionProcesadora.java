package com.hebergames.letmecook.red.paquetes;

import java.io.Serializable;
import java.util.ArrayList;

public class DatosEstacionProcesadora implements Serializable {
    private static final long serialVersionUID = 1L;
    public int index;
    public boolean procesando;
    public String nombreIngrediente;
    public String estadoIndicador;
    public ArrayList<String> objetosEnEstacion;
    public boolean tieneJugador;

    public DatosEstacionProcesadora(int index, boolean procesando,
                                    String nombreIngrediente, String estadoIndicador) {
        this.index = index;
        this.procesando = procesando;
        this.nombreIngrediente = nombreIngrediente;
        this.estadoIndicador = estadoIndicador;
        this.objetosEnEstacion = new ArrayList<>();
        this.tieneJugador = false;
    }

    public DatosEstacionProcesadora(int index, boolean procesando,
                                    String nombreIngrediente, String estadoIndicador,
                                    ArrayList<String> objetosEnEstacion, boolean tieneJugador) {
        this.index = index;
        this.procesando = procesando;
        this.nombreIngrediente = nombreIngrediente;
        this.estadoIndicador = estadoIndicador;
        this.objetosEnEstacion = objetosEnEstacion;
        this.tieneJugador = tieneJugador;
    }
}
