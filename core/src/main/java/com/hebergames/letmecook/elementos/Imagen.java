package com.hebergames.letmecook.elementos;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.hebergames.letmecook.utiles.Render;

public class Imagen {

    private final Sprite sprite;

    public Imagen(String ruta) {
        Texture textura = new Texture(ruta);
        sprite = new Sprite(textura);
    }

    public void dibujar() {
        this.sprite.draw(Render.batch);
    }

    public void setSize(int ancho, int alto) {
        this.sprite.setSize(ancho, alto);
    }

    public void setPosicion(float x, float y) {
        this.sprite.setPosition(x, y);
    }

}
