package com.hebergames.letmecook.pantallas.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.eventos.entrada.Entrada;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.utiles.Recursos;
import com.hebergames.letmecook.utiles.Render;

public class PantallaDetalleTutorial extends Pantalla {
    private final SpriteBatch BATCH;
    private final OrthographicCamera CAMARA;
    private final Viewport VIEWPORT;
    private final AnimacionTutorial ANIMACION;
    private Texture botonCerrar;
    private Rectangle areaCerrar;
    private final PantallaTutorial PANTALLA_TUTORIAL;
    private final Texto TEXTO_INFO;

    public PantallaDetalleTutorial(ElementoTutorial elemento, PantallaTutorial PANTALLA_TUTORIAL) {
        this.PANTALLA_TUTORIAL = PANTALLA_TUTORIAL;
        BATCH = Render.batch;
        CAMARA = new OrthographicCamera();
        VIEWPORT = new ScreenViewport(CAMARA);

        ANIMACION = new AnimacionTutorial(
            elemento.getRutaSpritesheet(),
            elemento.getFrameWidth(),
            elemento.getFrameHeight(),
            elemento.getCantidadFrames(),
            elemento.getFps()
        );

        try {
            botonCerrar = new Texture(Gdx.files.internal("core/src/main/java/com/hebergames/letmecook/recursos/imagenes/botonCerrar.png"));
        } catch (Exception e) {
            System.err.println("Error cargando botón cerrar");
        }

        TEXTO_INFO = new Texto(Recursos.FUENTE_MENU, 24, Color.YELLOW, true);
        if (!ANIMACION.estaCargado()) {
            TEXTO_INFO.setTexto("Tutorial no disponible");
        } else {
            TEXTO_INFO.setTexto("Presiona ESC o haz clic en X para volver");
        }
    }

    @Override
    public void show() {
        VIEWPORT.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        Entrada entrada = new Entrada();
        Gdx.input.setInputProcessor(entrada);

        float tamanoBtn = 50f;

        areaCerrar = new Rectangle(
            VIEWPORT.getWorldWidth() - tamanoBtn - 20f,
            VIEWPORT.getWorldHeight() - tamanoBtn - 20f,
            tamanoBtn,
            tamanoBtn
        );

        entrada.setCallbackClick((worldX, worldY) -> {
            System.out.println("Click en detalle tutorial: " + worldX + ", " + worldY);
            System.out.println("Área cerrar: " + areaCerrar);

            if (areaCerrar.contains(worldX, worldY)) {
                System.out.println("Cerrando detalle tutorial");
                cerrar();
            }
        });
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            cerrar();
            return;
        }

        if (ANIMACION != null) {
            ANIMACION.actualizar(delta);
        }

        VIEWPORT.apply();
        CAMARA.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        BATCH.setProjectionMatrix(CAMARA.combined);
        BATCH.begin();

        BATCH.setColor(0, 0, 0, 0.9f);
        BATCH.draw(Recursos.PIXEL, 0, 0, VIEWPORT.getWorldWidth(), VIEWPORT.getWorldHeight());
        BATCH.setColor(1, 1, 1, 1);

        if (ANIMACION != null && ANIMACION.estaCargado()) {
            TextureRegion frameActual = ANIMACION.getFrameActual();
            if (frameActual != null) {
                float escala = 4f;
                float anchoFrame = frameActual.getRegionWidth() * escala;
                float altoFrame = frameActual.getRegionHeight() * escala;

                float maxAncho = VIEWPORT.getWorldWidth() * 0.8f;
                float maxAlto = VIEWPORT.getWorldHeight() * 0.8f;

                if (anchoFrame > maxAncho) {
                    float factor = maxAncho / anchoFrame;
                    anchoFrame *= factor;
                    altoFrame *= factor;
                }

                if (altoFrame > maxAlto) {
                    float factor = maxAlto / altoFrame;
                    anchoFrame *= factor;
                    altoFrame *= factor;
                }

                float xFrame = (VIEWPORT.getWorldWidth() - anchoFrame) / 2f;
                float yFrame = (VIEWPORT.getWorldHeight() - altoFrame) / 2f;

                BATCH.setColor(Color.WHITE);
                float grosorMarco = 5f;
                BATCH.draw(Recursos.PIXEL, xFrame - grosorMarco, yFrame - grosorMarco,
                    anchoFrame + 2 * grosorMarco, altoFrame + 2 * grosorMarco);

                BATCH.setColor(0.1f, 0.1f, 0.1f, 1f);
                BATCH.draw(Recursos.PIXEL, xFrame, yFrame, anchoFrame, altoFrame);

                BATCH.setColor(1, 1, 1, 1);
                BATCH.draw(frameActual, xFrame, yFrame, anchoFrame, altoFrame);
            }
        }

        TEXTO_INFO.setPosition(
            VIEWPORT.getWorldWidth() / 2f - TEXTO_INFO.getAncho() / 2f,
            50f
        );
        TEXTO_INFO.dibujar();

        if (botonCerrar != null) {
            BATCH.draw(botonCerrar, areaCerrar.x, areaCerrar.y, areaCerrar.width, areaCerrar.height);
        }

        BATCH.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    private void cerrar() {
        cambiarPantalla(PANTALLA_TUTORIAL);
    }

    @Override
    public void resize(int width, int height) {
        VIEWPORT.update(width, height, true);

        float tamanoBton = 50f;
        areaCerrar.set(
            VIEWPORT.getWorldWidth() - tamanoBton - 20f,
            VIEWPORT.getWorldHeight() - tamanoBton - 20f,
            tamanoBton,
            tamanoBton
        );
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (botonCerrar != null) {
            botonCerrar.dispose();
        }
    }
}
