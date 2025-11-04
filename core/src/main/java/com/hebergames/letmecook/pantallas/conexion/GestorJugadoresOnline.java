package com.hebergames.letmecook.pantallas.conexion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.red.paquetes.DatosJugador;
import com.hebergames.letmecook.red.paquetes.PaqueteEstado;
import com.hebergames.letmecook.utiles.GestorAnimacion;
import com.hebergames.letmecook.utiles.Recursos;

public class GestorJugadoresOnline {
    private Jugador jugador1Local;
    private Jugador jugador2Local;
    private GestorAnimacion gestorAnimacionJ1;
    private GestorAnimacion gestorAnimacionJ2;
    private final int ID_JUGADOR;

    public GestorJugadoresOnline(int ID_JUGADOR) {
        this.ID_JUGADOR = ID_JUGADOR;
        inicializarJugadores();
    }

    public void inicializarJugadores() {
        gestorAnimacionJ1 = new GestorAnimacion(Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f);
        gestorAnimacionJ2 = new GestorAnimacion(Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f);

        jugador1Local = new Jugador(0, 0, gestorAnimacionJ1);
        jugador2Local = new Jugador(0, 0, gestorAnimacionJ2);

        jugador1Local.actualizar(0);
        jugador2Local.actualizar(0);
    }

    public void actualizarDesdeServidor(PaqueteEstado estado) {
        if (estado == null) return;

        actualizarJugador(jugador1Local, estado.getJugador1());
        actualizarJugador(jugador2Local, estado.getJugador2());
    }

    private void actualizarJugador(Jugador jugador, DatosJugador datos) {
        if (datos == null) return;

        Vector2 posicionObjetivo = new Vector2(datos.x, datos.y);
        jugador.getPosicion().lerp(posicionObjetivo, 0.3f);
        jugador.setAnguloRotacion(datos.angulo);
        jugador.setObjetoEnMano(datos.objetoEnMano);
        jugador.setMoviendose(datos.estaMoviendose);

        if (datos.estaCorriendo && datos.velocidadX == 0 && datos.velocidadY == 0) {
            jugador.iniciarDeslizamiento();
        }
    }

    public void actualizar(float delta, boolean pausado) {
        if (!pausado) {
            jugador1Local.actualizar(delta);
            jugador2Local.actualizar(delta);
        }
    }

    public void dibujar(SpriteBatch batch) {
        jugador1Local.dibujar(batch);
        jugador2Local.dibujar(batch);
    }

    public Jugador getJugador1() {
        return this.jugador1Local;
    }

    public Jugador getJugador2() {
        return this.jugador2Local;
    }

    public Jugador getJugadorLocal() {
        return ID_JUGADOR == 1 ? jugador1Local : jugador2Local;
    }

    public Vector2 getPosicionJugadorLocal() {
        return getJugadorLocal().getPosicion();
    }

    public void dispose() {
        if (gestorAnimacionJ1 != null) {
            gestorAnimacionJ1.dispose();
            gestorAnimacionJ1 = null;
        }

        if (gestorAnimacionJ2 != null) {
            gestorAnimacionJ2.dispose();
            gestorAnimacionJ2 = null;
        }
    }
}
