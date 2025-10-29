package com.hebergames.letmecook.entregables.ingredientes;

public interface CoccionListener {
    void onCambioEstado(EstadoCoccion nuevoEstado);
    void onIngredienteQuemado();
}
