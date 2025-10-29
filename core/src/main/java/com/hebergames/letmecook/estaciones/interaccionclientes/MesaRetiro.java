package com.hebergames.letmecook.estaciones.interaccionclientes;

import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.eventos.puntaje.CallbackPuntaje;
import com.hebergames.letmecook.pedidos.GestorPedidos;
import com.hebergames.letmecook.pedidos.ResultadoEntrega;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;

public class MesaRetiro extends EstacionTrabajo implements EstacionEntrega {
    private Cliente clienteAsignado;
    private GestorPedidos gestorPedidos;
    private CallbackPuntaje callbackPuntaje;

    public MesaRetiro(Rectangle area) {
        super(area);
    }

    public void asignarCliente(Cliente cliente) {
        this.clienteAsignado = cliente;
    }

    public boolean tieneCliente() {
        return clienteAsignado != null;
    }

    public Cliente getCliente() {
        return clienteAsignado;
    }

    public void setGestorPedidos(GestorPedidos gestor) {
        this.gestorPedidos = gestor;
    }

    public void setCallbackPuntaje(CallbackPuntaje callback) {
        this.callbackPuntaje = callback;
    }

    public void entregarProducto(Producto producto) {
        if (gestorPedidos != null && tieneCliente() && producto != null) {
            ResultadoEntrega resultado = gestorPedidos.entregarPedido(this, producto);

            if (callbackPuntaje != null) {
                callbackPuntaje.onPuntosObtenidos(resultado.getPuntos());
            }

            if (resultado.getPuntos() > 0) {
                GestorAudio.getInstance().reproducirSonido(SonidoJuego.PEDIDO_ENTREGADO);
            } else {
                GestorAudio.getInstance().reproducirSonido(SonidoJuego.PEDIDO_INCORRECTO);
            }

        }
    }

    public void liberarCliente() {
        this.clienteAsignado = null;
    }

    @Override
    public void alInteractuar() {}
}
