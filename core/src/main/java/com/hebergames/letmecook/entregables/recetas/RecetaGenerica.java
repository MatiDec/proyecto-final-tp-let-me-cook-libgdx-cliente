package com.hebergames.letmecook.entregables.recetas;

import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.entregables.productos.TipoProducto;

import java.util.ArrayList;

public class RecetaGenerica extends Receta {
    private final TipoProducto TIPO_PRODUCTO;

    public RecetaGenerica(String nombre, ArrayList<String> ingredientesRequeridos,
                          TipoProducto tipoProducto) {
        super(nombre, ingredientesRequeridos, tipoProducto.getCategoria());
        this.TIPO_PRODUCTO = tipoProducto;
    }

    @Override
    public Producto preparar() {
        return TIPO_PRODUCTO.crear();
    }
}
