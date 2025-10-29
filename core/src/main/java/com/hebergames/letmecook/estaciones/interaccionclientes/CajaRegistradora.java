package com.hebergames.letmecook.estaciones.interaccionclientes;

import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.pedidos.EstadoPedido;
import com.hebergames.letmecook.pedidos.GestorPedidos;

public class CajaRegistradora extends EstacionTrabajo {
    private Cliente clienteAsignado;
    private GestorPedidos gestorPedidos;

    public CajaRegistradora(Rectangle area) {
        super(area);
    }

    public void setGestorPedidos(GestorPedidos gestor) {
        this.gestorPedidos = gestor;
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

    public void liberarCliente() {
        this.clienteAsignado = null;
    }

    public boolean tomarPedido() {
        if (gestorPedidos != null && tieneCliente() &&
                clienteAsignado.getPedido().getEstadoPedido() == EstadoPedido.EN_ESPERA) {
            return gestorPedidos.tomarPedido(this);
        }
        return false;
    }

    @Override
    public void alInteractuar() {

    }
}
