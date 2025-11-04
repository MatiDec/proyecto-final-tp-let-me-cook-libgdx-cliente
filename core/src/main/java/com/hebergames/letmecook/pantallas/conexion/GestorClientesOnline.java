package com.hebergames.letmecook.pantallas.conexion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.entidades.clientes.TipoCliente;
import com.hebergames.letmecook.entidades.clientes.VisualizadorCliente;
import com.hebergames.letmecook.entregables.productos.GestorProductos;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.mapa.GestorMapa;
import com.hebergames.letmecook.red.paquetes.DatosCliente;
import com.hebergames.letmecook.red.paquetes.PaqueteEstado;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.GestorTexturas;

import java.util.*;

public class GestorClientesOnline {
    private Map<Integer, Cliente> clientesVisualesMap = new HashMap<>();
    private GestorProductos gestorProductos;
    private ArrayList<EstacionTrabajo> estaciones;

    public GestorClientesOnline() {
        this.gestorProductos = new GestorProductos();
    }

    public void setEstaciones(ArrayList<EstacionTrabajo> estaciones) {
        this.estaciones = estaciones;
    }

    public ArrayList<Cliente> crearClientesVisualesDesdeEstado(PaqueteEstado estado) {
        ArrayList<Cliente> clientesVisuales = new ArrayList<>();
        Set<Integer> idsActuales = new HashSet<>();

        for (DatosCliente dc : estado.getClientes()) {
            idsActuales.add(dc.id);

            Cliente clienteVisual = clientesVisualesMap.get(dc.id);

            if (clienteVisual == null) {
                clienteVisual = crearNuevoClienteVisual(dc);
                clientesVisualesMap.put(dc.id, clienteVisual);
            }

            actualizarClienteVisual(clienteVisual, dc);
            clientesVisuales.add(clienteVisual);
        }

        clientesVisualesMap.keySet().removeIf(id -> !idsActuales.contains(id));

        return clientesVisuales;
    }

    private Cliente crearNuevoClienteVisual(DatosCliente dc) {
        ArrayList<Producto> productos = new ArrayList<>();
        for (String nombreProducto : dc.productosPedido) {
            Producto p = gestorProductos.obtenerProductoPorNombre(nombreProducto);
            if (p != null) productos.add(p);
        }

        Cliente clienteVisual = new Cliente(productos, dc.tiempoRestante,
            dc.esVirtual ? TipoCliente.VIRTUAL : TipoCliente.PRESENCIAL);

        if (dc.esVirtual) {
            GestorAudio.getInstance().reproducirSonido(SonidoJuego.CLIENTE_LLEGA_VIRTUAL);
        } else {
            GestorAudio.getInstance().reproducirSonido(SonidoJuego.CLIENTE_LLEGA);
        }

        clienteVisual.getPedido().setEstadoPedido(dc.getEstadoPedido());

        TextureRegion texturaCliente = GestorTexturas.getInstance().getTexturaCliente();
        clienteVisual.setVisualizador(new VisualizadorCliente(texturaCliente));

        return clienteVisual;
    }

    private void actualizarClienteVisual(Cliente clienteVisual, DatosCliente dc) {
        clienteVisual.setPorcentajeTolerancia(dc.porcentajeTolerancia);
        clienteVisual.getPedido().setEstadoPedido(dc.getEstadoPedido());

        if (dc.indexEstacion >= 0 && dc.indexEstacion < estaciones.size()) {
            clienteVisual.setEstacionAsignada(estaciones.get(dc.indexEstacion));
        }
    }

    public void dibujarClientes(SpriteBatch batch, PaqueteEstado estado, GestorMapa gestorMapa, GestorTexturas gestorTexturas) {
        if (estado == null) return;

        for (DatosCliente dc : estado.getClientes()) {
            dibujarClienteDesdeServidor(batch, dc, gestorMapa, gestorTexturas);
        }
    }

    private void dibujarClienteDesdeServidor(SpriteBatch batch, DatosCliente datos, GestorMapa gestorMapa, GestorTexturas gestorTexturas) {
        if (datos.indexEstacion < 0 || datos.indexEstacion >= gestorMapa.getEstaciones().size()) {
            return;
        }

        EstacionTrabajo estacion = gestorMapa.getEstaciones().get(datos.indexEstacion);
        Rectangle area = estacion.area;
        float x = area.x + (area.width / 2f) - 32f;
        float y = area.y + area.height;

        TextureRegion texturaCliente = gestorTexturas.getTexturaCliente();
        if (texturaCliente != null) {
            batch.draw(texturaCliente, x, y, 64, 64);
        }

        TextureRegion cara = gestorTexturas.getCaraPorTolerancia(datos.porcentajeTolerancia);
        if (cara != null) {
            batch.draw(cara, x + 20f, y + 68f, 24f, 24f);
        }
    }

    public void limpiar() {
        clientesVisualesMap.clear();
    }

    public void dispose() {
        for (Cliente c : clientesVisualesMap.values()) {
            c.liberarRecursos();
        }
        clientesVisualesMap.clear();
    }
}
