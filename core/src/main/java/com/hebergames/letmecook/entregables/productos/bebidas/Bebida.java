package com.hebergames.letmecook.entregables.productos.bebidas;

import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.entregables.productos.CategoriaProducto;
import com.hebergames.letmecook.entregables.productos.Producto;

public abstract class Bebida extends Producto {
    protected TamanoBebida tamano;
    protected float tiempoPreparacion;

    public Bebida(String nombre, TextureRegion textura, CategoriaProducto categoria,
                  TamanoBebida tamano, float tiempoPreparacion) {
        super(nombre, textura, categoria);
        this.tamano = tamano;
        this.tiempoPreparacion = tiempoPreparacion * tamano.getMultiplicadorTiempo();
    }

    public float getTiempoPreparacion() {
        return this.tiempoPreparacion;
    }

    public TamanoBebida getTamano() {
        return this.tamano;
    }

    public abstract String getTipo();
}
