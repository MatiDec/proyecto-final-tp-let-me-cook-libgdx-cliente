package com.hebergames.letmecook.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hebergames.letmecook.entidades.Jugador;

public class GestorViewport {

    private static final float MUNDO_ANCHO = 1920f;
    private static final float MUNDO_ALTO = 1080f;
    private static final float MAX_DISTANCIA_PARA_ZOOM = 1500f;
    private static final float MIN_ZOOM = 1.0f;
    private static final float MID_ZOOM = 1.2f;
    private static final float MAX_ZOOM = 1.5f;

    private final Viewport viewportJuego;
    private final Viewport viewportUI;
    private final OrthographicCamera camaraJuego;
    private final OrthographicCamera camaraUI;

    public GestorViewport() {
        camaraJuego = new OrthographicCamera();
        camaraJuego.setToOrtho(false, 1920, 1080);
        camaraJuego.zoom = MID_ZOOM;

        camaraUI = new OrthographicCamera();
        camaraUI.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        viewportJuego = new FitViewport(MUNDO_ANCHO, MUNDO_ALTO, camaraJuego);
        viewportUI = new ScreenViewport(camaraUI);
    }

    public void actualizarCamaraDinamica(Jugador jugador1, Jugador jugador2) {
        float centerX = jugador1.getPosicion().x;
        float centerY = jugador1.getPosicion().y;
        float zoom = MAX_ZOOM;

        if (jugador2 != null) {
            centerX = (jugador1.getPosicion().x + jugador2.getPosicion().x) / 2f;
            centerY = (jugador1.getPosicion().y + jugador2.getPosicion().y) / 2f;

            float dist_x = Math.abs(jugador1.getPosicion().x - jugador2.getPosicion().x);
            float dist_y = Math.abs(jugador1.getPosicion().y - jugador2.getPosicion().y);
            float max_dist = Math.max(dist_x, dist_y);

            float normalized_dist = Math.min(max_dist, MAX_DISTANCIA_PARA_ZOOM) / MAX_DISTANCIA_PARA_ZOOM;

            zoom = MIN_ZOOM + (MAX_ZOOM - MIN_ZOOM) * normalized_dist;
        }

        camaraJuego.zoom = zoom;
        camaraJuego.position.set(centerX, centerY, 0);
        camaraJuego.update();
    }

    public void actualizarCamaraUI() {
        camaraUI.update();
    }

    public void resize(int width, int height) {
        viewportJuego.update(width, height);
        viewportUI.update(width, height, true);
    }

    public Vector2 convertirCoordenadasJuego(int screenX, int screenY) {
        Vector2 coordenadas = new Vector2(screenX, screenY);
        viewportJuego.unproject(coordenadas);
        return coordenadas;
    }

    public Vector2 convertirCoordenadasUI(int screenX, int screenY) {
        Vector2 coordenadas = new Vector2(screenX, screenY);
        viewportUI.unproject(coordenadas);
        return coordenadas;
    }

    public Viewport getViewportJuego() {
        return viewportJuego;
    }

    public Viewport getViewportUI() {
        return viewportUI;
    }

    public OrthographicCamera getCamaraJuego() {
        return camaraJuego;
    }

    public OrthographicCamera getCamaraUI() {
        return camaraUI;
    }
}
