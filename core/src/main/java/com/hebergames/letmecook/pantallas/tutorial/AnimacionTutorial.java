package com.hebergames.letmecook.pantallas.tutorial;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimacionTutorial {
    private Animation<TextureRegion> animacion;
    private float tiempoAcumulado;
    private boolean cargado;
    private final int CANTIDAD_FRAMES;

    public AnimacionTutorial(String rutaSpritesheet, int frameWidth, int frameHeight, final int CANTIDAD_FRAMES, float fps) {
        this.tiempoAcumulado = 0f;
        this.CANTIDAD_FRAMES = CANTIDAD_FRAMES;
        this.cargado = false;

        cargarAnimacion(rutaSpritesheet, frameWidth, frameHeight, fps);
    }

    private void cargarAnimacion(String rutaSpritesheet, int frameWidth, int frameHeight, float fps) {
        try {
            Texture spritesheet = new Texture(Gdx.files.internal(rutaSpritesheet));
            TextureRegion[][] regiones = TextureRegion.split(spritesheet, frameWidth, frameHeight);

            TextureRegion[] frames = new TextureRegion[CANTIDAD_FRAMES];
            int indice = 0;

            outerLoop:
            for (TextureRegion[] regione : regiones) {
                for (TextureRegion textureRegion : regione) {
                    if (indice >= CANTIDAD_FRAMES) break outerLoop;
                    frames[indice++] = textureRegion;
                }
            }

            float duracionFrame = 1f / fps;
            animacion = new Animation<>(duracionFrame, frames);
            animacion.setPlayMode(Animation.PlayMode.LOOP);
            cargado = true;

            System.out.println("Animación tutorial cargada: " + CANTIDAD_FRAMES + " frames desde " + rutaSpritesheet);

        } catch (Exception e) {
            System.err.println("Error cargando animación desde: " + rutaSpritesheet);
            e.printStackTrace();
            cargado = false;
        }
    }

    public void actualizar(float delta) {
        if (!cargado) return;
        tiempoAcumulado += delta;
    }

    public TextureRegion getFrameActual() {
        if (!cargado || animacion == null) return null;
        return animacion.getKeyFrame(tiempoAcumulado);
    }

    public boolean estaCargado() {
        return cargado;
    }

}
