package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

public class PaqueteCambioNivel extends PaqueteRed {
    private final int PUNTAJE_NIVEL_COMPLETADO;
    private final String RUTA_NUEVO_MAPA;
    private final String TURNO_TRABAJO;
    private final int NUMERO_NIVEL;

    public PaqueteCambioNivel(int PUNTAJE_NIVEL_COMPLETADO, String RUTA_NUEVO_MAPA,
                              String TURNO_TRABAJO, int NUMERO_NIVEL) {
        this.PUNTAJE_NIVEL_COMPLETADO = PUNTAJE_NIVEL_COMPLETADO;
        this.RUTA_NUEVO_MAPA = RUTA_NUEVO_MAPA;
        this.TURNO_TRABAJO = TURNO_TRABAJO;
        this.NUMERO_NIVEL = NUMERO_NIVEL;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.CAMBIO_NIVEL;
    }

    public int getPuntajeNivelCompletado() { return this.PUNTAJE_NIVEL_COMPLETADO; }

    public int getNumeroNivel() { return this.NUMERO_NIVEL; }
}
