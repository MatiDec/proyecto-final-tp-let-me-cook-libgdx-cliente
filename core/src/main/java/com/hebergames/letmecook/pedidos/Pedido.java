package com.hebergames.letmecook.pedidos;

import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.utiles.Aleatorio;

import java.util.ArrayList;

public class Pedido {

    private final ArrayList<Producto> PRODUCTOS_SOLICITADOS;
    private static final int MAX_PRODUCTOS_POR_PEDIDO = 3;
    private EstadoPedido estadoPedido;

    public Pedido(final ArrayList<Producto> PRODUCTOS_SOLICITADOS) {
        this.PRODUCTOS_SOLICITADOS = PRODUCTOS_SOLICITADOS;
        this.estadoPedido = EstadoPedido.EN_ESPERA;
    }

    public EstadoPedido getEstadoPedido() {
        return this.estadoPedido;
    }

    public void setEstadoPedido(EstadoPedido estado) {
        this.estadoPedido = estado;
    }

    public ArrayList<Producto> getProductosSolicitados() {
        return this.PRODUCTOS_SOLICITADOS;
    }

    public static int getCantidadProductosAleatorios() {
        return 1 + Aleatorio.entero(MAX_PRODUCTOS_POR_PEDIDO);
    }

}
