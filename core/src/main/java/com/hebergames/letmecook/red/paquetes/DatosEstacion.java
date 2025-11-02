package com.hebergames.letmecook.red.paquetes;

import java.io.Serializable;
import java.util.ArrayList;

public class DatosEstacion implements Serializable {
    private static final long serialVersionUID = 1L;

    public int index;
    public String tipoEstacion;

    // Para procesadoras
    public boolean procesando;
    public String nombreIngrediente;
    public String estadoIndicador; // "PROCESANDO", "LISTO", "QUEMANDOSE", "INACTIVO"
    public float progresoProceso; // 0.0 a 1.0
    public boolean fueraDeServicio; // 👈 NUEVO - Para máquinas rotas
    public String estadoMaquina; // 👈 NUEVO - "ACTIVA" o "LISTA"

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
        this.fueraDeServicio = false; // 👈 NUEVO
        this.estadoMaquina = "INACTIVA"; // 👈 NUEVO
    }
}
