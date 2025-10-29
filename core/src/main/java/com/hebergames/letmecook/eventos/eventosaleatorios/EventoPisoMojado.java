package com.hebergames.letmecook.eventos.eventosaleatorios;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.utiles.GestorTexturas;

import java.util.ArrayList;
import java.util.Random;

public class EventoPisoMojado implements EventoAleatorio {
    private final ArrayList<Rectangle> TILES_AFECTADAS;
    private boolean activo;
    private final String NOMBRE = "Piso Mojado";
    private final float PROBABILIDAD = 0.20f;

    private final ArrayList<Rectangle> TODAS_LAS_TILES;
    private final Random RANDOM;

    public EventoPisoMojado(ArrayList<Rectangle> tilesDisponibles) {
        this.TODAS_LAS_TILES = tilesDisponibles;
        this.TILES_AFECTADAS = new ArrayList<>();
        this.RANDOM = new Random();
        this.activo = false;
    }

    @Override
    public void activar() {
        if (!activo && TODAS_LAS_TILES != null && !TODAS_LAS_TILES.isEmpty()) {
            TILES_AFECTADAS.clear();

            int MIN_TILES = 3;
            int MAX_TILES = 8;
            int cantidadTiles = MIN_TILES + RANDOM.nextInt(MAX_TILES - MIN_TILES + 1);
            cantidadTiles = Math.min(cantidadTiles, TODAS_LAS_TILES.size());

            ArrayList<Rectangle> tilesDisponibles = new ArrayList<>(TODAS_LAS_TILES);

            for (int i = 0; i < cantidadTiles; i++) {
                int index = RANDOM.nextInt(tilesDisponibles.size());
                TILES_AFECTADAS.add(tilesDisponibles.remove(index));
            }

            activo = true;
        }
    }

    @Override
    public void desactivar() {
        TILES_AFECTADAS.clear();
        activo = false;
    }

    @Override
    public String getNombre() {
        return this.NOMBRE;
    }

    @Override
    public float getProbabilidad() {
        return this.PROBABILIDAD;
    }

    public boolean estaSobrePisoMojado(float x, float y) {
        if (!activo) return false;

        for (Rectangle tile : TILES_AFECTADAS) {
            if (tile.contains(x, y)) {
                return true;
            }
        }
        return false;
    }

    public void dibujar(SpriteBatch batch) {
        if (!activo) return;

        TextureRegion textura = GestorTexturas.getInstance().getTexturaPisoMojado();
        if (textura == null) return;

        for (Rectangle tile : TILES_AFECTADAS) {
            batch.draw(textura, tile.x, tile.y, tile.width, tile.height);
        }
    }
}
