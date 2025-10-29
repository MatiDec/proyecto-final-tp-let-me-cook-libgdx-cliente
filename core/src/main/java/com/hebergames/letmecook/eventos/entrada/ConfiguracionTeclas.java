package com.hebergames.letmecook.eventos.entrada;

public class ConfiguracionTeclas {
    private final int arriba, abajo, izquierda, derecha, interactuar, correr;

    public ConfiguracionTeclas(int arriba, int abajo, int izquierda, int derecha, int interactuar, int correr) {
        this.arriba = arriba;
        this.abajo = abajo;
        this.izquierda = izquierda;
        this.derecha = derecha;
        this.interactuar = interactuar;
        this.correr = correr;
    }

    public int getArriba() {
        return this.arriba;
    }

    public int getAbajo() {
        return this.abajo;
    }

    public int getIzquierda() {
        return this.izquierda;
    }

    public int getDerecha() {
        return this.derecha;
    }

    public int getInteractuar() {
        return this.interactuar;
    }

    public int getCorrer() {
        return this.correr;
    }
}
