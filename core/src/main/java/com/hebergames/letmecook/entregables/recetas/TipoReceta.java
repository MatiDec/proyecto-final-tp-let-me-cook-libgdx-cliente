package com.hebergames.letmecook.entregables.recetas;

import com.hebergames.letmecook.entregables.productos.TipoProducto;

import java.util.ArrayList;
import java.util.Arrays;

public enum TipoReceta {
    HAMBURGUESA_CARNE("Hamburguesa de Carne", TipoProducto.HAMBURGUESA_CARNE, "Carne (Bien hecho)", "Pan"),
    HAMBURGUESA_POLLO("Hamburguesa de Pollo", TipoProducto.HAMBURGUESA_POLLO, "Pollo (Bien hecho)", "Pan"),
    PAPAS_FRITAS("Papas Fritas", TipoProducto.PAPAS_FRITAS, "Envase de Papas", "Papas (Bien hecho)"),
    NUGGETS_POLLO("Nuggets de pollo", TipoProducto.NUGGETS_POLLO, "Envase de Nuggets", "Nuggets (Bien hecho)"),
    AROS_CEBOLLA("Aros de Cebolla", TipoProducto.AROS_CEBOLLA, "Envase de Aros de Cebolla", "Aros de Cebolla (Bien hecho)"),
    RABAS("Rabas", TipoProducto.RABAS, "Envase de Rabas", "Rabas (Bien hecho)"),
    MILANESA_CARNE("Milanesa de Carne", TipoProducto.MILANESA_CARNE, "Bandeja para Milanesa de Carne", "Milanesa de Carne (Bien hecho)"),
    MILANESA_POLLO("Milanesa de Pollo", TipoProducto.MILANESA_POLLO, "Bandeja para Milanesa de Pollo", "Milanesa de Pollo (Bien hecho)"),

    HAMBURGUESA_CARNE_QUEMADA("Hamburguesa de Carne Quemada", TipoProducto.HAMBURGUESA_CARNE_QUEMADA, "Carne (Pasado)", "Pan"),
    HAMBURGUESA_POLLO_QUEMADA("Hamburguesa de Pollo Quemada", TipoProducto.HAMBURGUESA_POLLO_QUEMADA, "Pollo (Pasado)", "Pan"),
    PAPAS_FRITAS_QUEMADAS("Papas Fritas Quemadas", TipoProducto.PAPAS_FRITAS_QUEMADAS, "Envase de Papas", "Papas (Pasado)"),
    NUGGETS_POLLO_QUEMADOS("Nuggets de pollo Quemados", TipoProducto.NUGGETS_POLLO_QUEMADOS, "Envase de Nuggets", "Nuggets (Pasado)"),
    AROS_CEBOLLA_QUEMADOS("Aros de Cebolla Quemados", TipoProducto.AROS_CEBOLLA_QUEMADOS, "Envase de Aros de Cebolla", "Aros de Cebolla (Pasado)"),
    RABAS_QUEMADAS("Rabas Quemadas", TipoProducto.RABAS_QUEMADAS, "Envase de Rabas", "Rabas (Pasado)"),
    MILANESA_CARNE_QUEMADA("Milanesa de Carne Quemada", TipoProducto.MILANESA_CARNE_QUEMADA, "Bandeja para Milanesa de Carne", "Milanesa de Carne (Pasado)"),
    MILANESA_POLLO_QUEMADA("Milanesa de Pollo Quemada", TipoProducto.MILANESA_POLLO_QUEMADA, "Bandeja para Milanesa de Pollo", "Milanesa de Pollo (Pasado)"),

    HAMBURGUESA_CARNE_CRUDA("Hamburguesa de Carne Cruda", TipoProducto.HAMBURGUESA_CARNE_CRUDA, "Carne", "Pan"),
    HAMBURGUESA_POLLO_CRUDA("Hamburguesa de Pollo Cruda", TipoProducto.HAMBURGUESA_POLLO_CRUDA, "Pollo", "Pan"),
    PAPAS_FRITAS_CRUDAS("Papas Fritas Crudas", TipoProducto.PAPAS_FRITAS_CRUDAS, "Envase de Papas", "Papas"),
    NUGGETS_POLLO_CRUDOS("Nuggets de pollo Crudos", TipoProducto.NUGGETS_POLLO_CRUDOS, "Envase de Nuggets", "Nuggets"),
    AROS_CEBOLLA_CRUDOS("Aros de Cebolla Crudos", TipoProducto.AROS_CEBOLLA_CRUDOS, "Envase de Aros de Cebolla", "Aros de Cebolla"),
    RABAS_CRUDAS("Rabas Crudas", TipoProducto.RABAS_CRUDAS, "Envase de Rabas", "Rabas"),
    MILANESA_CARNE_CRUDA("Milanesa de Carne Cruda", TipoProducto.MILANESA_CARNE_CRUDA, "Bandeja para Milanesa de Carne", "Milanesa de Carne"),
    MILANESA_POLLO_CRUDA("Milanesa de Pollo Cruda", TipoProducto.MILANESA_POLLO_CRUDA, "Bandeja para Milanesa de Pollo", "Milanesa de Pollo");

    private final String NOMBRE;
    private final TipoProducto TIPO_PRODUCTO;
    private final String[] INGREDIENTES_REQUERIDOS;

    TipoReceta(final String NOMBRE, final TipoProducto TIPO_PRODUCTO, final String... ingredientes) {
        this.NOMBRE = NOMBRE;
        this.TIPO_PRODUCTO = TIPO_PRODUCTO;
        this.INGREDIENTES_REQUERIDOS = ingredientes;
    }

    public RecetaGenerica crear() {
        ArrayList<String> ingredientes = new ArrayList<>(Arrays.asList(INGREDIENTES_REQUERIDOS));
        return new RecetaGenerica(NOMBRE, ingredientes, TIPO_PRODUCTO);
    }

    public String getNombre() { return this.NOMBRE; }
    public TipoProducto getTipoProducto() { return this.TIPO_PRODUCTO; }
}
