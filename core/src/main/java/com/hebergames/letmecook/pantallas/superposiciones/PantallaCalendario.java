package com.hebergames.letmecook.pantallas.superposiciones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.mapa.niveles.GestorPartida;
import com.hebergames.letmecook.mapa.niveles.NivelPartida;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.pantallas.juego.PantallaJuego;
import com.hebergames.letmecook.utiles.Recursos;
import com.hebergames.letmecook.utiles.Render;

import java.util.ArrayList;

public class PantallaCalendario extends Pantalla {
    private final SpriteBatch BATCH;
    private final PantallaJuego PANTALLA_JUEGO;

    private final GestorPartida GESTOR_PARTIDA;
    private final Viewport VIEWPORT;
    private final OrthographicCamera CAMARA;

    private Texto tituloCalendario;
    private final ArrayList<InfoDiaNivel> DIAS_NIVELES;

    public PantallaCalendario(PantallaJuego PANTALLA_JUEGO) {
        this.PANTALLA_JUEGO = PANTALLA_JUEGO;
        this.BATCH = Render.batch;
        this.CAMARA = new OrthographicCamera();
        this.VIEWPORT = new ScreenViewport(CAMARA);
        this.GESTOR_PARTIDA = GestorPartida.getInstancia();
        this.DIAS_NIVELES = new ArrayList<>();
    }

    @Override
    public void show() {
        VIEWPORT.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        inicializarCalendario();
    }

    private void inicializarCalendario() {
        tituloCalendario = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, true);
        tituloCalendario.setTexto("Calendario de actividades");

        DIAS_NIVELES.clear();
        ArrayList<NivelPartida> niveles = GESTOR_PARTIDA.getTodosLosNiveles();

        for (int i = 0; i < niveles.size(); i++) {
            NivelPartida nivel = niveles.get(i);
            InfoDiaNivel info = new InfoDiaNivel(i + 1, nivel);
            DIAS_NIVELES.add(info);
        }

        posicionarElementos();
    }

    private void posicionarElementos() {
        float anchoViewport = VIEWPORT.getWorldWidth();
        float altoViewport = VIEWPORT.getWorldHeight();
        float centroX = anchoViewport / 2f;

        tituloCalendario.setPosition(
            centroX - tituloCalendario.getAncho() / 2f,
            altoViewport - 80f
        );

        int cantidadDias = DIAS_NIVELES.size();
        float anchoTotal = (cantidadDias * Recursos.ANCHO_DIA) + ((cantidadDias - 1) * Recursos.ESPACIADO);
        float inicioX = centroX - (anchoTotal / 2f);
        float posY = altoViewport / 2f;

        for (int i = 0; i < DIAS_NIVELES.size(); i++) {
            InfoDiaNivel info = DIAS_NIVELES.get(i);
            float posX = inicioX + (i * (Recursos.ANCHO_DIA + Recursos.ESPACIADO));
            info.setPosicion(posX, posY);
        }
    }

    @Override
    public void render(float delta) {
        VIEWPORT.apply();
        CAMARA.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        BATCH.setProjectionMatrix(CAMARA.combined);
        BATCH.begin();

        BATCH.setColor(0, 0, 0, 0.7f);
        float anchoViewport = VIEWPORT.getWorldWidth();
        float altoViewport = VIEWPORT.getWorldHeight();
        BATCH.draw(Recursos.PIXEL, 0, 0, anchoViewport, altoViewport);
        BATCH.setColor(1, 1, 1, 1);

        tituloCalendario.dibujar();

        int nivelActualIndex = GESTOR_PARTIDA.getNivelActualIndex();
        for (int i = 0; i < DIAS_NIVELES.size(); i++) {
            InfoDiaNivel info = DIAS_NIVELES.get(i);
            boolean esNivelActual = (i == nivelActualIndex);
            info.dibujar(BATCH, esNivelActual);
        }

        BATCH.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
        if (tituloCalendario != null) {
            posicionarElementos();
        }
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {}
}
