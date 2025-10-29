package com.hebergames.letmecook.entregables.ingredientes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.utiles.GestorTexturas;

public enum TipoEnvase {
    ENVASE_PAPAS("Envase de Papas", "Papas (Bien hecho)"),
    ENVASE_NUGGETS("Envase de Nuggets", "Nuggets (Bien hecho)"),
    ENVASE_AROS_CEBOLLA("Envase de Aros de Cebolla", "Aros de Cebolla (Bien hecho)"),
    ENVASE_RABAS("Envase de Rabas", "Rabas (Bien hecho)"),
    ENVASE_MILANESA_CARNE("Bandeja para Milanesa de Carne", "Milanesa de Carne (Bien hecho)"),
    ENVASE_TOSTADA("Bandeja con acompañamiento para tostadas", "Pan (Bien hecho)"),
    ENVASE_MILANESA_POLLO("Bandeja para Milanesa de Pollo", "Milanesa de Pollo (Bien hecho)"),

    ENVASE_PAPAS_QUEMADAS("Envase de Papas", "Papas (Pasado)"),
    ENVASE_NUGGETS_QUEMADAS("Envase de Nuggets", "Nuggets (Pasado)"),
    ENVASE_AROS_CEBOLLA_QUEMADOS("Envase de Aros de Cebolla", "Aros de Cebolla (Pasado)"),
    ENVASE_RABAS_QUEMADAS("Envase de Rabas", "Rabas (Pasado)"),
    ENVASE_MILANESA_CARNE_QUEMADA("Bandeja para Milanesa de Carne", "Milanesa de Carne (Pasado)"),
    ENVASE_TOSTADA_QUEMADA("Bandeja con acompañamiento para tostadas", "Pan (Pasado)"),
    ENVASE_MILANESA_POLLO_QUEMADA("Bandeja para Milanesa de Pollo", "Milanesa de Pollo (Pasado)"),

    ENVASE_PAPAS_CRUDAS("Envase de Papas", "Papas"),
    ENVASE_NUGGETS_CRUDAS("Envase de Nuggets", "Nuggets"),
    ENVASE_AROS_CEBOLLA_CRUDOS("Envase de Aros de Cebolla", "Aros de Cebolla"),
    ENVASE_RABAS_CRUDAS("Envase de Rabas", "Rabas"),
    ENVASE_MILANESA_CARNE_CRUDA("Bandeja para Milanesa de Carne", "Milanesa de Carne"),
    ENVASE_TOSTADA_SIN_TOSTAR("Bandeja con acompañamiento para tostadas", "Pan"),
    ENVASE_MILANESA_POLLO_CRUDA("Bandeja para Milanesa de Pollo", "Milanesa de Pollo");

    private final String nombre;
    private final String ingredienteRequerido;

    TipoEnvase(String nombre, String ingredienteRequerido) {
        this.nombre = nombre;
        this.ingredienteRequerido = ingredienteRequerido;
    }

    public static TipoEnvase obtenerPorIngrediente(String nombreIngrediente) {
        for (TipoEnvase tipo : values()) {
            if (tipo.ingredienteRequerido.equals(nombreIngrediente)) {
                return tipo;
            }
        }
        return null;
    }

    public IngredienteGenerico crearEnvase() {
        TextureRegion textura = GestorTexturas.getInstance().getTexturaIngrediente();
        return new IngredienteGenerico(nombre, textura);
    }

    public String getNombre() {
        return nombre;
    }

}
