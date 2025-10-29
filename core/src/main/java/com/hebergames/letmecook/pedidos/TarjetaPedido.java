package com.hebergames.letmecook.pedidos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.entregables.productos.bebidas.Bebida;
import com.hebergames.letmecook.utiles.GestorTexturas;
import com.hebergames.letmecook.utiles.Recursos;

import java.util.ArrayList;

public class TarjetaPedido {

    private final Texto TEXTO_TIEMPO;
    private final ShapeRenderer SHAPE_RENDERER;

    private final float ANCHO_TARJETA = 200f;
    private final float ALTO_TARJETA = 100f;
    private final float TAMANO_IMAGEN = 64f;
    private final float PADDING = 10f;

    public TarjetaPedido() {
        SHAPE_RENDERER = new ShapeRenderer();
        TEXTO_TIEMPO = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
    }

    public void dibujar(SpriteBatch batch, Cliente cliente, float x, float y,
                        TextureRegion texturaCliente) {

        if (cliente.getPedido().getEstadoPedido() != EstadoPedido.EN_PREPARACION) {
            return;
        }

        float porcentajeTolerancia = cliente.getPorcentajeToleranciaActual();

        batch.end();

        SHAPE_RENDERER.setProjectionMatrix(batch.getProjectionMatrix());
        SHAPE_RENDERER.begin(ShapeRenderer.ShapeType.Filled);

        SHAPE_RENDERER.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        SHAPE_RENDERER.rect(x, y, ANCHO_TARJETA, ALTO_TARJETA);

        Color colorBarra = getColorPorcentaje(porcentajeTolerancia);
        SHAPE_RENDERER.setColor(colorBarra);
        SHAPE_RENDERER.rect(x, y, ANCHO_TARJETA * porcentajeTolerancia, 5f);

        SHAPE_RENDERER.end();

        batch.begin();

        batch.draw(texturaCliente,
            x + PADDING,
            y + ALTO_TARJETA - TAMANO_IMAGEN - PADDING,
            TAMANO_IMAGEN, TAMANO_IMAGEN);

        TextureRegion cara = GestorTexturas.getInstance().getCaraPorTolerancia(porcentajeTolerancia);
        if (cara != null) {
            batch.draw(cara,
                x + PADDING + (TAMANO_IMAGEN / 2f) - 12f,
                y + ALTO_TARJETA - PADDING - 12f,
                24f, 24f);
        }

        ArrayList<Producto> productos = cliente.getPedido().getProductosSolicitados();
        int cantidadAMostrar = Math.min(productos.size(), 3);

        for (int i = 0; i < cantidadAMostrar; i++) {
            String claveTextura = obtenerClaveTextura(productos, i);

            TextureRegion texturaProductoActual = GestorTexturas.getInstance().getTexturaProducto(claveTextura);

            float offset = i * 16f;
            float tam = TAMANO_IMAGEN * 0.8f;

            batch.draw(texturaProductoActual,
                x + ANCHO_TARJETA - tam - PADDING - offset,
                y + ALTO_TARJETA - tam - PADDING - offset,
                tam, tam);
        }

        int segundos = (int) cliente.getTiempoRestante();
        TEXTO_TIEMPO.setTexto(segundos + "s");
        TEXTO_TIEMPO.setPosition(
            x + (ANCHO_TARJETA / 2f) - (TEXTO_TIEMPO.getAncho() / 2f),
            y + (ALTO_TARJETA / 2f) + (TEXTO_TIEMPO.getAlto() / 2f)
        );
        TEXTO_TIEMPO.dibujarEnUi(batch);
    }

    private static String obtenerClaveTextura(ArrayList<Producto> productos, int i) {
        Producto producto = productos.get(i);
        String claveTextura;

        if (producto instanceof Bebida) {
            Bebida bebida = (Bebida) producto;
            String nombre = bebida.getTipo();
            String tamano = bebida.getTamano().getNombre();
            claveTextura = (nombre + tamano).toLowerCase().replace(" ", "");
        } else {
            claveTextura = producto.getNombre().toLowerCase();
        }
        return claveTextura;
    }

    private Color getColorPorcentaje(float porcentaje) {
        if (porcentaje > 0.6f) {
            return Color.GREEN;
        } else if (porcentaje > 0.3f) {
            return Color.YELLOW;
        } else {
            return Color.RED;
        }
    }

    public void dispose() {
        SHAPE_RENDERER.dispose();
    }
}
