package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

import java.util.ArrayList;

public class PaqueteEstado extends PaqueteRed {
    private final DatosJugador JUGADOR_1;
    private final DatosJugador JUGADOR_2;
    private final ArrayList<DatosCliente> CLIENTES;
    private final ArrayList<DatosEstacion> ESTACIONES;
    private final int PUNTAJE;
    private final int TIEMPO_RESTANTE;
    private final boolean JUEGO_TERMINADO;
    private final String RAZON_FIN;

    public PaqueteEstado(DatosJugador j1, DatosJugador j2,
                         ArrayList<DatosCliente> CLIENTES,
                         ArrayList<DatosEstacion> ESTACIONES,
                         int PUNTAJE, int TIEMPO_RESTANTE,
                         boolean JUEGO_TERMINADO, String RAZON_FIN) {
        this.JUGADOR_1 = j1;
        this.JUGADOR_2 = j2;
        this.CLIENTES = CLIENTES;
        this.ESTACIONES = ESTACIONES;
        this.PUNTAJE = PUNTAJE;
        this.TIEMPO_RESTANTE = TIEMPO_RESTANTE;
        this.JUEGO_TERMINADO = JUEGO_TERMINADO;
        this.RAZON_FIN = RAZON_FIN;
    }

    public ArrayList<DatosEstacion> getEstaciones() {
        return ESTACIONES;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.ESTADO_JUEGO;
    }

    public DatosJugador getJugador1() {
        return this.JUGADOR_1;
    }

    public DatosJugador getJugador2() {
        return this.JUGADOR_2;
    }

    public ArrayList<DatosCliente> getClientes() {
        return this.CLIENTES;
    }

    public int getPuntaje() {
        return this.PUNTAJE;
    }

    public int getTiempoRestante() {
        return this.TIEMPO_RESTANTE;
    }

    public boolean isJuegoTerminado() {
        return this.JUEGO_TERMINADO;
    }

    public String getRazonFin() {
        return this.RAZON_FIN;
    }
}
