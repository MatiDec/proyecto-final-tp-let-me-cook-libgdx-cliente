package com.hebergames.letmecook.eventos.puntaje;

public class GestorPuntaje implements CallbackPuntaje {

    private int puntajeActual;

    public GestorPuntaje() {
        this.puntajeActual = 0;
    }

    @Override
    public void onPuntosObtenidos(int puntos) {
        puntajeActual += puntos;
    }

    public void agregarPuntos(int puntos) {
        onPuntosObtenidos(puntos);
    }

    public int getPuntajeActual() {
        return puntajeActual;
    }
}
