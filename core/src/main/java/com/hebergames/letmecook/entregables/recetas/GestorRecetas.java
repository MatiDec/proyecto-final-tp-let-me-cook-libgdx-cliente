package com.hebergames.letmecook.entregables.recetas;

import com.hebergames.letmecook.entregables.ingredientes.Ingrediente;

import java.util.ArrayList;

public class GestorRecetas {
    private static GestorRecetas instancia;
    private final ArrayList<Receta> RECETAS;

    private GestorRecetas() {
        RECETAS = new ArrayList<>();
        cargarRecetas();
    }

    public void cargarRecetas() {
        RECETAS.clear();
        for (TipoReceta tipo : TipoReceta.values()) {
            RECETAS.add(tipo.crear());
        }
    }

    public static GestorRecetas getInstance() {
        if (instancia == null) {
            instancia = new GestorRecetas();
        }
        return instancia;
    }

    public Receta buscarReceta(ArrayList<Ingrediente> ingredientes) {
        for (Receta receta : RECETAS) {
            if (receta.puedePreparar(ingredientes)) {
                return receta;
            }
        }
        return null;
    }

    public ArrayList<Receta> getRECETAS() {
        return this.RECETAS;
    }
}
