package com.hebergames.letmecook.eventos.entrada;

import com.badlogic.gdx.graphics.Color;
import com.hebergames.letmecook.elementos.Texto;

public class TextoInteractuable implements BotonInteractuable {

    private final Texto TEXTO;
    private final Runnable ACCION;

    public TextoInteractuable(Texto texto, Runnable accion) {
        this.TEXTO = texto;
        this.ACCION = accion;
    }

    @Override
    public boolean fueClickeado(float x, float y) {
        return TEXTO.fueClickeado(x, y);
    }

    @Override
    public void alClick() {
        ACCION.run();
    }
}
