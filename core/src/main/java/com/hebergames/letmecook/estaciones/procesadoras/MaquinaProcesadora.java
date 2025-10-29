package com.hebergames.letmecook.estaciones.procesadoras;

import com.hebergames.letmecook.entregables.ingredientes.Ingrediente;

public interface MaquinaProcesadora {
    boolean puedeIniciarProceso();
    boolean iniciarProceso(Ingrediente ingrediente);
    void actualizarProceso(float delta);
    boolean tieneProcesandose();
    Ingrediente obtenerResultado();
}
