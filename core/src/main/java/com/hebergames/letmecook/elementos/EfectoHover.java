package com.hebergames.letmecook.elementos;

import com.badlogic.gdx.graphics.Color;

public class EfectoHover {

    private final Texto texto;
    private final Color colorOriginal;
    private final Color colorHover;
    private boolean estaEnHover = false;

    public EfectoHover(Texto texto, Color colorHover) {
        this.texto = texto;
        this.colorOriginal = new Color(texto.getFuente().getColor());
        this.colorHover = colorHover;
    }

    public void actualizar(float mouseX, float mouseY) {
        boolean mouseEncima = texto.fueClickeado(mouseX, mouseY);

        if (mouseEncima && !estaEnHover) {
            aplicarHover();
            estaEnHover = true;
        } else if (!mouseEncima && estaEnHover) {
            quitarHover();
            estaEnHover = false;
        }
    }

    private void aplicarHover() {
        this.texto.getFuente().setColor(colorHover);
    }

    private void quitarHover() {
        this.texto.getFuente().setColor(colorOriginal);
    }

    public boolean isEnHover() {
        return this.estaEnHover;
    }
}
