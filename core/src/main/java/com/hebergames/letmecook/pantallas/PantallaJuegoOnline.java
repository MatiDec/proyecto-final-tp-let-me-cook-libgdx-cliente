package com.hebergames.letmecook.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.hebergames.letmecook.cliente.ClienteRed;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.entidades.clientes.GestorClientes;
import com.hebergames.letmecook.entidades.clientes.TipoCliente;
import com.hebergames.letmecook.entidades.clientes.VisualizadorCliente;
import com.hebergames.letmecook.entregables.productos.CategoriaProducto;
import com.hebergames.letmecook.entregables.productos.GestorProductos;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaRegistradora;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaVirtual;
import com.hebergames.letmecook.estaciones.interaccionclientes.MesaRetiro;
import com.hebergames.letmecook.estaciones.procesadoras.EstadoMaquina;
import com.hebergames.letmecook.estaciones.procesadoras.Procesadora;
import com.hebergames.letmecook.eventos.entrada.DatosEntrada;
import com.hebergames.letmecook.eventos.eventosaleatorios.EventoPisoMojado;
import com.hebergames.letmecook.eventos.eventosaleatorios.GestorEventosAleatorios;
import com.hebergames.letmecook.eventos.puntaje.GestorPuntaje;
import com.hebergames.letmecook.mapa.GestorMapa;
import com.hebergames.letmecook.mapa.indicadores.EstadoIndicador;
import com.hebergames.letmecook.mapa.indicadores.GestorIndicadores;
import com.hebergames.letmecook.mapa.niveles.*;
import com.hebergames.letmecook.pantallas.juego.GestorTiempoJuego;
import com.hebergames.letmecook.pantallas.juego.GestorUIJuego;
import com.hebergames.letmecook.pantallas.juego.GestorViewport;
import com.hebergames.letmecook.pantallas.superposiciones.GestorMostrarCalendario;
import com.hebergames.letmecook.pantallas.superposiciones.GestorPantallasOverlay;
import com.hebergames.letmecook.pantallas.superposiciones.PantallaCalendario;
import com.hebergames.letmecook.pantallas.superposiciones.PantallaPausa;
import com.hebergames.letmecook.pedidos.EstadoPedido;
import com.hebergames.letmecook.pedidos.GestorPedidos;
import com.hebergames.letmecook.red.VisualizadorMenuEstacion;
import com.hebergames.letmecook.red.paquetes.*;
import com.hebergames.letmecook.sonido.CancionNivel;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.*;

import java.util.*;

public class PantallaJuegoOnline extends Pantalla {
    private final int CANTIDAD_MAPAS = 7;
    private ClienteRed cliente;
    private SpriteBatch batch;
    private GestorViewport gestorViewport;
    private GestorUIJuego gestorUI;
    private GestorMapa gestorMapa;
    private GestorAnimacion gestorAnimacion;
    private GestorTexturas gestorTexturas;
    private Jugador jugador1Local;
    private Jugador jugador2Local;
    private GestorAnimacion gestorAnimacionJ1;
    private GestorAnimacion gestorAnimacionJ2;
    private GestorTiempoJuego gestorTiempo;
    private GestorIndicadores gestorIndicadores;
    private GestorMostrarCalendario gestorMostrarCalendario;
    private GestorPantallasOverlay gestorOverlays;
    private GestorAudio gestorAudio;
    private ArrayList<EstacionTrabajo> estaciones;
    private GestorClientes gestorClientes;
    private GestorPedidos gestorPedidos;
    private GestorPuntaje gestorPuntaje;
    private boolean despedido = false;
    private String razonDespido = "";
    // Añadir en la clase
    private Map<Integer, Cliente> clientesVisualesMap = new HashMap<>();

    // 🎯 Agregar estas variables de clase
    private boolean ultimoArriba, ultimoAbajo, ultimoIzquierda, ultimoDerecha, ultimoCorrer;
    private float tiempoDesdeUltimoEnvio = 0;
    private static final float INTERVALO_MINIMO_ENVIO = 0.016f; // ~60 FPS

    private GestorProductos gestorProductos;

    private static final int TIEMPO_OBJETIVO = 10;

    // Input local
    private DatosEntrada inputLocal;
    private int miIdJugador;

    private int estacionCercanaIndex = -1;
    private static final float DISTANCIA_INTERACCION = 150f;

    private VisualizadorMenuEstacion visualizadorMenu;
    private boolean juegoFinalizado = false;
    private float tiempoUltimaActualizacion = 0;
    private static final float INTERVALO_ACTUALIZACION_UI = 0.016f; // Actualizar UI cada 100ms

    public PantallaJuegoOnline(ClienteRed cliente) {
        this.cliente = cliente;
        this.miIdJugador = cliente.getIdJugador();
        this.inputLocal = new DatosEntrada();
        this.visualizadorMenu = new VisualizadorMenuEstacion();
        this.gestorProductos = new GestorProductos();
    }

    @Override
    public void show() {
        if (!GestorTexturas.getInstance().estanTexturasListas()) {
            GestorTexturas.getInstance().cargarTexturas();
        }
        gestorTexturas = GestorTexturas.getInstance();
        batch = Render.batch;
        gestorViewport = new GestorViewport();
        gestorUI = new GestorUIJuego();

        // Inicializar gestores adicionales
        gestorTiempo = new GestorTiempoJuego(TIEMPO_OBJETIVO);
        gestorIndicadores = new GestorIndicadores();
        gestorAudio = GestorAudio.getInstance();
        gestorPuntaje = new GestorPuntaje();
        gestorMostrarCalendario = new GestorMostrarCalendario();

        // 👇 MODIFICADO - Esperar y usar configuración del servidor
        GestorPartida gestorPartida = GestorPartida.getInstancia();

        // Esperar a recibir configuración del servidor
        PaqueteInicioPartida config = esperarConfiguracionServidor();

        if (config != null) {
            System.out.println("✅ Usando configuración del servidor");
            // Usar la configuración recibida del servidor
            gestorPartida.generarNuevaPartida(
                config.getNiveles(),
                config.getNiveles().size(),
                true
            );
        } else {
            System.out.println("⚠️ No se recibió configuración");
        }

        NivelPartida nivel = gestorPartida.getNivelActual();
        gestorMapa = new GestorMapa();
        gestorMapa.setMapaActual(nivel.getMapa());

        estaciones = gestorMapa.getEstaciones();

        // 👇 Registrar indicadores igual que en PantallaJuego
        for (EstacionTrabajo estacion : estaciones) {
            if (estacion.getProcesadora() != null && estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();
                if (proc.getIndicador() != null) {
                    gestorIndicadores.registrarIndicador(proc.getIndicador());
                }
            }
        }

        gestorAnimacionJ1 = new GestorAnimacion(Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f);
        gestorAnimacionJ2 = new GestorAnimacion(Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f);

        // Crear jugadores locales solo para visualización
        jugador1Local = new Jugador(0, 0, gestorAnimacionJ1);
        jugador2Local = new Jugador(0, 0, gestorAnimacionJ2);

        // Forzar inicialización del frame
        jugador1Local.actualizar(0);
        jugador2Local.actualizar(0);

        // Inicializar audio y overlays con configuración del servidor
        inicializarAudio(nivel, config);
        inicializarSistemaPedidos();
    }

    private void inicializarAudio(NivelPartida nivel, PaqueteInicioPartida config) {
        gestorAudio.cargarTodasLasMusicasNiveles();
        gestorAudio.cargarTodosLosSonidos();

        gestorAudio.reproducirMusicaNivel(nivel.getCancionNivel());
        gestorAudio.pausarMusica();

        PantallaPausa pantallaPausa = new PantallaPausa(this);
        PantallaCalendario pantallaCalendario = new PantallaCalendario(this);
        gestorOverlays = new GestorPantallasOverlay(pantallaPausa, pantallaCalendario, gestorAudio);
        gestorMostrarCalendario.iniciarMostrar();
        gestorOverlays.mostrarCalendarioInicial();
    }

    private PaqueteInicioPartida esperarConfiguracionServidor() {
        System.out.println("⏳ Esperando configuración del servidor...");

        int intentos = 0;
        int maxIntentos = 50; // 5 segundos máximo (50 * 100ms)

        while (intentos < maxIntentos) {
            PaqueteInicioPartida config = cliente.getConfiguracionPartida();
            if (config != null) {
                System.out.println("✅ Configuración recibida del servidor");
                return config;
            }

            try {
                Thread.sleep(100); // Esperar 100ms entre intentos
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            intentos++;
        }

        System.err.println("⚠️ Timeout esperando configuración del servidor");
        return null;
    }

    private void inicializarSistemaPedidos() {
        ArrayList<CajaRegistradora> cajas = new ArrayList<>();
        ArrayList<MesaRetiro> mesas = new ArrayList<>();
        ArrayList<CajaVirtual> cajasVirtuales = new ArrayList<>();

        for (EstacionTrabajo estacion : estaciones) {
            if (estacion instanceof CajaRegistradora) {
                cajas.add((CajaRegistradora) estacion);
            } else if (estacion instanceof MesaRetiro) {
                mesas.add((MesaRetiro) estacion);
            } else if (estacion instanceof CajaVirtual) {
                cajasVirtuales.add((CajaVirtual) estacion);
            }
        }

        // Nota: En modo online, el servidor maneja la lógica de clientes
        // pero mantenemos los gestores para la UI
        gestorClientes = null; // El servidor maneja esto
        gestorPedidos = new GestorPedidos(null, mesas); // Parcialmente funcional

        // Configurar callbacks de puntaje para las estaciones
        for (CajaVirtual cajaVirtual : cajasVirtuales) {
            cajaVirtual.setGestorPedidos(gestorPedidos);
            cajaVirtual.setCallbackPuntaje(gestorPuntaje);
        }

        for (MesaRetiro mesa : mesas) {
            mesa.setGestorPedidos(gestorPedidos);
            mesa.setCallbackPuntaje(gestorPuntaje);
        }

        // Inicializar eventos aleatorios (el servidor los maneja)
        GestorEventosAleatorios gestorEventos = GestorEventosAleatorios.getInstancia();
        gestorEventos.reset();
    }

    @Override
    public void render(float delta) {
        if (juegoFinalizado) return;

        //
        tiempoDesdeUltimoEnvio += delta;
        //

        // Verificar desconexión del servidor
        if (cliente.isServidorCerrado() || cliente.isJugadorDesconectado()) {
            finalizarPorDesconexion();
            return;
        }

        limpiarPantalla();
        manejarInput();
        gestorMostrarCalendario.actualizar(delta);

        if (gestorMostrarCalendario.estaMostrando() && gestorOverlays.isCalendarioMostradoAutomaticamente()) {
            gestorOverlays.cerrarCalendarioAutomatico();
        }

        // Solo actualizar estado desde servidor cada cierto intervalo
        tiempoUltimaActualizacion += delta;
        if (tiempoUltimaActualizacion >= INTERVALO_ACTUALIZACION_UI) {
            actualizarEstadoDesdeServidor();
            actualizarEstacionesDesdeServidor();
            verificarCambioNivel();
            verificarFinJuego();
            tiempoUltimaActualizacion = 0;
        }

        if (!gestorOverlays.isCalendarioVisible()) {
            renderizarJuego(delta);
            renderizarUI();
        }
        renderizarOverlays(delta);

        verificarFinJuego();
    }

    private void limpiarPantalla() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void manejarInput() {
        // Manejo de ESC para pausa/cerrar calendario
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (gestorOverlays.isCalendarioVisible()) {
                gestorOverlays.toggleCalendario();
            } else {
                togglePausa();
            }
        }

        // Manejo de TAB para calendario
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (gestorMostrarCalendario.estaMostrando()) {
                if (gestorOverlays.isCalendarioVisible()) {
                    gestorOverlays.toggleCalendario();
                } else {
                    if (gestorOverlays.isJuegoEnPausa()) {
                        togglePausa();
                    }
                    gestorOverlays.toggleCalendario();
                }
            }
        }

        // Solo capturar input del juego si no hay overlays activos
        if (!gestorOverlays.isJuegoEnPausa() && !gestorOverlays.isCalendarioVisible()) {
            capturarInput();
            enviarInputAlServidor();
        }
    }

    private void capturarInput() {
        inputLocal.arriba = Gdx.input.isKeyPressed(Input.Keys.W);
        inputLocal.abajo = Gdx.input.isKeyPressed(Input.Keys.S);
        inputLocal.izquierda = Gdx.input.isKeyPressed(Input.Keys.A);
        inputLocal.derecha = Gdx.input.isKeyPressed(Input.Keys.D);
        inputLocal.correr = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        // Detectar estación cercana
        detectarEstacionCercana();

        // Tecla de interacción (E)
        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && estacionCercanaIndex >= 0) {
            cliente.enviarInteraccion(estacionCercanaIndex,
                PaqueteInteraccion.TipoInteraccion.INTERACTUAR_BASICO);
        }

        // Teclas numéricas para menús (1-9)
        for (int i = Input.Keys.NUM_1; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i) && estacionCercanaIndex >= 0) {
                int numero = i - Input.Keys.NUM_0;
                cliente.enviarInteraccion(estacionCercanaIndex,
                    PaqueteInteraccion.TipoInteraccion.SELECCION_MENU, numero - 1); // 0-indexed
            }
        }

        // Tecla 0 para preparar/confirmar
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) && estacionCercanaIndex >= 0) {
            cliente.enviarInteraccion(estacionCercanaIndex,
                PaqueteInteraccion.TipoInteraccion.SELECCION_MENU, 9);
        }
    }

//    private void enviarInputAlServidor() {
//        cliente.enviarInput(
//            inputLocal.arriba,
//            inputLocal.abajo,
//            inputLocal.izquierda,
//            inputLocal.derecha,
//            inputLocal.correr
//        );
//    }

    private void enviarInputAlServidor() {
        // 🎯 Solo enviar si cambió el input o pasó suficiente tiempo
        boolean cambio = inputLocal.arriba != ultimoArriba ||
            inputLocal.abajo != ultimoAbajo ||
            inputLocal.izquierda != ultimoIzquierda ||
            inputLocal.derecha != ultimoDerecha ||
            inputLocal.correr != ultimoCorrer;

        if (cambio || tiempoDesdeUltimoEnvio >= INTERVALO_MINIMO_ENVIO) {
            cliente.enviarInput(
                inputLocal.arriba,
                inputLocal.abajo,
                inputLocal.izquierda,
                inputLocal.derecha,
                inputLocal.correr
            );

            ultimoArriba = inputLocal.arriba;
            ultimoAbajo = inputLocal.abajo;
            ultimoIzquierda = inputLocal.izquierda;
            ultimoDerecha = inputLocal.derecha;
            ultimoCorrer = inputLocal.correr;
            tiempoDesdeUltimoEnvio = 0;
        }
    }

//    private void actualizarEstadoDesdeServidor() {
//        PaqueteEstado estado = cliente.getUltimoEstado();
//        if (estado == null) return;
//
//        DatosJugador datosJ1 = estado.getJugador1();
//        DatosJugador datosJ2 = estado.getJugador2();
//
//        if (datosJ1 != null) {
//            // Asignar posición y ángulo directamente sin interpolación
//            jugador1Local.getPosicion().set(datosJ1.x, datosJ1.y);
//            jugador1Local.setAnguloRotacion(datosJ1.angulo);
//            jugador1Local.setObjetoEnMano(datosJ1.objetoEnMano);
//
//            // Mantener deslizamiento si estaba corriendo y se detuvo
//            if (datosJ1.estaCorriendo && datosJ1.velocidadX == 0 && datosJ1.velocidadY == 0) {
//                jugador1Local.iniciarDeslizamiento();
//            }
//        }
//
//        if (datosJ2 != null) {
//            jugador2Local.getPosicion().set(datosJ2.x, datosJ2.y);
//            jugador2Local.setAnguloRotacion(datosJ2.angulo);
//            jugador2Local.setObjetoEnMano(datosJ2.objetoEnMano);
//
//            if (datosJ2.estaCorriendo && datosJ2.velocidadX == 0 && datosJ2.velocidadY == 0) {
//                jugador2Local.iniciarDeslizamiento();
//            }
//        }
//
//        // Actualizar gestorUI y estaciones
//        gestorUI.actualizarPuntaje(estado.getPuntaje());
//        gestorUI.actualizarTiempo(estado.getTiempoRestante());
//        gestorUI.actualizarInventario(datosJ1.objetoEnMano, datosJ2.objetoEnMano);
//    }

    private void actualizarEstadoDesdeServidor() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        DatosJugador datosJ1 = estado.getJugador1();
        DatosJugador datosJ2 = estado.getJugador2();

        // ========== ACTUALIZAR JUGADOR 1 ==========
        if (datosJ1 != null) {

            Vector2 posicionObjetivo = new Vector2(datosJ1.x, datosJ1.y);
            jugador1Local.getPosicion().lerp(posicionObjetivo, 0.3f);
            jugador1Local.setAnguloRotacion(datosJ1.angulo);
            jugador1Local.setObjetoEnMano(datosJ1.objetoEnMano);

            if (datosJ1.estaMoviendose) {
                jugador1Local.setMoviendose(true);
            } else {
                jugador1Local.setMoviendose(false);
            }

            if (datosJ1.estaCorriendo && datosJ1.velocidadX == 0 && datosJ1.velocidadY == 0) {
                jugador1Local.iniciarDeslizamiento();
            }
        }

        // ========== ACTUALIZAR JUGADOR 2 ==========
        if (datosJ2 != null) {
            Vector2 posicionObjetivo = new Vector2(datosJ2.x, datosJ2.y);
            jugador2Local.getPosicion().lerp(posicionObjetivo, 0.3f);

            jugador2Local.setAnguloRotacion(datosJ2.angulo);
            jugador2Local.setObjetoEnMano(datosJ2.objetoEnMano);

            if (datosJ2.estaMoviendose) {
                jugador2Local.setMoviendose(true);
            } else {
                jugador2Local.setMoviendose(false);
            }

            if (datosJ2.estaCorriendo && datosJ2.velocidadX == 0 && datosJ2.velocidadY == 0) {
                jugador2Local.iniciarDeslizamiento();
            }
        }

        gestorUI.actualizarPuntaje(estado.getPuntaje());
        gestorUI.actualizarTiempo(estado.getTiempoRestante());

        String itemJ1 = datosJ1 != null ? datosJ1.objetoEnMano : "Vacío";
        String itemJ2 = datosJ2 != null ? datosJ2.objetoEnMano : "Vacío";
        gestorUI.actualizarInventario(itemJ1, itemJ2);
    }


    private void actualizarEstacionesDesdeServidor() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        DatosJugador datosLocal = (miIdJugador == 1) ? estado.getJugador1() : estado.getJugador2();

        for (DatosEstacion datosEst : estado.getEstaciones()) {
            if (datosEst.index < 0 || datosEst.index >= estaciones.size()) continue;

            EstacionTrabajo estacion = estaciones.get(datosEst.index);

            if (estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();

                if (proc.getIndicador() != null) {
                    EstadoIndicador estadoIndicadorPrevio = proc.getIndicador().getEstado();
                    EstadoIndicador estadoIndicador;

                    // Convertir string a enum
                    if (datosEst.estadoIndicador != null && !datosEst.estadoIndicador.isEmpty()) {
                        try {
                            estadoIndicador = EstadoIndicador.valueOf(datosEst.estadoIndicador);
                        } catch (IllegalArgumentException e) {
                            estadoIndicador = EstadoIndicador.INACTIVO;
                        }
                    } else {
                        estadoIndicador = EstadoIndicador.INACTIVO;
                    }

                    // 🚫 Solo actualizar visibilidad si realmente cambió el estado
                    if (estadoIndicador != estadoIndicadorPrevio) {
                        proc.getIndicador().setEstado(estadoIndicador);
                        System.out.println("🔧 Estación " + datosEst.index + " cambió a: " +
                            estadoIndicador + " (visible=" + proc.getIndicador().isVisible() + ")");
                    }
                }
            }

            estacion.setFueraDeServicio(datosEst.fueraDeServicio);
        }

        if (!datosLocal.estaEnMenu) {
            if (visualizadorMenu.isVisible()) {
                visualizadorMenu.ocultar();
            }
            return;
        }

        if (estacionCercanaIndex < 0) return;

        DatosEstacion datosEst = null;
        for (DatosEstacion de : estado.getEstaciones()) {
            if (de.index == estacionCercanaIndex) {
                datosEst = de;
                break;
            }
        }

        if (datosEst == null) return;

        boolean esJ1 = (miIdJugador == 1);

        switch (datosEst.tipoEstacion) {
            case "Heladera":
                visualizadorMenu.mostrarMenuHeladera(esJ1);
                break;

            case "Mesa":
                visualizadorMenu.mostrarMenuMesa(esJ1, datosEst.objetosEnEstacion);
                break;

            case "Cafetera":
                visualizadorMenu.mostrarMenuCafetera(esJ1, datosEst.estadoMenuBebida, datosEst.progresoPreparacion);
                break;

            case "Fuente":
                visualizadorMenu.mostrarMenuFuente(esJ1, datosEst.estadoMenuBebida, datosEst.progresoPreparacion);
                break;

            case "MaquinaEnvasadora":
                String nombreIngrediente = datosLocal.objetoEnMano;
                visualizadorMenu.mostrarMenuEnvasadora(esJ1, nombreIngrediente);
                break;

            default:
                visualizadorMenu.ocultar();
                break;
        }
    }

    private void actualizarEstadoProcesadora(Procesadora proc, DatosEstacion datosEst) {
        // Obtener las texturas de la máquina
        TextureRegion[] texturas = proc.getTexturaActual() != null ?
            GestorTexturas.getInstance().getTexturasMaquina(datosEst.tipoEstacion.toLowerCase()) : null;

        if (texturas == null) return;

        // Actualizar estado visual según el estado del servidor
        switch (datosEst.estadoIndicador) {
            case "ACTIVA":
                // Máquina activa
                break;
            case "LISTA":
                // Máquina lista
                break;
            case "INACTIVO":
                // Máquina inactiva
                break;
        }
    }

    private void renderizarJuego(float delta) {
        gestorViewport.getViewportJuego().apply();
        gestorViewport.actualizarCamaraDinamica(jugador1Local, jugador2Local);

        gestorMapa.renderizar(gestorViewport.getCamaraJuego());

        gestorIndicadores.actualizar(delta, gestorViewport.getCamaraJuego());

        // Actualizar indicadores si el juego está activo
        if (!gestorOverlays.isJuegoEnPausa() && !gestorOverlays.isCalendarioVisible()) {
            gestorAudio.reanudarMusica();
        }

        if (!gestorOverlays.isJuegoEnPausa() && !gestorOverlays.isCalendarioVisible()) {
            // Actualizar animaciones de jugadores
            jugador1Local.actualizar(delta);
            jugador2Local.actualizar(delta);

            gestorAudio.reanudarMusica();
        }

        batch.setProjectionMatrix(gestorViewport.getCamaraJuego().combined);
        batch.begin();

        //gestorMapa.actualizarEstaciones(delta);
        gestorMapa.dibujarIndicadores(batch);
        gestorIndicadores.dibujar(batch);

        // Dibujar jugadores
        jugador1Local.dibujar(batch);
        jugador2Local.dibujar(batch);

        // 👇 Dibujar overlays de máquinas procesadoras
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado != null) {
            for (DatosEstacion datosEst : estado.getEstaciones()) {
                if (datosEst.index < 0 || datosEst.index >= estaciones.size()) continue;

                EstacionTrabajo estacion = estaciones.get(datosEst.index);

                if (estacion.getProcesadora() instanceof Procesadora) {
                    Procesadora proc = (Procesadora) estacion.getProcesadora();

                    // Obtener textura según estado
                    TextureRegion overlay = null;

                    if (datosEst.estadoMaquina.equals("ACTIVA")) {
                        overlay = GestorTexturas.getInstance()
                            .getTexturaMaquina(datosEst.tipoEstacion.toLowerCase(), EstadoMaquina.ACTIVA);
                    } else if (datosEst.estadoMaquina.equals("LISTA")) {
                        overlay = GestorTexturas.getInstance()
                            .getTexturaMaquina(datosEst.tipoEstacion.toLowerCase(), EstadoMaquina.LISTA);
                    }

                    if (overlay != null) {
                        Rectangle area = estacion.area;
                        batch.draw(overlay, area.x, area.y, area.width, area.height);
                    }
                }
            }
        }

        // Dibujar clientes desde el servidor
        if (estado != null) {
            for (DatosCliente dc : estado.getClientes()) {
                dibujarClienteDesdeServidor(dc);
            }
        }

        // Dibujar evento de piso mojado si está activo
        GestorEventosAleatorios gestorEventos = GestorEventosAleatorios.getInstancia();
        EventoPisoMojado eventoPiso = gestorEventos.getEventoPisoMojado();
        if (eventoPiso != null) {
            eventoPiso.dibujar(batch);
        }

        batch.end();
    }

    private void dibujarClienteDesdeServidor(DatosCliente datos) {
        if (datos.indexEstacion < 0 || datos.indexEstacion >= gestorMapa.getEstaciones().size()) {
            return;
        }

        EstacionTrabajo estacion = gestorMapa.getEstaciones().get(datos.indexEstacion);
        Rectangle area = estacion.area;
        float x = area.x + (area.width / 2f) - 32f; // 64/2 = 32
        float y = area.y + area.height;

        // ✅ Usar instancia cacheada
        TextureRegion texturaCliente = gestorTexturas.getTexturaCliente();
        if (texturaCliente != null) {
            batch.draw(texturaCliente, x, y, 64, 64);
        }

        // ✅ Usar instancia cacheada
        TextureRegion cara = gestorTexturas.getCaraPorTolerancia(datos.porcentajeTolerancia);
        if (cara != null) {
            batch.draw(cara, x + 20f, y + 68f, 24f, 24f); // Precalcular: 32-12=20, 64+4=68
        }
    }

    private void renderizarUI() {
        gestorViewport.getViewportUI().apply();
        gestorViewport.actualizarCamaraUI();

        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        gestorUI.actualizarTiempo(estado.getTiempoRestante());
        gestorUI.actualizarPuntaje(estado.getPuntaje());

        DatosJugador datosJ1 = estado.getJugador1();
        DatosJugador datosJ2 = estado.getJugador2();

        String itemJ1 = datosJ1 != null ? datosJ1.objetoEnMano : "Vacío";
        String itemJ2 = datosJ2 != null ? datosJ2.objetoEnMano : "Vacío";

        gestorUI.actualizarInventario(itemJ1, itemJ2);

        // Crear clientes visuales solo una vez
        ArrayList<Cliente> clientesVisuales = crearClientesVisualesDesdeEstado(estado);
        if (!clientesVisuales.isEmpty()) {
            gestorUI.actualizarPedidosActivos(clientesVisuales);
        }

        batch.setProjectionMatrix(gestorViewport.getCamaraUI().combined);
        batch.begin();

        gestorUI.dibujar(batch);

        if (!clientesVisuales.isEmpty()) {
            gestorUI.dibujarPedidos(batch, clientesVisuales,
                gestorViewport.getViewportUI().getWorldWidth(),
                gestorViewport.getViewportUI().getWorldHeight());
        }

        // Dibujar menús de estaciones si el jugador está en uno
        DatosJugador datosLocal = (miIdJugador == 1) ? datosJ1 : datosJ2;
        if (datosLocal != null && datosLocal.estaEnMenu && estacionCercanaIndex >= 0) {
            visualizadorMenu.dibujar(batch,
                gestorViewport.getViewportUI().getWorldWidth(),
                gestorViewport.getViewportUI().getWorldHeight());
        }

        batch.end();
    }


    private void renderizarOverlays(float delta) {
        batch.setProjectionMatrix(gestorViewport.getCamaraUI().combined);
        gestorOverlays.renderOverlays(delta, batch);
    }

    public void togglePausa() {
        gestorOverlays.togglePausa();
    }

    public void reanudarJuego() {
        gestorOverlays.reanudarJuego();
    }

    private ArrayList<Cliente> crearClientesVisualesDesdeEstado(PaqueteEstado estado) {
        ArrayList<Cliente> clientesVisuales = new ArrayList<>();
        Set<Integer> idsActuales = new HashSet<>();

        for (DatosCliente dc : estado.getClientes()) {
            idsActuales.add(dc.id);

            Cliente clienteVisual = clientesVisualesMap.get(dc.id);

            if (clienteVisual == null) {
                // ✅ Crear solo si no existe
                ArrayList<Producto> productos = new ArrayList<>();
                for (String nombreProducto : dc.productosPedido) {
                    Producto p = gestorProductos.obtenerProductoPorNombre(nombreProducto);
                    if (p != null) productos.add(p);
                }

                clienteVisual = new Cliente(productos, dc.tiempoRestante,
                    dc.esVirtual ? TipoCliente.VIRTUAL : TipoCliente.PRESENCIAL);
                clienteVisual.getPedido().setEstadoPedido(dc.getEstadoPedido());

                // ✅ Crear visualizador UNA SOLA VEZ y reutilizarlo
                TextureRegion texturaCliente = GestorTexturas.getInstance().getTexturaCliente();
                clienteVisual.setVisualizador(new VisualizadorCliente(texturaCliente));

                clientesVisualesMap.put(dc.id, clienteVisual);
            }

            // ✅ Solo actualizar datos dinámicos (no recrear objetos)
            clienteVisual.setPorcentajeTolerancia(dc.porcentajeTolerancia);
            clienteVisual.getPedido().setEstadoPedido(dc.getEstadoPedido());

            if (dc.indexEstacion >= 0 && dc.indexEstacion < estaciones.size()) {
                clienteVisual.setEstacionAsignada(estaciones.get(dc.indexEstacion));
            }

            clientesVisuales.add(clienteVisual);
        }

        // Eliminar clientes que ya no existen
        clientesVisualesMap.keySet().removeIf(id -> !idsActuales.contains(id));

        return clientesVisuales;
    }

    private void verificarCambioNivel() {
        PaqueteCambioNivel paqueteCambio = cliente.getPaqueteCambioNivel();

        if (paqueteCambio != null) {
            cliente.limpiarPaqueteCambioNivel();
            cambiarANuevoNivel(paqueteCambio);
        }
    }

    private void cambiarANuevoNivel(PaqueteCambioNivel paquete) {
        System.out.println("🎮 Cambiando a nivel " + paquete.getNumeroNivel());

        GestorPartida gestorPartida = GestorPartida.getInstancia();
        int nivelCompletadoIndex = paquete.getNumeroNivel() - 1; // El nivel que acabamos de terminar

        if (nivelCompletadoIndex >= 0 && nivelCompletadoIndex < gestorPartida.getTodosLosNiveles().size()) {
            NivelPartida nivelCompletado = gestorPartida.getTodosLosNiveles().get(nivelCompletadoIndex);
            nivelCompletado.marcarCompletado(paquete.getPuntajeNivelCompletado());
            System.out.println("✅ Nivel " + (nivelCompletadoIndex + 1) + " marcado con puntaje: " + paquete.getPuntajeNivelCompletado());
        }

        gestorPartida.sumarPuntajeSinModificarNivel(paquete.getPuntajeNivelCompletado());
        gestorPartida.establecerNivelActual(paquete.getNumeroNivel());
        limpiarRecursosNivel();
        reinicializarNivel();

        gestorAudio.reproducirSonido(SonidoJuego.NIVEL_COMPLETADO);
    }

    private void reinicializarNivel() {
        GestorPartida gestorPartida = GestorPartida.getInstancia();
        NivelPartida nivel = gestorPartida.getNivelActual();

        gestorMapa = new GestorMapa();
        gestorMapa.setMapaActual(nivel.getMapa());
        estaciones = gestorMapa.getEstaciones();
        gestorIndicadores = new GestorIndicadores();
        for (EstacionTrabajo estacion : estaciones) {
            if (estacion.getProcesadora() != null && estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();
                if (proc.getIndicador() != null) {
                    gestorIndicadores.registrarIndicador(proc.getIndicador());
                }
            }
        }

        gestorAnimacionJ1 = new GestorAnimacion(Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f);
        gestorAnimacionJ2 = new GestorAnimacion(Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f);

        jugador1Local = new Jugador(0, 0, gestorAnimacionJ1);
        jugador2Local = new Jugador(0, 0, gestorAnimacionJ2);

        jugador1Local.actualizar(0);
        jugador2Local.actualizar(0);

        gestorAudio.detenerMusica();
        gestorAudio.reproducirMusicaNivel(nivel.getCancionNivel());

        // Limpiar clientes visuales
        clientesVisualesMap.clear();

        // Reiniciar tiempo
        gestorTiempo = new GestorTiempoJuego(TIEMPO_OBJETIVO);

        // Mostrar calendario del nuevo nivel
        gestorMostrarCalendario = new GestorMostrarCalendario();
        gestorMostrarCalendario.iniciarMostrar();

        // 👇 Recrear overlays con nueva referencia
        if (gestorOverlays != null) {
            gestorOverlays.dispose();
        }
        PantallaPausa pantallaPausa = new PantallaPausa(this);
        PantallaCalendario pantallaCalendario = new PantallaCalendario(this);
        gestorOverlays = new GestorPantallasOverlay(pantallaPausa, pantallaCalendario, gestorAudio);
        gestorOverlays.mostrarCalendarioInicial();

        GestorEventosAleatorios.getInstancia().reset();

        System.out.println("✅ Cliente listo para nivel " + gestorPartida.getNivelActualIndex());
    }

    private void limpiarRecursosNivel() {
        if (gestorAnimacionJ1 != null) {
            gestorAnimacionJ1.dispose();
            gestorAnimacionJ1 = null;
        }

        if (gestorAnimacionJ2 != null) {
            gestorAnimacionJ2.dispose();
            gestorAnimacionJ2 = null;
        }

        if (gestorMapa != null) {
            gestorMapa.dispose();
        }

        clientesVisualesMap.clear();

        GestorEventosAleatorios.getInstancia().reset();
    }

    private void verificarFinJuego() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado != null && estado.isJuegoTerminado()) {
            int puntaje = estado.getPuntaje();
            String razon = estado.getRazonFin();

            // Guardar puntaje del nivel actual antes de finalizar
            GestorPartida gestorPartida = GestorPartida.getInstancia();
            int nivelActualIndex = gestorPartida.getNivelActualIndex();

            if (nivelActualIndex >= 0 && nivelActualIndex < gestorPartida.getTodosLosNiveles().size()) {
                NivelPartida nivelActual = gestorPartida.getTodosLosNiveles().get(nivelActualIndex);
                if (!nivelActual.isCompletado()) {
                    nivelActual.marcarCompletado(puntaje - gestorPartida.getPuntajeTotalPartida());
                }
            }

            // Determinar si es despido
            boolean esDespido = razon != null && !razon.isEmpty();

            if (esDespido) {
                despedido = true;
                razonDespido = razon;
            }

            terminarJuego(puntaje);
        }

        // 🔍 Verificar si el otro jugador se desconectó
        if (cliente.isJugadorDesconectado() && !juegoFinalizado) {
            finalizarPorDesconexion();
        }
    }

    private void terminarJuego(int puntaje) {
        juegoFinalizado = true;
        gestorAudio.detenerMusica();

        if (cliente != null && cliente.isConectado()) {
            cliente.desconectar();
        }

        if (despedido) {
            gestorAudio.reproducirSonido(SonidoJuego.DESPIDO);

            GestorPartida gestorPartida = GestorPartida.getInstancia();
            int puntajeTotal = gestorPartida.getPuntajeTotalPartida() + puntaje;

            Pantalla.cambiarPantalla(new PantallaFinal(
                gestorTiempo.getTiempoFormateado(),
                puntajeTotal,
                true,
                razonDespido
            ));
        } else {
            // ✅ Partida completada exitosamente
            GestorPartida gestorPartida = GestorPartida.getInstancia();
            int puntajeTotal = gestorPartida.getPuntajeTotalPartida();

            gestorAudio.reproducirSonido(SonidoJuego.NIVEL_COMPLETADO);

            Pantalla.cambiarPantalla(new PantallaFinal(
                gestorTiempo.getTiempoFormateado(),
                puntajeTotal,
                false,
                ""
            ));
        }
    }

    private void finalizarPorDesconexion() {
        if (juegoFinalizado) return;

        juegoFinalizado = true;

        String razon = "Conexión perdida con el servidor";
        if (cliente.isJugadorDesconectado()) {
            razon = cliente.getRazonDesconexion();
        }

        System.out.println("🔴 Finalizando por desconexión: " + razon);

        // 🔥 NO desconectar aquí - se hará en dispose()
        Gdx.app.postRunnable(() -> {
            Pantalla.cambiarPantalla(new PantallaConexion());
        });
    }

    private void detectarEstacionCercana() {
        Vector2 posJugadorLocal = (miIdJugador == 1) ?
            jugador1Local.getPosicion() : jugador2Local.getPosicion(); // Cambiar de posicionJ1/J2 a jugador1Local/jugador2Local

        estacionCercanaIndex = -1;
        float distanciaMinima = DISTANCIA_INTERACCION;

        for (int i = 0; i < gestorMapa.getEstaciones().size(); i++) {
            float distancia = getDistancia(i, posJugadorLocal);

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                estacionCercanaIndex = i;
            }
        }
    }

    private float getDistancia(int i, Vector2 posJugadorLocal) {
        EstacionTrabajo estacion = gestorMapa.getEstaciones().get(i);

        float centroEstacionX = estacion.area.x + estacion.area.width / 2f;
        float centroEstacionY = estacion.area.y + estacion.area.height / 2f;

        float centroJugadorX = posJugadorLocal.x + 64;
        float centroJugadorY = posJugadorLocal.y + 64;

        float dx = centroJugadorX - centroEstacionX;
        float dy = centroJugadorY - centroEstacionY;
        float distancia = (float) Math.sqrt(dx * dx + dy * dy);
        return distancia;
    }

    @Override
    public void resize(int width, int height) {
        gestorViewport.resize(width, height);
        gestorUI.actualizarPosiciones(
            gestorViewport.getViewportUI().getWorldWidth(),
            gestorViewport.getViewportUI().getWorldHeight()
        );
    }

    @Override
    public void pause() {
        if (gestorAudio != null) {
            gestorAudio.pausarMusica();
        }
    }

    @Override
    public void resume() {
        if (gestorAudio != null && !gestorOverlays.isJuegoEnPausa()) {
            gestorAudio.reanudarMusica();
        }
    }

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        System.out.println("🧹 Limpiando PantallaJuegoOnline...");

        juegoFinalizado = true;

        // 🔥 Desconectar cliente UNA SOLA VEZ
        if (cliente != null && cliente.isConectado()) {
            cliente.desconectar();
            cliente = null;
        }

        // Liberar clientes visuales
        for (Cliente c : clientesVisualesMap.values()) {
            c.liberarRecursos();
        }
        clientesVisualesMap.clear();

        // Liberar animaciones
        if (gestorAnimacionJ1 != null) {
            gestorAnimacionJ1.dispose();
            gestorAnimacionJ1 = null;
        }

        if (gestorAnimacionJ2 != null) {
            gestorAnimacionJ2.dispose();
            gestorAnimacionJ2 = null;
        }

        // Liberar overlays
        if (gestorOverlays != null) {
            gestorOverlays.dispose();
            gestorOverlays = null;
        }

        // Liberar UI
        if (gestorUI != null) {
            gestorUI.dispose();
            gestorUI = null;
        }

        // Liberar mapa
        if (gestorMapa != null) {
            gestorMapa.dispose();
            gestorMapa = null;
        }

        GestorAudio.getInstance().dispose();

        GestorEventosAleatorios.getInstancia().reset();

        System.out.println("✅ PantallaJuegoOnline limpiada");
    }

    //esto de aca es porque me quedaba de fondo consumiendo ram cada vez que cerraba con la x el game aunque ahora no lo implemente
    public void disposeFinal() {

        GestorFuentes.getInstance().dispose();
        GestorTexturas.getInstance().dispose();
    }
}
