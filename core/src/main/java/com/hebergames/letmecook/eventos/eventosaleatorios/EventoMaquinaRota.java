package com.hebergames.letmecook.eventos.eventosaleatorios;

import com.hebergames.letmecook.estaciones.EstacionTrabajo;

public class EventoMaquinaRota implements EventoAleatorio {
    private final EstacionTrabajo ESTACION_AFECTADA;
    private boolean activo;
    private final float PROBABILIDAD = 0.15f;

    public EventoMaquinaRota(EstacionTrabajo estacion) {
        this.ESTACION_AFECTADA = estacion;
        this.activo = false;
    }

    @Override
    public void activar() {
        if (ESTACION_AFECTADA != null && !activo) {
            ESTACION_AFECTADA.setFueraDeServicio(true);
            activo = true;
        }
    }

    @Override
    public void desactivar() {
        if (ESTACION_AFECTADA != null && activo) {
            ESTACION_AFECTADA.setFueraDeServicio(false);
            activo = false;
        }
    }

    @Override
    public String getNombre() {
        return "Máquina Fuera de Servicio";
    }

    @Override
    public float getProbabilidad() {
        return this.PROBABILIDAD;
    }
}
