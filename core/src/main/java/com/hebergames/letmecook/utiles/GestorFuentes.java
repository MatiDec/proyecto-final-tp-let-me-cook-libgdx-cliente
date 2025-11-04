package com.hebergames.letmecook.utiles;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import java.util.HashMap;
import java.util.Map;

public class GestorFuentes {
    private static GestorFuentes instancia;

    private final Map<String, BitmapFont> fuentesCache = new HashMap<>();
    private final Map<String, FreeTypeFontGenerator> generadoresCache = new HashMap<>();

    private GestorFuentes() {}

    public static GestorFuentes getInstance() {
        if (instancia == null) {
            instancia = new GestorFuentes();
        }
        return instancia;
    }

    public BitmapFont obtenerFuente(String rutaFuente, int dimension, Color color, boolean sombra) {
        String clave = generarClave(rutaFuente, dimension, color, sombra);

        if (fuentesCache.containsKey(clave)) {
            return fuentesCache.get(clave);
        }


        BitmapFont fuente = crearFuente(rutaFuente, dimension, color, sombra);
        fuentesCache.put(clave, fuente);

        return fuente;
    }

    private BitmapFont crearFuente(String rutaFuente, int dimension, Color color, boolean sombra) {
        FreeTypeFontGenerator generator = obtenerGenerador(rutaFuente);

        FreeTypeFontGenerator.FreeTypeFontParameter parametro =
            new FreeTypeFontGenerator.FreeTypeFontParameter();

        parametro.size = dimension;
        parametro.color = color;

        if (sombra) {
            parametro.shadowColor = Color.BLACK;
            parametro.shadowOffsetX = 1;
            parametro.shadowOffsetY = 1;
        }

        return generator.generateFont(parametro);
    }

    private FreeTypeFontGenerator obtenerGenerador(String rutaFuente) {
        if (!generadoresCache.containsKey(rutaFuente)) {
            FreeTypeFontGenerator generator = new FreeTypeFontGenerator(
                Gdx.files.internal(rutaFuente)
            );
            generadoresCache.put(rutaFuente, generator);
        }
        return generadoresCache.get(rutaFuente);
    }

    private String generarClave(String ruta, int dimension, Color color, boolean sombra) {
        return ruta + "_" + dimension + "_" + color.toString() + "_" + sombra;
    }

    public void dispose() {
        for (BitmapFont fuente : fuentesCache.values()) {
            fuente.dispose();
        }
        fuentesCache.clear();

        for (FreeTypeFontGenerator gen : generadoresCache.values()) {
            gen.dispose();
        }
        generadoresCache.clear();
    }

    public void limpiarCache() {
        for (BitmapFont fuente : fuentesCache.values()) {
            fuente.dispose();
        }
        fuentesCache.clear();
    }
}
