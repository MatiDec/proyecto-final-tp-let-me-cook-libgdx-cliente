package com.hebergames.letmecook.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.mapa.niveles.GestorPartida;
import com.hebergames.letmecook.mapa.niveles.NivelPartida;
import com.hebergames.letmecook.pantallas.superposiciones.InfoDiaNivel;
import com.hebergames.letmecook.utiles.Recursos;
import com.hebergames.letmecook.utiles.Render;

import java.util.ArrayList;

public class PantallaFinal extends Pantalla {

    private final String tiempo;
    private final int puntaje;

    private Texto titulo;
    private Texto resumenTiempo;
    private Texto resumenPuntaje;
    private Texto opcionMenu;
    private ArrayList<InfoDiaNivel> diasNiveles;
    private final boolean despedido;
    private final String razonDespido;
    private Texto textoDespido;
    private Texto textoRazon;

    private SpriteBatch batch;

    private float tiempoTranscurrido = 0f;
    private final float TIEMPO_MAXIMO = 10f;

    public PantallaFinal(String tiempo, int puntaje, boolean despedido, String razonDespido) {
        this.tiempo = tiempo;
        this.puntaje = puntaje;
        this.despedido = despedido;
        this.razonDespido = razonDespido;
    }

    @Override
    public void show() {
        batch = Render.batch;
        GestorPartida gestorPartida = GestorPartida.getInstancia();

        if (despedido) {
            titulo = new Texto(Recursos.FUENTE_MENU, 64, Color.RED, true);
            titulo.setTexto("¡HAS SIDO DESPEDIDO!");
        } else {
            titulo = new Texto(Recursos.FUENTE_MENU, 64, Color.WHITE, true);
            titulo.setTexto("¡Partida Finalizada!");
        }

        resumenPuntaje = new Texto(Recursos.FUENTE_MENU, 40, Color.YELLOW, true);
        resumenPuntaje.setTexto("Puntaje Total: " + puntaje);

        opcionMenu = new Texto(Recursos.FUENTE_MENU, 28, Color.YELLOW, true);
        opcionMenu.setTexto("Presiona ENTER para volver al menú");

        resumenTiempo = new Texto(Recursos.FUENTE_MENU, 40, Color.CYAN, true);
        resumenTiempo.setTexto("Tiempo total: " + tiempo);

        if (despedido) {
            textoDespido = new Texto(Recursos.FUENTE_MENU, 36, Color.RED, true);
            textoDespido.setTexto("Razón del despido:");

            textoRazon = new Texto(Recursos.FUENTE_MENU, 30, Color.ORANGE, true);
            textoRazon.setTexto(razonDespido);
        }

        diasNiveles = new ArrayList<>();
        ArrayList<NivelPartida> niveles = gestorPartida.getTodosLosNiveles();
        for (int i = 0; i < niveles.size(); i++) {
            NivelPartida nivel = niveles.get(i);
            InfoDiaNivel info = new InfoDiaNivel(i + 1, nivel);
            diasNiveles.add(info);
        }

        posicionarTarjetas();
    }

    private void posicionarTarjetas() {
        float anchoVentana = Gdx.graphics.getWidth();
        float altoVentana = Gdx.graphics.getHeight();

        int cantidadDias = diasNiveles.size();
        float escalaX = anchoVentana / 1920f;
        float escalaY = altoVentana / 1080f;

        float anchoTotal = (cantidadDias * Recursos.ANCHO_DIA * escalaX) + ((cantidadDias - 1) * Recursos.ESPACIADO * escalaX);
        float inicioX = (anchoVentana - anchoTotal) / 2f;
        float posY = altoVentana / 2f + 50f * escalaY;

        for (int i = 0; i < diasNiveles.size(); i++) {
            InfoDiaNivel info = diasNiveles.get(i);
            float posX = inicioX + (i * (Recursos.ANCHO_DIA * escalaX + Recursos.ESPACIADO * escalaX));
            info.setPosicion(posX, posY);
        }

        titulo.setPosition(Gdx.graphics.getWidth()/2f - titulo.getAncho()/2f,
            Gdx.graphics.getHeight() - 100 * escalaY);
        resumenPuntaje.setPosition(Gdx.graphics.getWidth()/2f - resumenPuntaje.getAncho()/2f,
            Gdx.graphics.getHeight() - 180 * escalaY);
        opcionMenu.setPosition(Gdx.graphics.getWidth()/2f - opcionMenu.getAncho()/2f,
            80 * escalaY);

        if (despedido && textoDespido != null && textoRazon != null) {
            textoDespido.setPosition(Gdx.graphics.getWidth()/2f - textoDespido.getAncho()/2f,
                Gdx.graphics.getHeight() - 280 * escalaY);
            textoRazon.setPosition(Gdx.graphics.getWidth()/2f - textoRazon.getAncho()/2f,
                Gdx.graphics.getHeight() - 330 * escalaY);
        }
    }

    @Override
    public void render(float delta) {
        tiempoTranscurrido += delta;

        if (tiempoTranscurrido >= TIEMPO_MAXIMO) {
            Pantalla.cambiarPantalla(new PantallaMenu());
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        titulo.dibujarEnUi(batch);
        resumenTiempo.dibujarEnUi(batch);
        resumenPuntaje.dibujarEnUi(batch);
        opcionMenu.dibujarEnUi(batch);

        if (despedido && textoDespido != null && textoRazon != null) {
            textoDespido.dibujarEnUi(batch);
            textoRazon.dibujarEnUi(batch);
        }

        for (InfoDiaNivel info : diasNiveles) {
            info.dibujar(batch, false);
        }
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            Pantalla.cambiarPantalla(new PantallaMenu());
        }
    }

    @Override
    public void resize(int width, int height) {
        if (titulo != null) {
            posicionarTarjetas();
        }
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
