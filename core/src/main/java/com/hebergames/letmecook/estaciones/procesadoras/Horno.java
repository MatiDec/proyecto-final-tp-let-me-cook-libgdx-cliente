package com.hebergames.letmecook.estaciones.procesadoras;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;

public class Horno extends EstacionProcesadora {

    public Horno(Rectangle area) {
        super(area);
        procesadora = new Procesadora(area, "horno");
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
