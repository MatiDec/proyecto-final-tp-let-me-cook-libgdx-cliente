package com.hebergames.letmecook.entregables.productos;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.entregables.ObjetoAlmacenable;

public abstract class Producto implements ObjetoAlmacenable {
    protected String nombre;
    protected TextureRegion textura;
    protected CategoriaProducto categoria;

    public Producto(String nombre, TextureRegion textura, CategoriaProducto categoria) {
        this.nombre = nombre;
        this.textura = textura;
        this.categoria = categoria;
    }

    public String getNombre() { return nombre; }
    public TextureRegion getTextura() { return textura; }

}
