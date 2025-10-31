package com.hebergames.letmecook.red.paquetes;

import java.io.Serializable;
import java.util.ArrayList;

public class DatosEstacion implements Serializable {
    private static final long serialVersionUID = 1L;

    public int index;
    public String tipoEstacion; // "Horno", "Heladera", "Mesa", etc.

    // Para procesadoras
    public boolean procesando;
    public String nombreIngrediente;
    public String estadoIndicador;
    public float progresoProceso; // 0.0 a 1.0

    // Para mesas
    public ArrayList<String> objetosEnEstacion;

    // Para bebidas (cafetera/fuente)
    public String estadoMenuBebida;
    public String tamanoSeleccionado;
    public float progresoPreparacion;

    // General
    public boolean tieneJugador;

    public DatosEstacion(int index, String tipoEstacion) {
        this.index = index;
        this.tipoEstacion = tipoEstacion;
        this.objetosEnEstacion = new ArrayList<>();
        this.procesando = false;
        this.nombreIngrediente = "";
        this.estadoIndicador = "INACTIVO";
        this.progresoProceso = 0f;
        this.estadoMenuBebida = "";
        this.tamanoSeleccionado = "";
        this.progresoPreparacion = 0f;
        this.tieneJugador = false;
    }
}
