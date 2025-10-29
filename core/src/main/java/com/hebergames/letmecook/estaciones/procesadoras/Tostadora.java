package com.hebergames.letmecook.estaciones.procesadoras;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;

public class Tostadora extends EstacionProcesadora {
    public Tostadora(Rectangle area) {
        super(area);
        procesadora = new Procesadora(area, "tostadora");
    }

    @Override
    public void alInteractuar() {
        if (getJugadorOcupante() != null) {
            manejarProcesamiento(getJugadorOcupante());
        }
    }

    @Override
    public void dibujarEstado(SpriteBatch batch) {

    }
}
