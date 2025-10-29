package com.hebergames.letmecook.pantallas.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.eventos.entrada.Entrada;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.pantallas.PantallaMenu;
import com.hebergames.letmecook.utiles.Recursos;
import com.hebergames.letmecook.utiles.Render;

import java.util.ArrayList;

public class PantallaTutorial extends Pantalla {
    private SpriteBatch batch;
    private OrthographicCamera camara;
    private Viewport viewport;
    private Entrada entrada;
    private ArrayList<ElementoTutorial> elementosTutorial;
    private Texture botonCerrar;
    private Rectangle areaCerrar;
    private Texto titulo;

    @Override
    public void show() {
        batch = Render.batch;
        camara = new OrthographicCamera();
        viewport = new ScreenViewport(camara);
        viewport.update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        entrada = new Entrada();
        Gdx.input.setInputProcessor(entrada);

        titulo = new Texto(Recursos.FUENTE_MENU, 64, Color.WHITE, true);
        titulo.setTexto("Tutoriales");

        try {
            botonCerrar = new Texture(Gdx.files.internal("core/src/main/java/com/hebergames/letmecook/recursos/imagenes/botonCerrar.png"));
        } catch (Exception e) {
            System.err.println("Error cargando botón cerrar");
        }

        inicializarTutoriales();
        posicionarElementos();
        configurarEntrada();
    }

    private void inicializarTutoriales() {
        elementosTutorial = new ArrayList<>();

        // Formato: (título, miniatura, spritesheet, frameWidth, frameHeight, cantidadFrames, fps)
        elementosTutorial.add(new ElementoTutorial(
            "Controles Básicos",
            "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/tutoriales/miniatura_controles.jpg",
            "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/tutoriales/spritesheet_controles.jpg",
            128, 64, 12, 10f
        ));

        elementosTutorial.add(new ElementoTutorial(
            "Cocinar Recetas",
            "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/tutoriales/miniatura_recetas.png",
            "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/tutoriales/spritesheet_recetas.png",
            64, 64, 16, 12f
        ));

        elementosTutorial.add(new ElementoTutorial(
            "Atender Clientes",
            "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/tutoriales/miniatura_clientes.png",
            "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/tutoriales/spritesheet_clientes.png",
            64, 64, 10, 8f
        ));

        elementosTutorial.add(new ElementoTutorial(
            "Gestionar Pedidos",
            "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/tutoriales/miniatura_pedidos.png",
            "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/tutoriales/spritesheet_pedidos.png",
            64, 64, 14, 10f
        ));
    }

    private void posicionarElementos() {
        float anchoViewport = viewport.getWorldWidth();
        float altoViewport = viewport.getWorldHeight();

        titulo.setPosition(anchoViewport / 2f - titulo.getAncho() / 2f, altoViewport - 80f);

        float tamanoBton = 50f;
        areaCerrar = new Rectangle(
            anchoViewport - tamanoBton - 20f,
            altoViewport - tamanoBton - 20f,
            tamanoBton,
            tamanoBton
        );

        int columnas = 3;
        float espaciadoX = 50f;
        float espaciadoY = 80f;
        float anchoElemento = 200f;
        float altoElemento = 250f;

        float anchoTotal = (columnas * anchoElemento) + ((columnas - 1) * espaciadoX);

        float inicioX = (anchoViewport - anchoTotal) / 2f;
        float inicioY = altoViewport - 150f;

        for (int i = 0; i < elementosTutorial.size(); i++) {
            int fila = i / columnas;
            int columna = i % columnas;

            float x = inicioX + (columna * (anchoElemento + espaciadoX));
            float y = inicioY - (fila * (altoElemento + espaciadoY));

            elementosTutorial.get(i).setPosicion(x, y);
        }
    }

    private void configurarEntrada() {
        entrada.setCallbackClick((worldX, worldY) -> {
            System.out.println("Click en PantallaTutorial: " + worldX + ", " + worldY);
            System.out.println("Área cerrar: " + areaCerrar);

            if (areaCerrar.contains(worldX, worldY)) {
                System.out.println("Cerrando tutorial principal");
                cambiarPantalla(new PantallaMenu());
                return;
            }

            for (int i = 0; i < elementosTutorial.size(); i++) {
                ElementoTutorial elemento = elementosTutorial.get(i);
                if (elemento.fueClickeado(worldX, worldY)) {
                    System.out.println("Click en elemento tutorial " + i);
                    cambiarPantalla(new PantallaDetalleTutorial(elemento, this));
                    return;
                }
            }
        });
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            cambiarPantalla(new PantallaMenu());
            return;
        }

        entrada.actualizarEntradas();
        viewport.apply();
        camara.update();

        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camara.combined);
        batch.begin();

        titulo.dibujar();

        float mouseX = Gdx.input.getX();
        float mouseY = viewport.getWorldHeight() - Gdx.input.getY();

        for (ElementoTutorial elemento : elementosTutorial) {
            elemento.actualizarHover(mouseX, mouseY);
            elemento.dibujar(batch);
        }

        if (botonCerrar != null) {
            batch.draw(botonCerrar, areaCerrar.x, areaCerrar.y, areaCerrar.width, areaCerrar.height);
        }

        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
        posicionarElementos();
    }

    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        for (ElementoTutorial elemento : elementosTutorial) {
            elemento.dispose();
        }
        if (botonCerrar != null) {
            botonCerrar.dispose();
        }
    }
}
