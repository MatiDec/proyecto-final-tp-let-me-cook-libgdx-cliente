package com.hebergames.letmecook.utiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.Gdx;
import java.util.HashMap;
import java.util.Map;

public class GestorAnimacion {
    private final Texture SPRITESHEET;
    private final TextureRegion[][] REGIONES;
    private final float DURACION_FRAME;
    private final Map<String, Integer> ANIMACIONES;

    public GestorAnimacion(String ruta, int frameWidth, int frameHeight, float duracion) {
        SPRITESHEET = new Texture(Gdx.files.internal(ruta));
        REGIONES = TextureRegion.split(SPRITESHEET, frameWidth, frameHeight);
        this.DURACION_FRAME = duracion;
        ANIMACIONES = new HashMap<>();

        registrarAnimacion("vacio", 0);
        registrarAnimacion("carne", 1);
        registrarAnimacion("pan", 2);
    }

    public void registrarAnimacion(String nombre, int filaAnimacion) {
        ANIMACIONES.put(nombre.toLowerCase(), filaAnimacion);
    }

    public Animation<TextureRegion> getAnimacionPorObjeto(String nombreObjeto) {
        int fila = ANIMACIONES.getOrDefault(nombreObjeto.toLowerCase(), ANIMACIONES.get("vacio"));
        return getAnimacion(fila);
    }

    public Animation<TextureRegion> getAnimacion(int fila) {
        return new Animation<>(DURACION_FRAME, REGIONES[fila]);
    }

    public void dispose() {
        if (SPRITESHEET != null) {
            SPRITESHEET.dispose();
        }
    }
}
