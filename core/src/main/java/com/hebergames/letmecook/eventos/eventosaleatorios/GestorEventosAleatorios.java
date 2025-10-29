package com.hebergames.letmecook.eventos.eventosaleatorios;

import java.util.ArrayList;
import java.util.Random;

public class GestorEventosAleatorios {
    private final ArrayList<EventoAleatorio> EVENTOS_ACTIVOS;
    private final ArrayList<EventoAleatorio> EVENTOS_POSIBLES;
    private final Random RANDOM;
    private static GestorEventosAleatorios instancia;

    private GestorEventosAleatorios() {
        this.EVENTOS_ACTIVOS = new ArrayList<>();
        this.EVENTOS_POSIBLES = new ArrayList<>();
        this.RANDOM = new Random();
    }

    public static GestorEventosAleatorios getInstancia() {
        if (instancia == null) {
            instancia = new GestorEventosAleatorios();
        }
        return instancia;
    }

    public void registrarEventoPosible(EventoAleatorio evento) {
        EVENTOS_POSIBLES.add(evento);
    }

    public void iniciarRonda() {
        desactivarTodosLosEventos();

        for (EventoAleatorio evento : EVENTOS_POSIBLES) {
            if (RANDOM.nextFloat() < evento.getProbabilidad()) {
                evento.activar();
                EVENTOS_ACTIVOS.add(evento);
            }
        }
    }

    public void finalizarRonda() {
        desactivarTodosLosEventos();
    }

    private void desactivarTodosLosEventos() {
        for (EventoAleatorio evento : EVENTOS_ACTIVOS) {
            evento.desactivar();
        }
        EVENTOS_ACTIVOS.clear();
    }

    public void limpiarEventos() {
        desactivarTodosLosEventos();
        EVENTOS_POSIBLES.clear();
    }

    public EventoPisoMojado getEventoPisoMojado() {
        for (EventoAleatorio evento : EVENTOS_ACTIVOS) {
            if (evento instanceof EventoPisoMojado) {
                return (EventoPisoMojado) evento;
            }
        }
        return null;
    }

    public void reset() {
        limpiarEventos();
    }
}
