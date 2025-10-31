package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

public class PaqueteInteraccion extends PaqueteRed {
    private int idJugador;
    private int indexEstacion;
    private TipoInteraccion tipoInteraccion;
    private int parametroExtra; // Para selecciones de menú, etc.

    public enum TipoInteraccion {
        INTERACTUAR_BASICO,      // E simple
        SELECCION_MENU,          // Número del menú
        TOMAR_INGREDIENTE,       // De heladera
        DEPOSITAR_OBJETO,        // En mesa
        INICIAR_PROCESO,         // En máquina procesadora
        RECOGER_RESULTADO        // De máquina procesadora
    }

    public PaqueteInteraccion(int idJugador, int indexEstacion, TipoInteraccion tipo) {
        this.idJugador = idJugador;
        this.indexEstacion = indexEstacion;
        this.tipoInteraccion = tipo;
        this.parametroExtra = -1;
    }

    public PaqueteInteraccion(int idJugador, int indexEstacion, TipoInteraccion tipo, int parametro) {
        this.idJugador = idJugador;
        this.indexEstacion = indexEstacion;
        this.tipoInteraccion = tipo;
        this.parametroExtra = parametro;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.INTERACCION;
    }

    public int getIdJugador() { return idJugador; }
    public int getIndexEstacion() { return indexEstacion; }
    public TipoInteraccion getTipoInteraccion() { return tipoInteraccion; }
    public int getParametroExtra() { return parametroExtra; }
}
