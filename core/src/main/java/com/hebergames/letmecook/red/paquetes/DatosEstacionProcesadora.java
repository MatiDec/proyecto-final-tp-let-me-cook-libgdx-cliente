package com.hebergames.letmecook.red.paquetes;

import java.io.Serializable;

// === DATOS ESTACIÓN PROCESADORA ===
class DatosEstacionProcesadora implements Serializable {
    public int index;
    public boolean procesando;
    public String nombreIngrediente;
    public String estadoIndicador;

    public DatosEstacionProcesadora(int index, boolean procesando,
                                    String nombreIngrediente, String estadoIndicador) {
        this.index = index;
        this.procesando = procesando;
        this.nombreIngrediente = nombreIngrediente;
        this.estadoIndicador = estadoIndicador;
    }
}
