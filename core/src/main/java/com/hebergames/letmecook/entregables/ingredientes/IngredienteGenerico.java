package com.hebergames.letmecook.entregables.ingredientes;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.estaciones.procesadoras.MetodoCoccion;

public class IngredienteGenerico extends Ingrediente {

    public IngredienteGenerico(String nombre, TextureRegion textura) {
        super(nombre, textura);
    }

    public IngredienteGenerico(String nombre, TextureRegion textura, MetodoCoccion metodoCoccion,
                               float tiempoCoccionMinimo, float tiempoCoccionMaximo) {
        super(nombre, textura, metodoCoccion, tiempoCoccionMinimo, tiempoCoccionMaximo);
    }
}
