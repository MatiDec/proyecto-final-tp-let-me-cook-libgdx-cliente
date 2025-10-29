package com.hebergames.letmecook.pantallas.opciones;

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
import com.hebergames.letmecook.pantallas.juego.GestorConfiguracion;
import com.hebergames.letmecook.pantallas.superposiciones.PantallaPausa;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.utiles.Recursos;
import com.hebergames.letmecook.utiles.Render;

public class PantallaOpciones extends Pantalla {

    private SpriteBatch BATCH;
    private OrthographicCamera camara;
    private Viewport viewport;
    private Entrada entrada;

    private ControlVolumen controlVolumen;
    private SelectorResolucion selectorResolucion;
    private Texto tPantallaCompleta, tAplicar, tVolver;
    private boolean pantallaCompleta;
    private final String[] RESOLUCIONES = {"840x680", "1280x720", "1366x768", "1600x900", "1920x1080", "2560x1440"};

    @Override
    public void show() {
        BATCH = Render.batch;
        camara = new OrthographicCamera();
        viewport = new ScreenViewport(camara);

        GestorConfiguracion.cargar();

        entrada = new Entrada();
        Gdx.input.setInputProcessor(entrada);

        inicializarOpciones();
        posicionarTextos();
        registrarEntradas();
    }

    private void inicializarOpciones() {
        int volumenInicial = GestorConfiguracion.getInt("volumenMusica", 100);
        pantallaCompleta = GestorConfiguracion.getBoolean("pantallaCompleta", false);
        String resActual = GestorConfiguracion.get("resolucion", "1920x1080");

        int indiceResolucion = 0;
        for (int i = 0; i < RESOLUCIONES.length; i++) {
            if (RESOLUCIONES[i].equals(resActual)) {
                indiceResolucion = i;
                break;
            }
        }

        controlVolumen = new ControlVolumen("Volumen: ", volumenInicial);
        selectorResolucion = new SelectorResolucion(RESOLUCIONES, indiceResolucion);

        tPantallaCompleta = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, true);
        actualizarTextoPantallaCompleta();

        tAplicar = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, true);
        tAplicar.setTexto("Aplicar");

        tVolver = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, true);
        tVolver.setTexto("Volver");
    }

    private void actualizarTextoPantallaCompleta() {
        tPantallaCompleta.setTexto("Pantalla completa: " + (pantallaCompleta ? "ON" : "OFF"));
    }

    private void registrarEntradas() {
        entrada.registrar(new TextoInteractuable(controlVolumen.getTextoFlechaIzq(), () -> {
            controlVolumen.disminuirVolumen();
            GestorConfiguracion.set("volumenMusica", String.valueOf(controlVolumen.getVolumen()));
            GestorAudio.getInstance().setVolumenMusica(controlVolumen.getVolumen() / 100f);
        }));

        entrada.registrar(new TextoInteractuable(controlVolumen.getTextoFlechaDer(), () -> {
            controlVolumen.aumentarVolumen();
            GestorConfiguracion.set("volumenMusica", String.valueOf(controlVolumen.getVolumen()));
            GestorAudio.getInstance().setVolumenMusica(controlVolumen.getVolumen() / 100f);
        }));

        entrada.registrar(new TextoInteractuable(tPantallaCompleta, () -> {
            pantallaCompleta = !pantallaCompleta;
            actualizarTextoPantallaCompleta();
            GestorConfiguracion.set("pantallaCompleta", String.valueOf(pantallaCompleta));
        }));

        entrada.registrar(new TextoInteractuable(selectorResolucion.getTextoFlechaIzq(), () -> {
            selectorResolucion.anterior();
        }));

        entrada.registrar(new TextoInteractuable(selectorResolucion.getTextoFlechaDer(), () -> {
            selectorResolucion.siguiente();
        }));

        entrada.registrar(new TextoInteractuable(tAplicar, () -> {
            String res = selectorResolucion.getResolucionActual();
            GestorConfiguracion.set("resolucion", res);
            GestorConfiguracion.set("pantallaCompleta", String.valueOf(pantallaCompleta));

            String[] partes = res.split("x");
            int w = Integer.parseInt(partes[0]);
            int h = Integer.parseInt(partes[1]);

            if (pantallaCompleta) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            } else {
                Gdx.graphics.setWindowedMode(w, h);
            }
        }));

        entrada.registrar(new TextoInteractuable(tVolver, () -> {
            Pantalla.limpiarPila();
            cambiarPantalla(new PantallaMenu());
        }));

    }

    private void posicionarTextos() {
        float ancho = viewport.getWorldWidth();
        float alto = viewport.getWorldHeight();

        float centroX = ancho / 2f;
        float centroY = alto / 2f;

        float espaciado = 80f;
        float inicio = centroY + 150f;

        controlVolumen.setPosicion(centroX - 300f, inicio);
        tPantallaCompleta.setPosition(centroX - tPantallaCompleta.getAncho() / 2f, inicio - espaciado);
        selectorResolucion.setPosicion(centroX - 300f, inicio - 2 * espaciado);
        tAplicar.setPosition(centroX - tAplicar.getAncho() / 2f, inicio - 3 * espaciado);
        tVolver.setPosition(centroX - tVolver.getAncho() / 2f, inicio - 4 * espaciado);
    }

    @Override
    public void render(float delta) {
        viewport.apply();
        camara.update();
        entrada.actualizarEntradas();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        BATCH.setProjectionMatrix(camara.combined);
        BATCH.begin();

        controlVolumen.dibujar(BATCH);
        tPantallaCompleta.dibujar();
        selectorResolucion.dibujar();
        tAplicar.dibujar();
        tVolver.dibujar();

        BATCH.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        posicionarTextos();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose() {}
}
