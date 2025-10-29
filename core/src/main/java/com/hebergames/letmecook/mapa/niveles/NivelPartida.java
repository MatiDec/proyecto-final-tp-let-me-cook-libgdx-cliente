package com.hebergames.letmecook.mapa.niveles;

import com.hebergames.letmecook.mapa.Mapa;
import com.hebergames.letmecook.sonido.CancionNivel;

public class NivelPartida {
    private final Mapa MAPA;
    private final TurnoTrabajo TURNO;
    private int puntajeObtenido;
    private boolean completado;
    private final CancionNivel CANCION_NIVEL;

    public NivelPartida(Mapa MAPA, TurnoTrabajo TURNO, CancionNivel cancion) {
        this.MAPA = MAPA;
        this.TURNO = TURNO;
        this.CANCION_NIVEL = cancion;
        this.puntajeObtenido = 0;
        this.completado = false;
    }

    public void marcarCompletado(int puntaje) {
        this.completado = true;
        this.puntajeObtenido = puntaje;
    }

    public Mapa getMapa() { return this.MAPA; }

    public TurnoTrabajo getTurno() { return this.TURNO; }

    public boolean isCompletado() { return this.completado; }

    public int getPuntajeObtenido() { return this.puntajeObtenido; }

    public CancionNivel getCancionNivel() { return this.CANCION_NIVEL; }
}
