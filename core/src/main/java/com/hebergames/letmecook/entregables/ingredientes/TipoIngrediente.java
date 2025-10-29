package com.hebergames.letmecook.entregables.ingredientes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.estaciones.procesadoras.MetodoCoccion;

public enum TipoIngrediente {
    PAN("Pan", MetodoCoccion.TOSTADORA, 3f, 7f),
    CARNE("Carne", MetodoCoccion.HORNO, 5f, 15f),
    POLLO("Pollo", MetodoCoccion.HORNO, 6f, 12f),
    MILANESA_CARNE("Milanesa de Carne", MetodoCoccion.HORNO, 7f, 14f),
    MILANESA_POLLO("Milanesa de Pollo", MetodoCoccion.HORNO, 5f, 13f),
    PAPAS("Papas", MetodoCoccion.FREIDORA, 3f, 12f),
    NUGGETS("Nuggets", MetodoCoccion.FREIDORA, 4f, 13f),
    AROS_CEBOLLA("Aros de Cebolla", MetodoCoccion.FREIDORA, 3f, 9f),
    RABAS("Rabas", MetodoCoccion.FREIDORA, 3f, 8f);

    private final String NOMBRE;
    private final MetodoCoccion METODO_COCCION;
    private final float TIEMPO_MINIMO_COCCION;
    private final float TIEMPO_MAXIMO_COCCION;

    TipoIngrediente(String NOMBRE, MetodoCoccion METODO_COCCION,
                    float tiempoCoccionMinimo, float tiempoCoccionMaximo) {
        this.NOMBRE = NOMBRE;
        this.METODO_COCCION = METODO_COCCION;
        this.TIEMPO_MINIMO_COCCION = tiempoCoccionMinimo;
        this.TIEMPO_MAXIMO_COCCION = tiempoCoccionMaximo;
    }

    public IngredienteGenerico crear(TextureRegion textura) {
        if (METODO_COCCION != null) {
            return new IngredienteGenerico(NOMBRE, textura, METODO_COCCION,
                TIEMPO_MINIMO_COCCION, TIEMPO_MAXIMO_COCCION);
        }
        return new IngredienteGenerico(NOMBRE, textura);
    }

    public String getNombre() { return NOMBRE; }
}
