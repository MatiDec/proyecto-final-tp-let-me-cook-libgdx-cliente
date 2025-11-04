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
        registrarAnimacion("pan", 1);
        registrarAnimacion("carne", 2);
        registrarAnimacion("pollo", 3);
        registrarAnimacion("milanesa de carne", 4);
        registrarAnimacion("milanesa de pollo", 5);
        registrarAnimacion("papas", 6);
        registrarAnimacion("nuggets", 7);
        registrarAnimacion("aros de cebolla", 8);
        registrarAnimacion("rabas", 9);
        registrarAnimacion("hamburguesa de carne", 10);
        registrarAnimacion("hamburguesa de pollo", 11);
        registrarAnimacion("bandeja para milanesa de carne", 12);
        registrarAnimacion("bandeja para milanesa de pollo", 13);
        registrarAnimacion("envase de papas", 14);
        registrarAnimacion("envase de nuggets", 15);
        registrarAnimacion("envase de aros de cebolla", 16);
        registrarAnimacion("envase de rabas", 17);

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
