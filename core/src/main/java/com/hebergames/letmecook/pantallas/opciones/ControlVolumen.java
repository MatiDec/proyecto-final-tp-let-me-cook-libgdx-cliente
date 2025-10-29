package com.hebergames.letmecook.pantallas.opciones;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.utiles.Recursos;

public class ControlVolumen {
    private final Texto TEXTO_TITULO;
    private final Texto TEXTO_FLECHA_IZQ;
    private final Texto TEXTO_FLECHA_DER;
    private int volumenActual;
    private float x, y;

    private final int INCREMENTO = 10;
    private final int CUADRADOS_TOTAL = 10;
    private final float ANCHO_CUADRADO = 20f;
    private final float ESPACIADO_CUADRADO = 5f;
    private final float DIFERENCIA_ALTURA = 7.5f;

    public ControlVolumen(String titulo, int volumenInicial) {
        this.volumenActual = volumenInicial;
        TEXTO_TITULO = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, true);
        TEXTO_TITULO.setTexto(titulo);

        TEXTO_FLECHA_IZQ = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE, true);
        TEXTO_FLECHA_IZQ.setTexto("<");

        TEXTO_FLECHA_DER = new Texto(Recursos.FUENTE_MENU, 60, Color.WHITE, true);
        TEXTO_FLECHA_DER.setTexto(">");
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
        actualizarPosiciones();
    }

    private void actualizarPosiciones() {
        TEXTO_TITULO.setPosition(x, y);

        float anchoBarra = (CUADRADOS_TOTAL * ANCHO_CUADRADO) + ((CUADRADOS_TOTAL - 1) * ESPACIADO_CUADRADO);

        float MARGEN_TITULO_A_FLECHA = 10f;
        float flechaIzqX = x + TEXTO_TITULO.getAncho() + MARGEN_TITULO_A_FLECHA;
        TEXTO_FLECHA_IZQ.setPosition(flechaIzqX, y + DIFERENCIA_ALTURA);

        float MARGEN_FLECHA_A_BARRA = 5f;

        float inicioBarraX = flechaIzqX + TEXTO_FLECHA_IZQ.getAncho() + MARGEN_FLECHA_A_BARRA;

        float flechaDerX = inicioBarraX + anchoBarra + MARGEN_FLECHA_A_BARRA;

        TEXTO_FLECHA_DER.setPosition(flechaDerX, y + DIFERENCIA_ALTURA);
    }

    public void aumentarVolumen() {
        volumenActual = Math.min(100, volumenActual + INCREMENTO);
    }

    public void disminuirVolumen() {
        volumenActual = Math.max(0, volumenActual - INCREMENTO);
    }

    public void dibujar(SpriteBatch batch) {
        actualizarPosiciones();

        TEXTO_TITULO.dibujar();
        TEXTO_FLECHA_IZQ.dibujar();
        TEXTO_FLECHA_DER.dibujar();

        float barraY = y - (ANCHO_CUADRADO / 2) + DIFERENCIA_ALTURA;

        float MARGEN_FLECHA_A_BARRA = 5f;
        float inicioBarraX = TEXTO_FLECHA_IZQ.getX() + TEXTO_FLECHA_IZQ.getAncho() + MARGEN_FLECHA_A_BARRA;

        int cuadradosLlenos = volumenActual / INCREMENTO;
        float ALTO_CUADRADO = -40f;

        for (int i = 0; i < CUADRADOS_TOTAL; i++) {
            float cuadradoX = inicioBarraX + (i * (ANCHO_CUADRADO + ESPACIADO_CUADRADO));

            if (i < cuadradosLlenos) {
                batch.setColor(Color.WHITE);
            } else {
                batch.setColor(Color.DARK_GRAY);
            }

            batch.draw(Recursos.PIXEL, cuadradoX, barraY, ANCHO_CUADRADO, ALTO_CUADRADO);
        }

        batch.setColor(Color.WHITE);
    }

    public int getVolumen() {
        return volumenActual;
    }

    public Texto getTextoFlechaIzq() {
        return TEXTO_FLECHA_IZQ;
    }

    public Texto getTextoFlechaDer() {
        return TEXTO_FLECHA_DER;
    }
}
