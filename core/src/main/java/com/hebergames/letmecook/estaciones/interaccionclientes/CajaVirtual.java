package com.hebergames.letmecook.estaciones.interaccionclientes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.eventos.puntaje.CallbackPuntaje;
import com.hebergames.letmecook.pedidos.EstadoPedido;
import com.hebergames.letmecook.pedidos.GestorPedidos;
import com.hebergames.letmecook.pedidos.ResultadoEntrega;
import com.hebergames.letmecook.utiles.GestorTexturas;

public class CajaVirtual extends EstacionTrabajo implements EstacionEntrega {
    private Cliente clienteVirtual;
    private GestorPedidos gestorPedidos;
    private CallbackPuntaje callbackPuntaje;
    private TextureRegion texturaCajaApagada;
    private TextureRegion texturaCajaEncendida;

    public CajaVirtual(Rectangle area) {
        super(area);
        cargarTexturas();
    }

    private void cargarTexturas() {
        this.texturaCajaApagada = GestorTexturas.getInstance().getTexturaVirtualInactiva();
        this.texturaCajaEncendida = GestorTexturas.getInstance().getTexturaVirtualActiva();
    }

    public void setGestorPedidos(GestorPedidos gestor) {
        this.gestorPedidos = gestor;
    }

    public void setCallbackPuntaje(CallbackPuntaje callback) {
        this.callbackPuntaje = callback;
    }

    public void asignarCliente(Cliente cliente) {
        this.clienteVirtual = cliente;
    }

    public boolean tieneCliente() {
        return clienteVirtual != null;
    }

    public Cliente getCliente() {
        return clienteVirtual;
    }

    public void liberarCliente() {
        this.clienteVirtual = null;
    }

    @Override
    public void alInteractuar() {
        Jugador jugador = getJugadorOcupante();
        if (jugador == null) return;

        if (tieneCliente() && clienteVirtual.getPedido().getEstadoPedido() == EstadoPedido.EN_ESPERA) {
            if (gestorPedidos != null) {
                clienteVirtual.getPedido().setEstadoPedido(EstadoPedido.EN_PREPARACION);
                clienteVirtual.resetearTiempo();
            }
            jugador.salirDeMenu();
        }

        else if (tieneCliente() &&
            clienteVirtual.getPedido().getEstadoPedido() == EstadoPedido.EN_PREPARACION &&
            jugador.getInventario() instanceof Producto) {

            Producto productoJugador = (Producto) jugador.getInventario();

            ResultadoEntrega resultado = gestorPedidos.entregarPedido(this, productoJugador);

            if (callbackPuntaje != null) {
                callbackPuntaje.onPuntosObtenidos(resultado.getPuntos());
            }

            jugador.sacarDeInventario();

            jugador.salirDeMenu();
        }
    }

    public void dibujarEstadoCaja(SpriteBatch batch) {
        if (tieneCliente()) {
            batch.draw(texturaCajaEncendida, area.x, area.y, area.width, area.height);

            TextureRegion texturaClienteVirtual = GestorTexturas.getInstance().getTexturaCliente();
            if (texturaClienteVirtual != null) {
                float clienteX = area.x + (area.width / 2f) - 32f;
                float clienteY = area.y + (area.height / 2f) - 32f;
                batch.draw(texturaClienteVirtual, clienteX, clienteY, 64f, 64f);
            }
        } else {
            batch.draw(texturaCajaApagada, area.x, area.y, area.width, area.height);
        }
    }

    public void dibujarEstado(SpriteBatch batch) {
        dibujarEstadoCaja(batch);
    }
}
