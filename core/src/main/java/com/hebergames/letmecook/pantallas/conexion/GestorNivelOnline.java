package com.hebergames.letmecook.pantallas.conexion;

import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaVirtual;
import com.hebergames.letmecook.estaciones.interaccionclientes.MesaRetiro;
import com.hebergames.letmecook.eventos.eventosaleatorios.GestorEventosAleatorios;
import com.hebergames.letmecook.eventos.puntaje.GestorPuntaje;
import com.hebergames.letmecook.mapa.GestorMapa;
import com.hebergames.letmecook.mapa.niveles.NivelPartida;
import com.hebergames.letmecook.pantallas.juego.GestorTiempoJuego;
import com.hebergames.letmecook.pantallas.superposiciones.GestorMostrarCalendario;
import com.hebergames.letmecook.pedidos.GestorPedidos;

import java.util.ArrayList;

public class GestorNivelOnline {
    private static final int TIEMPO_OBJETIVO = 10;

    private GestorMapa gestorMapa;
    private GestorTiempoJuego gestorTiempo;
    private GestorMostrarCalendario gestorMostrarCalendario;
    private GestorPedidos gestorPedidos;
    private GestorPuntaje gestorPuntaje;

    public GestorNivelOnline() {
        this.gestorPuntaje = new GestorPuntaje();
    }

    public void inicializarNivel(NivelPartida nivel) {
        gestorMapa = new GestorMapa();
        gestorMapa.setMapaActual(nivel.getMapa());

        gestorTiempo = new GestorTiempoJuego(TIEMPO_OBJETIVO);
        gestorMostrarCalendario = new GestorMostrarCalendario();
        gestorMostrarCalendario.iniciarMostrar();

        inicializarSistemaPedidos(gestorMapa.getEstaciones());
    }

    private void inicializarSistemaPedidos(ArrayList<EstacionTrabajo> estaciones) {
        ArrayList<MesaRetiro> mesas = new ArrayList<>();
        ArrayList<CajaVirtual> cajasVirtuales = new ArrayList<>();

        for (EstacionTrabajo estacion : estaciones) {
            if (estacion instanceof MesaRetiro) {
                mesas.add((MesaRetiro) estacion);
            } else if (estacion instanceof CajaVirtual) {
                cajasVirtuales.add((CajaVirtual) estacion);
            }
        }

        gestorPedidos = new GestorPedidos(null, mesas);

        for (CajaVirtual cajaVirtual : cajasVirtuales) {
            cajaVirtual.setGestorPedidos(gestorPedidos);
            cajaVirtual.setCallbackPuntaje(gestorPuntaje);
        }

        for (MesaRetiro mesa : mesas) {
            mesa.setGestorPedidos(gestorPedidos);
            mesa.setCallbackPuntaje(gestorPuntaje);
        }

        GestorEventosAleatorios.getInstancia().reset();
    }

    public void reinicializar(NivelPartida nivel) {
        limpiarRecursos();
        inicializarNivel(nivel);
    }

    public void limpiarRecursos() {
        if (gestorMapa != null) {
            gestorMapa.dispose();
        }
        GestorEventosAleatorios.getInstancia().reset();
    }

    public GestorMapa getGestorMapa() {
        return gestorMapa;
    }

    public GestorTiempoJuego getGestorTiempo() {
        return gestorTiempo;
    }

    public GestorMostrarCalendario getGestorMostrarCalendario() {
        return gestorMostrarCalendario;
    }

    public GestorPuntaje getGestorPuntaje() {
        return gestorPuntaje;
    }
}
