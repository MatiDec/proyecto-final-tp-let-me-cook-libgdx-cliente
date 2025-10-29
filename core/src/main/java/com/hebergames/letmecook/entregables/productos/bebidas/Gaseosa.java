package com.hebergames.letmecook.entregables.productos.bebidas;

import com.hebergames.letmecook.entregables.productos.CategoriaProducto;
import com.hebergames.letmecook.utiles.GestorTexturas;

import java.util.HashMap;
import java.util.Map;

public class Gaseosa extends Bebida {

    private static final Map<String, Float> TIPOS_GASEOSA = new HashMap<>();

    static {
        TIPOS_GASEOSA.put("Jugo", 2f);
        TIPOS_GASEOSA.put("Soda", 2f);
        TIPOS_GASEOSA.put("Sprite", 2f);
        TIPOS_GASEOSA.put("Pepsi", 2f);
        TIPOS_GASEOSA.put("CocaCola", 2f);
    }

    private final String TIPO;

    public Gaseosa(final String TIPO, TamanoBebida tamano) {
        super(TIPO + " " + tamano.getNombre(),
            GestorTexturas.getInstance().getTexturaProducto((TIPO + tamano.getNombre()).toLowerCase().replace(" ", "")),
            CategoriaProducto.BEBIDAS,
            tamano,
            TIPOS_GASEOSA.getOrDefault(TIPO, 2f));
        this.TIPO = TIPO;
    }

    public String getTipo() {
        return this.TIPO;
    }

    public static Map<String, Float> getTiposGaseosa() {
        return new HashMap<>(TIPOS_GASEOSA);
    }
}
