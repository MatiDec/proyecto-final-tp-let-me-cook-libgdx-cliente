package com.hebergames.letmecook.entregables.ingredientes;

public enum EstadoCoccion {
    MAL_HECHO("Mal hecho"),
    BIEN_HECHO("Bien hecho"),
    PASADO("Pasado");

    private final String ESTADO;

    EstadoCoccion(final String ESTADO) {
        this.ESTADO = ESTADO;
    }

    public String getESTADO() {
        return this.ESTADO;
    }
}
