package com.hebergames.letmecook.pantallas.superposiciones;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.mapa.niveles.NivelPartida;
import com.hebergames.letmecook.utiles.Recursos;

public class InfoDiaNivel {

    private final int NUMERO_DIA;
    private final NivelPartida NIVEL;
    private float x, y;

    private Texto textoDia;
    private Texto textoMapa;
    private Texto textoTurno;
    private Texto textoPuntos;

    public InfoDiaNivel(final int NUMERO_DIA, final NivelPartida NIVEL) {
        this.NUMERO_DIA = NUMERO_DIA;
        this.NIVEL = NIVEL;
        inicializarTextos();
    }

    private void inicializarTextos() {
        textoDia = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, true);
        textoDia.setTexto("Día " + NUMERO_DIA);

        textoMapa = new Texto(Recursos.FUENTE_MENU, 20, Color.LIGHT_GRAY, true);
        String nombreMapa = NIVEL.getMapa().getNombre();
        textoMapa.setTexto(nombreMapa);

        textoTurno = new Texto(Recursos.FUENTE_MENU, 20, Color.LIGHT_GRAY, true);
        textoTurno.setTexto("Turno");

        Texto textoTurnoNombre = new Texto(Recursos.FUENTE_MENU, 24, Color.YELLOW, true);
        textoTurnoNombre.setTexto(NIVEL.getTurno().getNombre());

        textoPuntos = new Texto(Recursos.FUENTE_MENU, 20, Color.LIGHT_GRAY, true);
        textoPuntos.setTexto("Puntos");
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void dibujar(SpriteBatch batch, boolean esNivelActual) {
        Color colorBorde = esNivelActual ? Color.YELLOW : Color.WHITE;
        float grosorBorde = esNivelActual ? 4f : 2f;

        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(Recursos.PIXEL, x, y - Recursos.ALTO_DIA, Recursos.ANCHO_DIA, Recursos.ALTO_DIA);

        batch.setColor(colorBorde);
        batch.draw(Recursos.PIXEL, x, y - Recursos.ALTO_DIA, Recursos.ANCHO_DIA, grosorBorde);
        batch.draw(Recursos.PIXEL, x, y, Recursos.ANCHO_DIA, grosorBorde);
        batch.draw(Recursos.PIXEL, x, y - Recursos.ALTO_DIA, grosorBorde, Recursos.ALTO_DIA);
        batch.draw(Recursos.PIXEL, x + Recursos.ANCHO_DIA - grosorBorde, y - Recursos.ALTO_DIA, grosorBorde, Recursos.ALTO_DIA);

        batch.setColor(1, 1, 1, 1);

        float centroX = x + (Recursos.ANCHO_DIA / 2f);
        float offsetY = y - 30f;

        textoDia.setPosition(centroX - textoDia.getAncho() / 2f, offsetY);
        textoDia.dibujar();

        offsetY -= 40f;
        textoMapa.setPosition(centroX - textoMapa.getAncho() / 2f, offsetY);
        textoMapa.dibujar();

        offsetY -= 35f;
        textoTurno.setPosition(centroX - textoTurno.getAncho() / 2f, offsetY);
        textoTurno.dibujar();

        offsetY -= 30f;
        Texto turnoNombre = new Texto(Recursos.FUENTE_MENU, 24,
            esNivelActual ? Color.YELLOW : Color.LIGHT_GRAY, true);
        turnoNombre.setTexto(NIVEL.getTurno().getNombre());
        turnoNombre.setPosition(centroX - turnoNombre.getAncho() / 2f, offsetY);
        turnoNombre.dibujar();

        offsetY -= 40f;
        textoPuntos.setPosition(centroX - textoPuntos.getAncho() / 2f, offsetY);
        textoPuntos.dibujar();

        offsetY -= 30f;
        String puntosTexto = NIVEL.isCompletado() ?
            String.valueOf(NIVEL.getPuntajeObtenido()) : "---";
        Texto textoPuntosValor = new Texto(Recursos.FUENTE_MENU, 28,
            NIVEL.isCompletado() ? Color.GREEN : Color.GRAY, true);
        textoPuntosValor.setTexto(puntosTexto);
        textoPuntosValor.setPosition(centroX - textoPuntosValor.getAncho() / 2f, offsetY);
        textoPuntosValor.dibujar();
    }
}
