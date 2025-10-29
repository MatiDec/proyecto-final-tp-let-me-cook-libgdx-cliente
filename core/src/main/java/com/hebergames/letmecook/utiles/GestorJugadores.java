package com.hebergames.letmecook.utiles;

import com.hebergames.letmecook.entidades.Jugador;

import java.util.ArrayList;

public class GestorJugadores {

    public static final float ANCHO = 1920;
    public static final float ALTO = 1080;
    private static GestorJugadores instancia;
    private ArrayList<Jugador> jugadores;

    private GestorJugadores() {
        jugadores = new ArrayList<>();
    }

    public static GestorJugadores getInstancia() {
        if(instancia == null) {
            instancia = new GestorJugadores();
        }
        return instancia;
    }

    public void setJugadores(ArrayList<Jugador> jugadores) {
        this.jugadores = jugadores;
    }

    public ArrayList getJugadores() {
        return jugadores;
    }

}
