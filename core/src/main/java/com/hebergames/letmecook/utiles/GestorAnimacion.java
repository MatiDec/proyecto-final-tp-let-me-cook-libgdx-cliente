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

        String[] nombres = { "vacio", "pan", "carne", "pollo", "milanesa de carne", "milanesa de pollo", "papas",
            "nuggets", "aros de cebolla", "rabas", "hamburguesa de carne", "hamburguesa de pollo", "bandeja para milanesa de carne",
            "bandeja para milanesa de pollo", "envase de papas", "envase de nuggets", "envase de aros de cebolla", "envase de rabas"
        };

        for (int i = 0; i < nombres.length; i++) {
            registrarAnimacion(nombres[i], i);
        }
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
