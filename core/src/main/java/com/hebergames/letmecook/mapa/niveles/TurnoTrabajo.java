package com.hebergames.letmecook.mapa.niveles;

import com.hebergames.letmecook.entregables.productos.CategoriaProducto;

public enum TurnoTrabajo {
    MANANA("Mañana", CategoriaProducto.DESAYUNO, CategoriaProducto.BEBIDAS),
    TARDE("Tarde", CategoriaProducto.ALMUERZO, CategoriaProducto.BEBIDAS),
    NOCHE("Noche", CategoriaProducto.ALMUERZO, CategoriaProducto.BEBIDAS);

    private final String NOMBRE;
    private final CategoriaProducto[] CATEGORIAS_PRODUCTOS;

    TurnoTrabajo(String NOMBRE, CategoriaProducto... categorias) {
        this.NOMBRE = NOMBRE;
        this.CATEGORIAS_PRODUCTOS = categorias;
    }

    public CategoriaProducto[] getCategoriasProductos() {
        return this.CATEGORIAS_PRODUCTOS;
    }

    public String getNombre() {
        return this.NOMBRE;
    }
}
