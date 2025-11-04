package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

public class PaqueteInteraccion extends PaqueteRed {
    private final int ID_JUGADOR;
    private final int INDICE_ESTACION;
    private final TipoInteraccion TIPO_INTERACCION;
    private final int PARAMETRO_EXTRA;

    public enum TipoInteraccion {
        INTERACTUAR_BASICO,
        SELECCION_MENU,
        TOMAR_INGREDIENTE,
        DEPOSITAR_OBJETO,
        INICIAR_PROCESO,
        RECOGER_RESULTADO
    }

    public PaqueteInteraccion(int ID_JUGADOR, int INDICE_ESTACION, TipoInteraccion tipo) {
        this.ID_JUGADOR = ID_JUGADOR;
        this.INDICE_ESTACION = INDICE_ESTACION;
        this.TIPO_INTERACCION = tipo;
        this.PARAMETRO_EXTRA = -1;
    }

    public PaqueteInteraccion(int ID_JUGADOR, int INDICE_ESTACION, TipoInteraccion tipo, int parametro) {
        this.ID_JUGADOR = ID_JUGADOR;
        this.INDICE_ESTACION = INDICE_ESTACION;
        this.TIPO_INTERACCION = tipo;
        this.PARAMETRO_EXTRA = parametro;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.INTERACCION;
    }

    public int getIndexEstacion() {
        return this.INDICE_ESTACION;
    }

    public int getIdJugador() {
        return this.ID_JUGADOR;
    }

    public TipoInteraccion getTipoInteraccion() {
        return this.TIPO_INTERACCION;
    }

    public int getParametroExtra() {
        return this.PARAMETRO_EXTRA;
    }
}
