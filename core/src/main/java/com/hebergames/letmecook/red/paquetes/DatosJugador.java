package com.hebergames.letmecook.red.paquetes;

import java.io.Serializable;

public class DatosJugador implements Serializable {
    public float x, y;
    public float angulo;
    public String objetoEnMano;
    public boolean estaEnMenu;

    // 👇 NUEVOS - Para deslizamiento
    public boolean estaCorriendo;
    public float velocidadX;
    public float velocidadY;

    public DatosJugador(float x, float y, float angulo, String objetoEnMano, boolean estaEnMenu) {
        this(x, y, angulo, objetoEnMano, estaEnMenu, false, 0f, 0f);
    }

    public DatosJugador(float x, float y, float angulo, String objetoEnMano, boolean estaEnMenu,
                        boolean estaCorriendo, float velocidadX, float velocidadY) {
        this.x = x;
        this.y = y;
        this.angulo = angulo;
        this.objetoEnMano = objetoEnMano;
        this.estaEnMenu = estaEnMenu;
        this.estaCorriendo = estaCorriendo;
        this.velocidadX = velocidadX;
        this.velocidadY = velocidadY;
    }
}
