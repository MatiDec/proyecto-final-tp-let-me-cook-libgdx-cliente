package com.hebergames.letmecook.entregables.productos.bebidas;

public enum TamanoBebida {
    PEQUENO("Pequeno", 1.0f),
    MEDIANO("Mediano", 1.5f),
    GRANDE("Grande", 2.0f);

    private final String NOMBRE;
    private final float MULTIPLICADOR_TIEMPO;

    TamanoBebida(String NOMBRE, final float MULTIPLICADOR_TIEMPO) {
        this.NOMBRE = NOMBRE;
        this.MULTIPLICADOR_TIEMPO = MULTIPLICADOR_TIEMPO;
    }

    public String getNombre() {
        return NOMBRE;
    }

    public float getMultiplicadorTiempo() {
        return MULTIPLICADOR_TIEMPO;
    }
}
