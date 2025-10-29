package com.hebergames.letmecook.pedidos;

import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.entidades.clientes.GestorClientes;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaRegistradora;
import com.hebergames.letmecook.estaciones.interaccionclientes.EstacionEntrega;
import com.hebergames.letmecook.estaciones.interaccionclientes.MesaRetiro;

import java.util.ArrayList;

public class GestorPedidos {
    private final GestorClientes GESTOR_CLIENTES;
    private final ArrayList<MesaRetiro> MESAS_RETIRO;

    public GestorPedidos(GestorClientes GESTOR_CLIENTES, ArrayList<MesaRetiro> mesas) {
        this.GESTOR_CLIENTES = GESTOR_CLIENTES;
        this.MESAS_RETIRO = mesas;
    }

    public boolean tomarPedido(CajaRegistradora caja) {
        Cliente cliente = caja.getCliente();
        if (cliente == null) {
            return false;
        }

        MesaRetiro mesaLibre = buscarMesaLibre();
        if (mesaLibre == null) {
            System.out.println("No hay mesas de retiro disponibles");
            return false;
        }

        logMem("antes setEstado");
        cliente.getPedido().setEstadoPedido(EstadoPedido.EN_PREPARACION);
        logMem("despues setEstado");

        cliente.resetearTiempo();
        logMem("despues resetearTiempo");

        cliente.setEstacionAsignada(mesaLibre);
        logMem("despues setEstacionAsignada");

        mesaLibre.asignarCliente(cliente);
        logMem("despues asignarCliente");

        caja.liberarCliente();
        logMem("despues liberarCaja");

        System.out.println("Pedido tomado. Cliente movido a mesa de retiro");
        for (int i = 0; i<cliente.getPedido().getProductosSolicitados().size(); i++) {
            System.out.println(cliente.getPedido().getProductosSolicitados().get(i).getNombre());
        }
        return true;
    }

    private void logMem(String tag) {
        long used = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024*1024);
        System.out.println(tag + " - MemUsed: " + used + " MB");
    }

    public ResultadoEntrega entregarPedido(EstacionEntrega estacion, Producto productoEntregado) {
        Cliente cliente = estacion.getCliente();
        if (cliente == null) {
            return new ResultadoEntrega(0);
        }

        Pedido pedido = cliente.getPedido();
        ArrayList<Producto> productosEsperados = pedido.getProductosSolicitados();

        boolean correcto = false;
        for (Producto esperado : productosEsperados) {
            if (productoEntregado.getNombre().equals(esperado.getNombre())) {
                correcto = true;
                productosEsperados.remove(esperado);
                break;
            }
        }

        int puntos;

        if (correcto && !productosEsperados.isEmpty()) {
            float porcentajeTiempo = cliente.getPorcentajeTiempo();
            if (porcentajeTiempo < 0.5f) {
                puntos = 50;
            } else if (porcentajeTiempo < 0.8f) {
                puntos = 35;
            } else {
                puntos = 25;
            }
            return new ResultadoEntrega(puntos);
        }

        if (correcto) {
            float porcentajeTiempo = cliente.getPorcentajeTiempo();
            if (porcentajeTiempo < 0.5f) {
                puntos = 100;
            } else if (porcentajeTiempo < 0.8f) {
                puntos = 75;
            } else {
                puntos = 50;
            }
            pedido.setEstadoPedido(EstadoPedido.COMPLETADO);
        } else {
            puntos = -25;
            pedido.setEstadoPedido(EstadoPedido.COMPLETADO);
        }

        estacion.liberarCliente();
        GESTOR_CLIENTES.removerCliente(cliente);

        return new ResultadoEntrega(puntos);
    }

    private MesaRetiro buscarMesaLibre() {
        for (MesaRetiro mesa : MESAS_RETIRO) {
            if (!mesa.tieneCliente()) {
                return mesa;
            }
        }
        return null;
    }

    public ArrayList<Cliente> getPedidosActivos() {
        return GESTOR_CLIENTES.getClientesEnPreparacion();
    }
}
