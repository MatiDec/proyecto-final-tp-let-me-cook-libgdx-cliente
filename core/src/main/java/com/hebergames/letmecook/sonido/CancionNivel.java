package com.hebergames.letmecook.sonido;

import com.hebergames.letmecook.mapa.niveles.TurnoTrabajo;
import com.hebergames.letmecook.utiles.Recursos;

public enum CancionNivel {
    MENU("musica_menu", Recursos.RUTA_AUDIO + "musica/musicaFondo1.ogg"),
    TURNO_MANANA("turno_manana", Recursos.RUTA_AUDIO + "musica/turnoManiana.ogg"),
    TURNO_TARDE("turno_tarde", Recursos.RUTA_AUDIO + "musica/turnoTarde.ogg"),
    TURNO_NOCHE("turno_noche", Recursos.RUTA_AUDIO + "musica/turnoNoche.ogg");

    private final String IDENTIFICADOR;
    private final String RUTA;

    CancionNivel(String IDENTIFICADOR, String RUTA) {
        this.IDENTIFICADOR = IDENTIFICADOR;
        this.RUTA = RUTA;
    }

    public String getIdentificador() { return this.IDENTIFICADOR; }

    public String getRuta() { return this.RUTA; }

    public static CancionNivel getPorTurno(TurnoTrabajo turno) {
        switch (turno) {
            case MANANA:
                return TURNO_MANANA;
            case TARDE:
                return TURNO_TARDE;
            case NOCHE:
                return TURNO_NOCHE;
            default:
                return MENU;
        }
    }
}
