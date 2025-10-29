package com.hebergames.letmecook.estaciones;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;

public class Basurero extends EstacionTrabajo{


    public Basurero(Rectangle area) {
        super(area);
    }

    @Override
    public void alInteractuar() {
        Jugador jugador = getJugadorOcupante();
        if(jugador.tieneInventarioLleno())
        {
            jugador.descartarInventario();
        }
    }
}
