package com.hebergames.letmecook.pantallas.juego;

import com.hebergames.letmecook.entidades.Jugador;
import java.util.ArrayList;

public class DetectorInactividad {

    private float tiempoInactividad = 0f;
    private final float TIEMPO_LIMITE_INACTIVIDAD;
    private final ArrayList<Jugador> JUGADORES;
    private final float[] ULTIMAS_POSICIONES_X;
    private final float[] ULTIMAS_POSICIONES_Y;

    public DetectorInactividad(ArrayList<Jugador> JUGADORES, float tiempoLimite) {
        this.JUGADORES = JUGADORES;
        this.TIEMPO_LIMITE_INACTIVIDAD = tiempoLimite;
        this.ULTIMAS_POSICIONES_X = new float[JUGADORES.size()];
        this.ULTIMAS_POSICIONES_Y = new float[JUGADORES.size()];

        for (int i = 0; i < JUGADORES.size(); i++) {
            ULTIMAS_POSICIONES_X[i] = JUGADORES.get(i).getPosicion().x;
            ULTIMAS_POSICIONES_Y[i] = JUGADORES.get(i).getPosicion().y;
        }
    }

    public void actualizar(float delta) {
        boolean algunoSeMovio = false;

        for (int i = 0; i < JUGADORES.size(); i++) {
            Jugador jugador = JUGADORES.get(i);
            float posX = jugador.getPosicion().x;
            float posY = jugador.getPosicion().y;

            if (Math.abs(posX - ULTIMAS_POSICIONES_X[i]) > 0.1f ||
                Math.abs(posY - ULTIMAS_POSICIONES_Y[i]) > 0.1f) {
                algunoSeMovio = true;
                ULTIMAS_POSICIONES_X[i] = posX;
                ULTIMAS_POSICIONES_Y[i] = posY;
            }
        }

        if (algunoSeMovio) {
            tiempoInactividad = 0f;
        } else {
            tiempoInactividad += delta;
        }
    }

    public boolean haySuperadoLimite() {
        return tiempoInactividad >= TIEMPO_LIMITE_INACTIVIDAD;
    }
}
