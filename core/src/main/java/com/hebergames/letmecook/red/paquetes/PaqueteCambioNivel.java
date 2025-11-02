package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

public class PaqueteCambioNivel extends PaqueteRed {
    private int puntajeNivelCompletado;
    private String rutaNuevoMapa;
    private String turnoTrabajo;
    private int numeroNivel;

    public PaqueteCambioNivel(int puntajeNivelCompletado, String rutaNuevoMapa,
                              String turnoTrabajo, int numeroNivel) {
        this.puntajeNivelCompletado = puntajeNivelCompletado;
        this.rutaNuevoMapa = rutaNuevoMapa;
        this.turnoTrabajo = turnoTrabajo;
        this.numeroNivel = numeroNivel;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.CAMBIO_NIVEL;
    }

    public int getPuntajeNivelCompletado() { return puntajeNivelCompletado; }
    public String getRutaNuevoMapa() { return rutaNuevoMapa; }
    public String getTurnoTrabajo() { return turnoTrabajo; }
    public int getNumeroNivel() { return numeroNivel; }
}
