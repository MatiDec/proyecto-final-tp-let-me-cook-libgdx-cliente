package com.hebergames.letmecook.sonido;

import com.hebergames.letmecook.mapa.niveles.TurnoTrabajo;

public enum CancionNivel {
    MENU("musica_menu", "core/src/main/java/com/hebergames/letmecook/recursos/audio/musica/musicaFondo1.ogg"),
    TURNO_MANANA("turno_manana", "core/src/main/java/com/hebergames/letmecook/recursos/audio/musica/turnoManiana.ogg"),
    TURNO_TARDE("turno_tarde", "core/src/main/java/com/hebergames/letmecook/recursos/audio/musica/turnoTarde.ogg"),
    TURNO_NOCHE("turno_noche", "core/src/main/java/com/hebergames/letmecook/recursos/audio/musica/turnoNoche.ogg");

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
