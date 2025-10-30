package com.hebergames.letmecook.red.paquetes;

import java.io.Serializable;

// === DATOS ESTADO JUGADOR ===
public class DatosJugador implements Serializable {
    public float x, y;
    public float angulo;
    public String objetoEnMano;
    public boolean estaEnMenu;

    public DatosJugador(float x, float y, float angulo, String objetoEnMano, boolean estaEnMenu) {
        this.x = x;
        this.y = y;
        this.angulo = angulo;
        this.objetoEnMano = objetoEnMano;
        this.estaEnMenu = estaEnMenu;
    }
}
