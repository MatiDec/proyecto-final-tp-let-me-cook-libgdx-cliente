package com.hebergames.letmecook.estaciones.procesadoras;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;

public abstract class EstacionProcesadora extends EstacionTrabajo {
    public EstacionProcesadora(Rectangle area) {
        super(area);
    }

    @Override
    public void alInteractuar() {
        manejarProcesamiento(getJugadorOcupante());
    }

    public abstract void dibujarEstado(SpriteBatch batch);
}
