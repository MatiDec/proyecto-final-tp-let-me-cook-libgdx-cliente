package com.hebergames.letmecook.entregables.productos.bebidas;

import com.hebergames.letmecook.entregables.productos.CategoriaProducto;
import com.hebergames.letmecook.utiles.GestorTexturas;

import java.util.HashMap;
import java.util.Map;

public class Cafe extends Bebida {

    private static final Map<String, Float> TIPOS_CAFE = new HashMap<>();

    static {
        TIPOS_CAFE.put("Expreso", 3f);
        TIPOS_CAFE.put("Americano", 4f);
        TIPOS_CAFE.put("Cortado", 5f);
    }

    private final String TIPO;

    public Cafe(final String TIPO, TamanoBebida tamano) {
        super(TIPO + " " + tamano.getNombre(),
            GestorTexturas.getInstance().getTexturaProducto((TIPO + tamano.getNombre()).toLowerCase().replace(" ", "")),
            CategoriaProducto.BEBIDAS,
            tamano,
            TIPOS_CAFE.getOrDefault(TIPO, 3f));
        this.TIPO = TIPO;
    }

    public String getTipo() {
        return this.TIPO;
    }

    public static Map<String, Float> getTiposCafe() {
        return new HashMap<>(TIPOS_CAFE);
    }
}
