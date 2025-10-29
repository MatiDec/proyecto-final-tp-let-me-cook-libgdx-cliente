package com.hebergames.letmecook.pantallas.superposiciones;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.eventos.entrada.Entrada;
import com.hebergames.letmecook.eventos.entrada.TextoInteractuable;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.pantallas.PantallaMenu;
import com.hebergames.letmecook.pantallas.juego.PantallaJuego;
import com.hebergames.letmecook.pantallas.opciones.ControlVolumen;
import com.hebergames.letmecook.pantallas.juego.GestorConfiguracion;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.utiles.Recursos;
import com.hebergames.letmecook.utiles.Render;

public class PantallaPausa extends Pantalla {

    private final PantallaJuego PANTALLA_JUEGO;
    private final SpriteBatch BATCH;

    private Texto oContinuar, oMenuPrincipal;
    private Texto tPantallaCompleta, tAplicar;
    private ControlVolumen controlVolumen;
    private boolean pantallaCompleta;

    private Entrada entrada;
    private final Viewport VIEWPORT;
    private final OrthographicCamera CAMARA;

    public PantallaPausa(PantallaJuego pantallaJuego) {
        this.PANTALLA_JUEGO = pantallaJuego;
        this.BATCH = Render.batch;
        this.CAMARA = new OrthographicCamera();
        this.VIEWPORT = new ScreenViewport(CAMARA);
        VIEWPORT.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    @Override
    public void show() {
        VIEWPORT.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        if (entrada == null) {
            entrada = new Entrada();
            inicializarOpciones();
            registrarEntradas();
        }

        Gdx.input.setInputProcessor(entrada);
        posicionarTextos();
    }

    private void inicializarOpciones() {
        oContinuar = new Texto(Recursos.FUENTE_MENU, 72, Color.WHITE, true);
        oContinuar.setTexto("Continuar");

        oMenuPrincipal = new Texto(Recursos.FUENTE_MENU, 72, Color.WHITE, true);
        oMenuPrincipal.setTexto("Menú Principal");

        controlVolumen = new ControlVolumen("Volumen: ",
            GestorConfiguracion.getInt("volumenMusica", 100));

        pantallaCompleta = GestorConfiguracion.getBoolean("pantallaCompleta", false);

        tPantallaCompleta = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, true);
        actualizarTextoPantallaCompleta();

        tAplicar = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, true);
        tAplicar.setTexto("Aplicar");
    }

    private void actualizarTextoPantallaCompleta() {
        tPantallaCompleta.setTexto("Pantalla completa: " + (pantallaCompleta ? "ON" : "OFF"));
    }

    private void registrarEntradas() {
        entrada.registrar(new TextoInteractuable(oContinuar, () -> {
            PANTALLA_JUEGO.reanudarJuego();
        }));

        entrada.registrar(new TextoInteractuable(oMenuPrincipal, () -> {
            PANTALLA_JUEGO.detenerHilos();
            cambiarPantalla(new PantallaMenu());
        }));

        entrada.registrar(new TextoInteractuable(controlVolumen.getTextoFlechaIzq(), () -> {
            controlVolumen.disminuirVolumen();
            GestorConfiguracion.set("volumenMusica",
                String.valueOf(controlVolumen.getVolumen()));
            GestorAudio.getInstance().setVolumenMusica(controlVolumen.getVolumen() / 100f);
        }));

        entrada.registrar(new TextoInteractuable(controlVolumen.getTextoFlechaDer(), () -> {
            controlVolumen.aumentarVolumen();
            GestorConfiguracion.set("volumenMusica",
                String.valueOf(controlVolumen.getVolumen()));
            GestorAudio.getInstance().setVolumenMusica(controlVolumen.getVolumen() / 100f);
        }));

        entrada.registrar(new TextoInteractuable(tPantallaCompleta, () -> {
            pantallaCompleta = !pantallaCompleta;
            actualizarTextoPantallaCompleta();
        }));

        entrada.registrar(new TextoInteractuable(tAplicar, () -> {
            GestorConfiguracion.set("pantallaCompleta", String.valueOf(pantallaCompleta));
            aplicarConfiguracion();
        }));
    }

    private void aplicarConfiguracion() {
        if (pantallaCompleta) {
            Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
        } else {
            int w = 1280;
            int h = 720;
            Gdx.graphics.setWindowedMode(w, h);
        }
        VIEWPORT.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        posicionarTextos();
    }

    private void posicionarTextos() {
        float ancho = VIEWPORT.getWorldWidth();
        float alto = VIEWPORT.getWorldHeight();
        float centroX = ancho / 2f;
        float centroY = alto / 2f;

        float espaciado = 90f;

        float inicio = centroY + 2.5f * espaciado;

        oContinuar.setPosition(centroX - oContinuar.getAncho() / 2f, inicio);

        oMenuPrincipal.setPosition(centroX - oMenuPrincipal.getAncho() / 2f,
            inicio - 1.25f * espaciado);

        controlVolumen.setPosicion(centroX - 300f, inicio - 2.5f * espaciado);

        tPantallaCompleta.setPosition(centroX - tPantallaCompleta.getAncho() / 2f,
            inicio - 3.75f * espaciado);

        tAplicar.setPosition(centroX - tAplicar.getAncho() / 2f,
            inicio - 5f * espaciado);
    }

    @Override
    public void render(float delta) {
        entrada.actualizarEntradas();
        VIEWPORT.apply();
        CAMARA.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        BATCH.setProjectionMatrix(CAMARA.combined);
        BATCH.begin();

        BATCH.setColor(0, 0, 0, 0.65f);
        BATCH.draw(Recursos.PIXEL, 0, 0, VIEWPORT.getWorldWidth(), VIEWPORT.getWorldHeight());
        BATCH.setColor(1, 1, 1, 1);

        controlVolumen.dibujar(BATCH);
        tPantallaCompleta.dibujar();
        tAplicar.dibujar();
        oContinuar.dibujar();
        oMenuPrincipal.dibujar();

        BATCH.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);
        posicionarTextos();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
