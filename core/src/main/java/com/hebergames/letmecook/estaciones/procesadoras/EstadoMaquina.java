package com.hebergames.letmecook.estaciones.procesadoras;

public enum EstadoMaquina {
    ACTIVA(0),
    LISTA(1);

    private final int indice;

    EstadoMaquina(int indice) {
        this.indice = indice;
    }

    public int getIndice() {
        return indice;
    }
}
