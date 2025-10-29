package com.hebergames.letmecook.entregables.productos;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.utiles.GestorTexturas;

public enum TipoProducto {
    HAMBURGUESA_CARNE("Hamburguesa de Carne", CategoriaProducto.ALMUERZO),
    HAMBURGUESA_POLLO("Hamburguesa de Pollo", CategoriaProducto.ALMUERZO),
    MILANESA_CARNE("Milanesa de Carne", CategoriaProducto.ALMUERZO),
    MILANESA_POLLO("Milanesa de Pollo", CategoriaProducto.ALMUERZO),
    PAPAS_FRITAS("Papas Fritas", CategoriaProducto.ALMUERZO),
    NUGGETS_POLLO("Nuggets de pollo", CategoriaProducto.ALMUERZO),
    RABAS("Rabas", CategoriaProducto.ALMUERZO),
    AROS_CEBOLLA("Aros de Cebolla", CategoriaProducto.ALMUERZO),
    CAFE("Cafe", CategoriaProducto.BEBIDAS),
    GASEOSA("Gaseosa", CategoriaProducto.BEBIDAS),

    HAMBURGUESA_CARNE_QUEMADA("Hamburguesa de Carne Quemada", CategoriaProducto.INVALIDO),
    HAMBURGUESA_POLLO_QUEMADA("Hamburguesa de Pollo Quemada", CategoriaProducto.INVALIDO),
    PAPAS_FRITAS_QUEMADAS("Papas Fritas Quemadas", CategoriaProducto.INVALIDO),
    NUGGETS_POLLO_QUEMADOS("Nuggets de pollo Quemados", CategoriaProducto.INVALIDO),
    AROS_CEBOLLA_QUEMADOS("Aros de Cebolla Quemados", CategoriaProducto.INVALIDO),
    RABAS_QUEMADAS("Rabas Quemadas", CategoriaProducto.INVALIDO),
    MILANESA_CARNE_QUEMADA("Milanesa de Carne Quemada", CategoriaProducto.INVALIDO),
    MILANESA_POLLO_QUEMADA("Milanesa de Pollo Quemada", CategoriaProducto.INVALIDO),

    HAMBURGUESA_CARNE_CRUDA("Hamburguesa de Carne Cruda", CategoriaProducto.INVALIDO),
    HAMBURGUESA_POLLO_CRUDA("Hamburguesa de Pollo Cruda", CategoriaProducto.INVALIDO),
    PAPAS_FRITAS_CRUDAS("Papas Fritas Crudas", CategoriaProducto.INVALIDO),
    NUGGETS_POLLO_CRUDOS("Nuggets de pollo Crudos", CategoriaProducto.INVALIDO),
    AROS_CEBOLLA_CRUDOS("Aros de Cebolla Crudos", CategoriaProducto.INVALIDO),
    RABAS_CRUDAS("Rabas Crudas", CategoriaProducto.INVALIDO),
    MILANESA_CARNE_CRUDA("Milanesa de Carne Cruda", CategoriaProducto.INVALIDO),
    MILANESA_POLLO_CRUDA("Milanesa de Pollo Cruda", CategoriaProducto.INVALIDO);

    private final String NOMBRE;
    private final CategoriaProducto CATEGORIA;

    TipoProducto(final String NOMBRE, CategoriaProducto CATEGORIA) {
        this.NOMBRE = NOMBRE;
        this.CATEGORIA = CATEGORIA;
    }

    public ProductoGenerico crear() {
        TextureRegion textura = GestorTexturas.getInstance().getTexturaProducto(NOMBRE.toLowerCase());
        return new ProductoGenerico(NOMBRE, textura, CATEGORIA);
    }

    public String getNombre() { return this.NOMBRE; }
    public CategoriaProducto getCategoria() { return this.CATEGORIA; }

    public TextureRegion getTextura() {
        return GestorTexturas.getInstance().getTexturaProducto(NOMBRE.toLowerCase());
    }

}
