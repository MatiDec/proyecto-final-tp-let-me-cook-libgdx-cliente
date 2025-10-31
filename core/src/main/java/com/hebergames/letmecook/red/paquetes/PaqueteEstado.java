package com.hebergames.letmecook.red.paquetes;

import com.hebergames.letmecook.red.PaqueteRed;

import java.util.ArrayList;

// === PAQUETE ESTADO COMPLETO ===
public class PaqueteEstado extends PaqueteRed {
    private DatosJugador jugador1;
    private DatosJugador jugador2;
    private ArrayList<DatosCliente> clientes;
    private ArrayList<DatosEstacion> estaciones;
    private int puntaje;
    private int tiempoRestante;
    private boolean juegoTerminado;
    private String razonFin;

    public PaqueteEstado(DatosJugador j1, DatosJugador j2,
                         ArrayList<DatosCliente> clientes,
                         ArrayList<DatosEstacion> estaciones,
                         int puntaje, int tiempoRestante,
                         boolean juegoTerminado, String razonFin) {
        this.jugador1 = j1;
        this.jugador2 = j2;
        this.clientes = clientes;
        this.estaciones = estaciones;
        this.puntaje = puntaje;
        this.tiempoRestante = tiempoRestante;
        this.juegoTerminado = juegoTerminado;
        this.razonFin = razonFin;
    }

    public ArrayList<DatosEstacion> getEstaciones() {
        return estaciones;
    }

    @Override
    public TipoPaquete getTipo() {
        return TipoPaquete.ESTADO_JUEGO;
    }

    public DatosJugador getJugador1() {
        return jugador1;
    }

    public DatosJugador getJugador2() {
        return jugador2;
    }

    public ArrayList<DatosCliente> getClientes() {
        return clientes;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public int getTiempoRestante() {
        return tiempoRestante;
    }

    public boolean isJuegoTerminado() {
        return juegoTerminado;
    }

    public String getRazonFin() {
        return razonFin;
    }
}
