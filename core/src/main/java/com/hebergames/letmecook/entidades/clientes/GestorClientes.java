package com.hebergames.letmecook.entidades.clientes;

import com.hebergames.letmecook.entregables.productos.CategoriaProducto;
import com.hebergames.letmecook.entregables.productos.GestorProductos;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaVirtual;
import com.hebergames.letmecook.mapa.niveles.TurnoTrabajo;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaRegistradora;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.estaciones.interaccionclientes.MesaRetiro;
import com.hebergames.letmecook.pedidos.CallbackPenalizacion;
import com.hebergames.letmecook.pedidos.EstadoPedido;
import com.hebergames.letmecook.pedidos.Pedido;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.Aleatorio;

import java.util.ArrayList;

public class GestorClientes {
    private final ArrayList<Cliente> CLIENTES_ACTIVOS;
    private final ArrayList<CajaRegistradora> CAJAS_DISPONIBLES;
    private float tiempoParaSiguienteCliente;
    private final float INTERVALO_SPAWN;
    private final TurnoTrabajo TURNO_ACTUAL;
    private CallbackPenalizacion callbackPenalizacion;
    private int ultimaCantidadClientes = 0;
    private int clientesAtendidos;
    private int clientesPerdidos;
    private final int MIN_CLIENTES_REQUERIDOS;
    private final int MAX_CLIENTES_TOTALES;
    private final GestorProductos GESTOR_PRODUCTOS;
    private final ArrayList<CajaVirtual> CAJAS_VIRTUALES;
    private boolean clienteGeneradoEnEsteCiclo = false;

    public GestorClientes(ArrayList<CajaRegistradora> cajas, ArrayList<CajaVirtual> cajasVirtuales,
                          float intervaloSpawn, TurnoTrabajo turno, int minClientesRequeridos) {
        this.CLIENTES_ACTIVOS = new ArrayList<>();
        this.CAJAS_DISPONIBLES = cajas;
        this.CAJAS_VIRTUALES = cajasVirtuales;
        this.GESTOR_PRODUCTOS = new GestorProductos();
        this.INTERVALO_SPAWN = intervaloSpawn;
        this.tiempoParaSiguienteCliente = intervaloSpawn;
        this.TURNO_ACTUAL = turno;
        this.clientesAtendidos = 0;
        this.clientesPerdidos = 0;
        this.MIN_CLIENTES_REQUERIDOS = minClientesRequeridos;
        this.MAX_CLIENTES_TOTALES = minClientesRequeridos;
    }

    public void setCallbackPenalizacion(CallbackPenalizacion callback) {
        this.callbackPenalizacion = callback;
    }

    public void actualizar(float delta) {
        clienteGeneradoEnEsteCiclo = false;

        for (int i = CLIENTES_ACTIVOS.size() - 1; i >= 0; i--) {
            Cliente cliente = CLIENTES_ACTIVOS.get(i);
            cliente.actualizar(delta);

            EstadoPedido estado = cliente.getPedido().getEstadoPedido();

            if (estado == EstadoPedido.COMPLETADO) {
                liberarEstacion(cliente);
                cliente.liberarRecursos();
                CLIENTES_ACTIVOS.remove(i);
                clientesAtendidos++;

            } else if (cliente.haExpiradoTiempo() && estado == EstadoPedido.EN_PREPARACION) {
                GestorAudio.getInstance().reproducirSonido(SonidoJuego.CLIENTE_SE_VA);
                aplicarPenalizacion(-50, "Cliente se fue por timeout en preparación");
                cliente.getPedido().setEstadoPedido(EstadoPedido.CANCELADO);
                liberarEstacion(cliente);
                cliente.liberarRecursos();
                CLIENTES_ACTIVOS.remove(i);
                clientesPerdidos++;

            } else if (cliente.haExpiradoTiempoCaja() && estado == EstadoPedido.EN_ESPERA) {
                GestorAudio.getInstance().reproducirSonido(SonidoJuego.CLIENTE_SE_VA);
                aplicarPenalizacion(-30, "Cliente se fue sin ser atendido");
                cliente.getPedido().setEstadoPedido(EstadoPedido.CANCELADO);
                liberarEstacion(cliente);
                cliente.liberarRecursos();
                CLIENTES_ACTIVOS.remove(i);
                clientesPerdidos++;
            }
        }

        tiempoParaSiguienteCliente -= delta;
        if (tiempoParaSiguienteCliente <= 0 && !clienteGeneradoEnEsteCiclo) {
            generarNuevoCliente();
            clienteGeneradoEnEsteCiclo = true;
            tiempoParaSiguienteCliente = INTERVALO_SPAWN;
        }
    }

    private void generarNuevoCliente() {
        int MAX_CLIENTES_SIMULTANEOS = 5;

        if (CLIENTES_ACTIVOS.size() >= MAX_CLIENTES_SIMULTANEOS) return;

        int totalClientesProcesados = clientesAtendidos + clientesPerdidos;
        if (totalClientesProcesados >= MAX_CLIENTES_TOTALES) return;

        EstacionTrabajo estacion = null;
        TipoCliente tipo = null;

        if (!CAJAS_VIRTUALES.isEmpty()) {
            CajaVirtual cajaVirtual = buscarCajaVirtualLibre();
            if (cajaVirtual != null && Aleatorio.booleano()) {
                estacion = cajaVirtual;
                tipo = TipoCliente.VIRTUAL;
            }
        }

        if (estacion == null) {
            CajaRegistradora cajaFisica = buscarCajaLibre();
            if (cajaFisica != null) {
                estacion = cajaFisica;
                tipo = TipoCliente.PRESENCIAL;
            }
        }

        if (estacion != null) {
            crearYAsignarCliente(estacion, tipo);
            if (tipo == TipoCliente.VIRTUAL) {
                GestorAudio.getInstance().reproducirSonido(SonidoJuego.CLIENTE_LLEGA_VIRTUAL.getIdentificador());
            } else {
                GestorAudio.getInstance().reproducirSonido(SonidoJuego.CLIENTE_LLEGA.getIdentificador());
            }
        }
    }

    private void crearYAsignarCliente(EstacionTrabajo estacion, TipoCliente tipo) {
        CategoriaProducto[] categoriasActuales = TURNO_ACTUAL.getCategoriasProductos();
        int cantidadProductos = Pedido.getCantidadProductosAleatorios();
        ArrayList<Producto> productosDelPedido = new ArrayList<>();

        for (int i = 0; i < cantidadProductos; i++) {
            Producto productoAleatorio = GESTOR_PRODUCTOS.obtenerProductoAleatorioPorCategorias(categoriasActuales);
            if (productoAleatorio != null) {
                productosDelPedido.add(productoAleatorio);
            }
        }

        if (productosDelPedido.isEmpty()) return;

        float tiempoMaximo = Aleatorio.decimalRango(60f, 90f);
        Cliente nuevoCliente = new Cliente(productosDelPedido, tiempoMaximo, tipo);
        nuevoCliente.inicializarVisualizador();
        nuevoCliente.setEstacionAsignada(estacion);

        if (estacion instanceof CajaVirtual) {
            ((CajaVirtual) estacion).asignarCliente(nuevoCliente);
        } else if (estacion instanceof CajaRegistradora) {
            ((CajaRegistradora) estacion).asignarCliente(nuevoCliente);
        }

        CLIENTES_ACTIVOS.add(nuevoCliente);
    }

    private CajaVirtual buscarCajaVirtualLibre() {
        for (CajaVirtual caja : CAJAS_VIRTUALES) {
            if (!caja.tieneCliente()) {
                return caja;
            }
        }
        return null;
    }

    private CajaRegistradora buscarCajaLibre() {
        for (CajaRegistradora caja : CAJAS_DISPONIBLES) {
            if (!caja.tieneCliente()) {
                return caja;
            }
        }
        return null;
    }

    private void aplicarPenalizacion(int puntos, String razon) {
        if (callbackPenalizacion != null) {
            callbackPenalizacion.aplicarPenalizacion(puntos, razon);
            System.out.println("PENALIZACION APLICADA: " + razon);
        }
    }

    private void liberarEstacion(Cliente cliente) {
        EstacionTrabajo estacion = cliente.getEstacionAsignada();
        if (estacion != null) {
            if (estacion instanceof CajaRegistradora) {
                ((CajaRegistradora) estacion).liberarCliente();
            } else if (estacion instanceof MesaRetiro) {
                ((MesaRetiro) estacion).liberarCliente();
            } else if (estacion instanceof CajaVirtual) {
                ((CajaVirtual) estacion).liberarCliente();
            }
        }
        cliente.setEstacionAsignada(null);
    }

    public void removerCliente(Cliente cliente) {
        CLIENTES_ACTIVOS.remove(cliente);
        liberarEstacion(cliente);
    }

    public ArrayList<Cliente> getClientesActivos() {
        return this.CLIENTES_ACTIVOS;
    }

    public ArrayList<Cliente> getClientesEnPreparacion() {
        ArrayList<Cliente> enPreparacion = new ArrayList<>();
        for (Cliente cliente : CLIENTES_ACTIVOS) {
            if (cliente.getPedido().getEstadoPedido() == EstadoPedido.EN_PREPARACION) {
                enPreparacion.add(cliente);
            }
        }
        return enPreparacion;
    }

    public int getUltimaCantidadClientes() {
        return this.ultimaCantidadClientes;
    }

    public int getClientesAtendidos() {
        return clientesAtendidos;
    }

    public int getMinClientesRequeridos() {
        return MIN_CLIENTES_REQUERIDOS;
    }

    public boolean cumpleRequisitoMinimo() {
        return clientesAtendidos < MIN_CLIENTES_REQUERIDOS;
    }

    public void actualizarUltimaCantidadClientes() {
        this.ultimaCantidadClientes = CLIENTES_ACTIVOS.size();
    }

    public boolean haAlcanzadoLimiteClientes() {
        return (clientesAtendidos + clientesPerdidos) >= MAX_CLIENTES_TOTALES;
    }

}
