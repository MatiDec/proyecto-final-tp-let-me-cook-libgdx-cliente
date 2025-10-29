package com.hebergames.letmecook.entidades.clientes;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.pedidos.EstadoPedido;
import com.hebergames.letmecook.pedidos.Pedido;
import com.hebergames.letmecook.utiles.GestorTexturas;

import java.util.ArrayList;

public class Cliente {
    private final Pedido PEDIDO;
    private EstacionTrabajo estacionAsignada;
    private float tiempoEspera;
    private final float TIEMPO_MAXIMO_ESPERA;
    private VisualizadorCliente visualizador;
    private static int contadorId = 0;
    private float tiempoEsperaEnCaja;
    private static final float TIEMPO_MAX_ESPERA_CAJA = 30f;
    private final TipoCliente TIPO_CLIENTE;

    public Cliente(ArrayList<Producto> productosSolicitados, float tiempoMaximoEspera, TipoCliente tipo) {
        int id = contadorId++;
        this.PEDIDO = new Pedido(productosSolicitados);
        this.TIEMPO_MAXIMO_ESPERA = tiempoMaximoEspera;
        this.tiempoEspera = 0f;
        this.visualizador = null;
        this.tiempoEsperaEnCaja = 0f;
        this.TIPO_CLIENTE = tipo;
    }

    public void actualizar(float delta) {
        EstadoPedido estado = this.PEDIDO.getEstadoPedido();

        if (estado == EstadoPedido.EN_ESPERA) {
            tiempoEsperaEnCaja += delta;
        }

        if (estado == EstadoPedido.EN_PREPARACION) {
            tiempoEspera += delta;
        }
    }

    public void inicializarVisualizador() {
        if (visualizador == null && GestorTexturas.getInstance().estanTexturasListas()) {
            TextureRegion textura = GestorTexturas.getInstance().getTexturaCliente();
            if (textura != null) {
                visualizador = new VisualizadorCliente(textura);
            }
        }
    }

    public void dibujar(SpriteBatch batch) {
        if (visualizador == null || estacionAsignada == null) {
            return;
        }

        EstadoPedido estado = PEDIDO.getEstadoPedido();
        if (estado == EstadoPedido.CANCELADO || estado == EstadoPedido.COMPLETADO) {
            return;
        }

        visualizador.dibujar(batch, this);
    }

    public void liberarRecursos() {
        this.visualizador = null;
    }

    public boolean esVirtual() {
        return this.TIPO_CLIENTE == TipoCliente.VIRTUAL;
    }

    public boolean haExpiradoTiempo() {
        return this.tiempoEspera >= this.TIEMPO_MAXIMO_ESPERA;
    }

    public float getTiempoRestante() {
        return Math.max(0, this.TIEMPO_MAXIMO_ESPERA - this.tiempoEspera);
    }

    public float getPorcentajeTiempo() {
        return this.tiempoEspera / this.TIEMPO_MAXIMO_ESPERA;
    }

    public Pedido getPedido() {
        return this.PEDIDO;
    }

    public EstacionTrabajo getEstacionAsignada() {
        return this.estacionAsignada;
    }

    public void setEstacionAsignada(EstacionTrabajo estacion) {
        this.estacionAsignada = estacion;
    }

    public boolean haExpiradoTiempoCaja() {
        return this.tiempoEsperaEnCaja >= TIEMPO_MAX_ESPERA_CAJA;
    }

    public float getPorcentajeTiempoCaja() {
        return this.tiempoEsperaEnCaja / TIEMPO_MAX_ESPERA_CAJA;
    }

    public float getPorcentajeToleranciaActual() {
        if (PEDIDO.getEstadoPedido() == EstadoPedido.EN_ESPERA) {
            return 1f - getPorcentajeTiempoCaja();
        } else if (PEDIDO.getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
            return 1f - getPorcentajeTiempo();
        }
        return 1f;
    }

    public void resetearTiempo() {
        this.tiempoEspera = 0f;
    }
}
