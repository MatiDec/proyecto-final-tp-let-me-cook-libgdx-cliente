package com.hebergames.letmecook.pantallas.juego;

import com.hebergames.letmecook.eventos.hilos.HiloPrincipal;

public class GestorTiempoJuego {

    private final HiloPrincipal hiloPrincipal;
    private final int tiempoObjetivo;

    public GestorTiempoJuego(int tiempoObjetivoSegundos) {
        this.tiempoObjetivo = tiempoObjetivoSegundos;
        this.hiloPrincipal = new HiloPrincipal();
        this.hiloPrincipal.start();
    }

    public boolean haTerminadoTiempo() {
        return hiloPrincipal.getSegundos() >= tiempoObjetivo;
    }

    public void detener() {
        hiloPrincipal.detener();
    }

    public String getTiempoFormateado() {
        int segundos = hiloPrincipal.getSegundos();
        int minutos = segundos / 60;
        int segundosRestantes = segundos % 60;
        return String.format("%02d:%02d", minutos, segundosRestantes);
    }

    public int getSegundos() {
        return hiloPrincipal.getSegundos();
    }
}
