package com.hebergames.letmecook.pantallas.opciones;

import com.badlogic.gdx.graphics.Color;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.utiles.Recursos;

public class SelectorResolucion {
    private final Texto TEXTO_TITULO;
    private final Texto TEXTO_FLECHA_IZQ;
    private final Texto TEXTO_FLECHA_DER;
    private final Texto TEXTO_RESOLUCION_ACTUAL;
    private final String[] RESOLUCIONES;
    private int indiceActual;
    private float x, y;

    public SelectorResolucion(final String[] RESOLUCIONES, int indiceInicial) {
        this.RESOLUCIONES = RESOLUCIONES;
        this.indiceActual = indiceInicial;

        TEXTO_TITULO = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, true);
        TEXTO_TITULO.setTexto("Resolución: ");

        TEXTO_FLECHA_IZQ = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE, true);
        TEXTO_FLECHA_IZQ.setTexto("<");

        TEXTO_FLECHA_DER = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE, true);
        TEXTO_FLECHA_DER.setTexto(">");

        TEXTO_RESOLUCION_ACTUAL = new Texto(Recursos.FUENTE_MENU, 48, Color.YELLOW, true);
        actualizarTextoResolucion();
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
        actualizarPosiciones();
    }

    private void actualizarPosiciones() {
        TEXTO_TITULO.setPosition(x, y);

        float MARGEN_TITULO_A_FLECHA = 20f;
        float flechaIzqX = x + TEXTO_TITULO.getAncho() + MARGEN_TITULO_A_FLECHA;
        TEXTO_FLECHA_IZQ.setPosition(flechaIzqX, y);

        float MARGEN_FLECHA_A_BLOQUE = 10f;
        float bloqueX = flechaIzqX + TEXTO_FLECHA_IZQ.getAncho() + MARGEN_FLECHA_A_BLOQUE;
        float ANCHO_BLOQUE_RESOLUCION = 300f;
        float bloqueCentro = bloqueX + ANCHO_BLOQUE_RESOLUCION / 2f;

        float textoResX = bloqueCentro - (TEXTO_RESOLUCION_ACTUAL.getAncho() / 2f);
        TEXTO_RESOLUCION_ACTUAL.setPosition(textoResX, y);

        float MARGEN_BLOQUE_A_FLECHA = 10f;
        float flechaDerX = bloqueX + ANCHO_BLOQUE_RESOLUCION + MARGEN_BLOQUE_A_FLECHA;
        TEXTO_FLECHA_DER.setPosition(flechaDerX, y);
    }

    private void actualizarTextoResolucion() {
        TEXTO_RESOLUCION_ACTUAL.setTexto(RESOLUCIONES[indiceActual]);
        actualizarPosiciones();
    }

    public void siguiente() {
        indiceActual = (indiceActual + 1) % RESOLUCIONES.length;
        actualizarTextoResolucion();
        actualizarPosiciones();
    }

    public void anterior() {
        indiceActual = (indiceActual - 1 + RESOLUCIONES.length) % RESOLUCIONES.length;
        actualizarTextoResolucion();
        actualizarPosiciones();
    }

    public void dibujar() {
        actualizarPosiciones();
        TEXTO_TITULO.dibujar();
        TEXTO_FLECHA_IZQ.dibujar();
        TEXTO_RESOLUCION_ACTUAL.dibujar();
        TEXTO_FLECHA_DER.dibujar();
    }

    public String getResolucionActual() {
        return this.RESOLUCIONES[indiceActual];
    }

    public Texto getTextoFlechaIzq() {
        return this.TEXTO_FLECHA_IZQ;
    }

    public Texto getTextoFlechaDer() {
        return this.TEXTO_FLECHA_DER;
    }
}
