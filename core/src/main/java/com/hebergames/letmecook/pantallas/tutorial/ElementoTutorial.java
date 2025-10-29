package com.hebergames.letmecook.pantallas.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.elementos.EfectoHover;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.utiles.Recursos;

public class ElementoTutorial {
    private final String RUTA_MINIATURA;
    private final String RUTA_SPRITESHEET;
    private final int ANCHO_FRAME;
    private final int ALTURA_FRAME;
    private final int CANTIDAD_FRAMES;
    private final float FRAMES_POR_SEGUNDO;
    private Texture miniatura;
    private final Texto TEXTO_TITULO;
    private float x, y;
    private final float ANCHO = 200f;
    private final float ALTO = 250f;
    private final EfectoHover EFECTO_HOVER;

    public ElementoTutorial(String titulo, final String RUTA_MINIATURA, final String RUTA_SPRITESHEET,
                            final int ANCHO_FRAME, final int ALTURA_FRAME, final int CANTIDAD_FRAMES, final float FRAMES_POR_SEGUNDO) {
        this.RUTA_MINIATURA = RUTA_MINIATURA;
        this.RUTA_SPRITESHEET = RUTA_SPRITESHEET;
        this.ANCHO_FRAME = ANCHO_FRAME;
        this.ALTURA_FRAME = ALTURA_FRAME;
        this.CANTIDAD_FRAMES = CANTIDAD_FRAMES;
        this.FRAMES_POR_SEGUNDO = FRAMES_POR_SEGUNDO;
        cargarTextura();

        TEXTO_TITULO = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, true);
        TEXTO_TITULO.setTexto(titulo);

        EFECTO_HOVER = new EfectoHover(TEXTO_TITULO, Color.YELLOW);
    }

    private void cargarTextura() {
        try {
            miniatura = new Texture(Gdx.files.internal(RUTA_MINIATURA));
        } catch (Exception e) {
            System.err.println("Error cargando miniatura: " + RUTA_MINIATURA);
            miniatura = Recursos.PIXEL;
        }
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
        TEXTO_TITULO.setPosition(x + (ANCHO - TEXTO_TITULO.getAncho()) / 2f, y - 40f);
    }

    public void dibujar(SpriteBatch batch) {
        batch.setColor(0.2f, 0.2f, 0.2f, 1f);
        batch.draw(Recursos.PIXEL, x, y - ALTO, ANCHO, ALTO);

        batch.setColor(Color.WHITE);
        float grosor = 2f;
        batch.draw(Recursos.PIXEL, x, y - ALTO, ANCHO, grosor);
        batch.draw(Recursos.PIXEL, x, y, ANCHO, grosor);
        batch.draw(Recursos.PIXEL, x, y - ALTO, grosor, ALTO);
        batch.draw(Recursos.PIXEL, x + ANCHO - grosor, y - ALTO, grosor, ALTO);

        float miniaturaAlto = 150f;
        batch.setColor(Color.WHITE);
        batch.draw(miniatura, x + 10f, y - miniaturaAlto - 10f, ANCHO - 20f, miniaturaAlto);

        TEXTO_TITULO.dibujar();

        batch.setColor(Color.WHITE);
    }

    public void actualizarHover(float mouseX, float mouseY) {
        EFECTO_HOVER.actualizar(mouseX, mouseY);

        if (EFECTO_HOVER.isEnHover()) {
            TEXTO_TITULO.getFuente().setColor(Color.YELLOW);
        } else {
            TEXTO_TITULO.getFuente().setColor(Color.WHITE);
        }
    }

    public boolean fueClickeado(float mouseX, float mouseY) {
        return mouseX >= x && mouseX <= x + ANCHO &&
            mouseY >= y - ALTO && mouseY <= y;
    }

    public String getRutaSpritesheet() {
        return RUTA_SPRITESHEET;
    }

    public int getFrameWidth() {
        return ANCHO_FRAME;
    }

    public int getFrameHeight() {
        return ALTURA_FRAME;
    }

    public int getCantidadFrames() {
        return CANTIDAD_FRAMES;
    }

    public float getFps() {
        return FRAMES_POR_SEGUNDO;
    }

    public void dispose() {
        if (miniatura != null) {
            miniatura.dispose();
        }
    }
}
