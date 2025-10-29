package com.hebergames.letmecook.estaciones.conmenu;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;

public abstract class EstacionConMenu extends EstacionTrabajo {

    private Jugador jugadorOcupante;

    public EstacionConMenu(Rectangle area) {
        super(area);
    }

    public abstract void alLiberar();
    public abstract void iniciarMenu(Jugador jugador);
    public abstract void manejarSeleccionMenu(Jugador jugador, int numeroSeleccion);
    protected abstract void dibujarMenu(SpriteBatch batch, Jugador jugador);

    public void dibujar(SpriteBatch batch, Jugador jugador) {
        if (jugadorOcupante == jugador && jugador.estaEnMenu()) {
            dibujarMenu(batch, jugador);
        }
    }

    public Jugador getJugadorOcupante() {
        return this.jugadorOcupante;
    }

    public void ocupar(Jugador jugador) {
        if (jugador != null) {
            this.jugadorOcupante = jugador;
        }
    }

    public void verificarDistanciaYLiberar() {
        if (jugadorOcupante != null) {

            if (!estaCerca(jugadorOcupante.getPosicion().x,
                jugadorOcupante.getPosicion().y)) {
                jugadorOcupante.salirDeMenu();
                alLiberar();
                jugadorOcupante = null;
            }
        }
    }
}
