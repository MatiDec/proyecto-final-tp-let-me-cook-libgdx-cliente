package com.hebergames.letmecook.utiles;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class Recursos {

    public static final String RUTA_IMAGENES = "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/";
    public static final String RUTA_FUENTES = "core/src/main/java/com/hebergames/letmecook/recursos/fuentes/";
    public static final String RUTA_AUDIO = "core/src/main/java/com/hebergames/letmecook/recursos/audio/";
    public static final String RUTA_MAPAS = "core/src/main/java/com/hebergames/letmecook/recursos/mapas/";

    public static final String FONDO = RUTA_IMAGENES + "pruebadeimagen.png";
    public static final String FUENTE_MENU = RUTA_FUENTES + "Chewy-Regular.ttf";
    public static final Texture PIXEL = new Texture(RUTA_IMAGENES + "pixel.png");
    public static final String MAQUINAS_SPRITESHEET = RUTA_IMAGENES + "maquinas.png";
    private static final Texture TEXTURA_INGREDIENTES = new Texture(RUTA_IMAGENES + "ingredientes.png");
    public static final TextureRegion INGREDIENTES = new TextureRegion(TEXTURA_INGREDIENTES);
    public static final String ARCHIVO_CONFIG = "core/src/main/java/com/hebergames/letmecook/configuracion/configuracion.txt";


    public static final String JUGADOR_SPRITESHEET = RUTA_IMAGENES + "Jugador.png";
    public static final String CLIENTES_SPRITESHEET = RUTA_IMAGENES + "clientes.jpg";
    public static final String CARAS_SPRITESHEET = RUTA_IMAGENES + "caras.jpg";
    public static final String PRODUCTOS_SPRITESHEET = RUTA_IMAGENES + "productos.png";
    public static final String BEBIDAS_SPRITESHEET = RUTA_IMAGENES + "bebidas.png";
    public static final String TEMPORIZADORES_SPRITESHEET = RUTA_IMAGENES + "temporizadores.png";
    public static final String PISO_MOJADO = RUTA_IMAGENES + "piso_mojado.png";
    public static final String ERROR_ICON = RUTA_IMAGENES + "error_icon.png";
    public static final String FLECHA = RUTA_IMAGENES + "texturaFlecha.png";

    public static final int MEDIDA_TILE = 128;
    public static final float ESPACIADO = 10f;
    public static final float ANCHO_DIA = 120f;
    public static final float ALTO_DIA = 240f;

    public static final int SPRITE_JUGADOR_WIDTH = 32;
    public static final int SPRITE_JUGADOR_HEIGHT = 32;
    public static final int SPRITE_CLIENTE_WIDTH = 32;
    public static final int SPRITE_CLIENTE_HEIGHT = 32;
    public static final int SPRITE_ITEM_WIDTH = 32;
    public static final int SPRITE_ITEM_HEIGHT = 32;

}
