package com.hebergames.letmecook.pantallas.superposiciones;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.sonido.GestorAudio;

public class GestorPantallasOverlay {

    private final PantallaPausa PANTALLA_PAUSA;
    private final PantallaCalendario PANTALLA_CALENDARIO;
    private boolean calendarioVisible;
    private boolean juegoEnPausa;
    private boolean calendarioMostradoAutomaticamente;

    private final GestorAudio gestorAudio;

    public GestorPantallasOverlay(PantallaPausa PANTALLA_PAUSA, PantallaCalendario PANTALLA_CALENDARIO,
                                  GestorAudio gestorAudio) {
        this.PANTALLA_PAUSA = PANTALLA_PAUSA;
        this.PANTALLA_CALENDARIO = PANTALLA_CALENDARIO;
        this.gestorAudio = gestorAudio;
        this.juegoEnPausa = false;
        this.calendarioVisible = false;
        this.calendarioMostradoAutomaticamente = false;
    }

    public void toggleCalendario() {
        if (calendarioMostradoAutomaticamente) {
            return;
        }

        calendarioVisible = !calendarioVisible;

        if (calendarioVisible) {
            if (juegoEnPausa) {
                juegoEnPausa = false;
            }
            PANTALLA_CALENDARIO.show();
            gestorAudio.pausarMusica();
        } else {
            gestorAudio.reanudarMusica();
        }
    }

    public void togglePausa() {
        juegoEnPausa = !juegoEnPausa;

        if (juegoEnPausa) {
            PANTALLA_PAUSA.show();
            gestorAudio.pausarMusica();
        } else {
            gestorAudio.reanudarMusica();
        }
    }

    public void reanudarJuego() {
        juegoEnPausa = false;
        gestorAudio.reanudarMusica();
    }

    public void renderOverlays(float delta, SpriteBatch batch) {
        if (juegoEnPausa) {
            PANTALLA_PAUSA.render(delta);
        } else if (calendarioVisible) {
            PANTALLA_CALENDARIO.render(delta);
        }
    }

    public void dispose() {
        if (PANTALLA_PAUSA != null) {
            PANTALLA_PAUSA.dispose();
        }
        if (PANTALLA_CALENDARIO != null) {
            PANTALLA_CALENDARIO.dispose();
        }
    }

    public void mostrarCalendarioInicial() {
        if (!calendarioVisible) {
            calendarioVisible = true;
            calendarioMostradoAutomaticamente = true;
            PANTALLA_CALENDARIO.show();
            gestorAudio.pausarMusica();
        }
    }

    public void cerrarCalendarioAutomatico() {
        if (calendarioMostradoAutomaticamente) {
            calendarioVisible = false;
            calendarioMostradoAutomaticamente = false;
            gestorAudio.reanudarMusica();
        }
    }

    public boolean isCalendarioMostradoAutomaticamente() {
        return calendarioMostradoAutomaticamente;
    }

    public boolean isJuegoEnPausa() {
        return juegoEnPausa;
    }

    public boolean isCalendarioVisible() { return calendarioVisible; }

}
