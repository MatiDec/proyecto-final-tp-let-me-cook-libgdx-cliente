package com.hebergames.letmecook.entregables.ingredientes;

import com.hebergames.letmecook.utiles.Recursos;

public class GestorIngredientes {
    private static GestorIngredientes instancia;

    private GestorIngredientes() {}

    public static GestorIngredientes getInstance() {
        if (instancia == null) {
            instancia = new GestorIngredientes();
        }
        return instancia;
    }

    public Ingrediente crearIngrediente(TipoIngrediente tipo) {
        return tipo.crear(Recursos.INGREDIENTES);
    }
}
