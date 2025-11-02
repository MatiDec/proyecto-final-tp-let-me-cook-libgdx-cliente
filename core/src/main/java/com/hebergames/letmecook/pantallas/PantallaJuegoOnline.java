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
import com.hebergames.letmecook.entregables.productos.CategoriaProducto;
import com.hebergames.letmecook.entregables.productos.GestorProductos;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaRegistradora;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaVirtual;
import com.hebergames.letmecook.estaciones.interaccionclientes.MesaRetiro;
import com.hebergames.letmecook.estaciones.procesadoras.Procesadora;
import com.hebergames.letmecook.eventos.entrada.DatosEntrada;
import com.hebergames.letmecook.eventos.eventosaleatorios.EventoPisoMojado;
import com.hebergames.letmecook.eventos.eventosaleatorios.GestorEventosAleatorios;
import com.hebergames.letmecook.eventos.puntaje.GestorPuntaje;
import com.hebergames.letmecook.mapa.GestorMapa;
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
    private ClienteRed cliente;
    private SpriteBatch batch;
    private GestorViewport gestorViewport;
    private GestorUIJuego gestorUI;
    private GestorMapa gestorMapa;
    private GestorAnimacion gestorAnimacion;
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

    private GestorProductos gestorProductos;

    private static final int TIEMPO_OBJETIVO = 200;

    // Estado visual de jugadores
    private Vector2 posicionJ1;
    private Vector2 posicionJ2;
    private float anguloJ1, anguloJ2;
    private String objetoJ1, objetoJ2;

    // Interpolación de movimiento
    private Vector2 posicionAnteriorJ1 = new Vector2();
    private Vector2 posicionAnteriorJ2 = new Vector2();
    private float anguloAnteriorJ1 = 0f;
    private float anguloAnteriorJ2 = 0f;
    private static final float SUAVIZADO_MOVIMIENTO = 1f; // Mayor = más suave pero con delay

    // Input local
    private DatosEntrada inputLocal;
    private int miIdJugador;

    private int estacionCercanaIndex = -1;
    private static final float DISTANCIA_INTERACCION = 150f;

    private VisualizadorMenuEstacion visualizadorMenu;
    private boolean jugadorEnMenu = false;
    private boolean juegoFinalizado = false;
    private float tiempoUltimaActualizacion = 0;
    private static final float INTERVALO_ACTUALIZACION_UI = 0.1f; // Actualizar UI cada 100ms

    public PantallaJuegoOnline(ClienteRed cliente) {
        this.cliente = cliente;
        this.miIdJugador = cliente.getIdJugador();
        this.inputLocal = new DatosEntrada();
        this.visualizadorMenu = new VisualizadorMenuEstacion();
        this.gestorProductos = new GestorProductos();

        posicionJ1 = new Vector2();
        posicionJ2 = new Vector2();
        objetoJ1 = "vacio";
        objetoJ2 = "vacio";
    }

    @Override
    public void show() {
        GestorTexturas.getInstance().cargarTexturas();
        batch = Render.batch;
        gestorViewport = new GestorViewport();
        gestorUI = new GestorUIJuego();

        // Inicializar gestores adicionales
        gestorTiempo = new GestorTiempoJuego(TIEMPO_OBJETIVO);
        gestorIndicadores = new GestorIndicadores();
        gestorAudio = GestorAudio.getInstance();
        gestorPuntaje = new GestorPuntaje();
        gestorMostrarCalendario = new GestorMostrarCalendario();

        GestorPartida gestorPartida = GestorPartida.getInstancia();
        if (gestorPartida.getNivelActual() == null) {
            ArrayList<String> rutasMapas = new ArrayList<>();
            rutasMapas.add(Recursos.RUTA_MAPAS + "Sucursal_1.tmx");
            gestorPartida.generarNuevaPartida(rutasMapas, 1);
        }

        NivelPartida nivel = gestorPartida.getNivelActual();
        gestorMapa = new GestorMapa();
        gestorMapa.setMapaActual(nivel.getMapa());

        estaciones = gestorMapa.getEstaciones();

        // Registrar indicadores de procesadoras
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

        // Inicializar audio y overlays
        inicializarAudio(nivel);
        inicializarSistemaPedidos();
    }

    private void inicializarAudio(NivelPartida nivel) {
        gestorAudio.cargarTodasLasMusicasNiveles();
        gestorAudio.cargarTodosLosSonidos();

        TurnoTrabajo turnoActual = nivel.getTurno();
        CancionNivel cancionNivel = CancionNivel.getPorTurno(turnoActual);
        gestorAudio.reproducirMusicaNivel(cancionNivel);
        gestorAudio.pausarMusica();

        PantallaPausa pantallaPausa = new PantallaPausa(this);
        PantallaCalendario pantallaCalendario = new PantallaCalendario(this);
        gestorOverlays = new GestorPantallasOverlay(pantallaPausa, pantallaCalendario, gestorAudio);
        gestorMostrarCalendario.iniciarMostrar();
        gestorOverlays.mostrarCalendarioInicial();
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

    private void enviarInputAlServidor() {
        cliente.enviarInput(
            inputLocal.arriba,
            inputLocal.abajo,
            inputLocal.izquierda,
            inputLocal.derecha,
            inputLocal.correr
        );
    }

    private void actualizarEstadoDesdeServidor() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        DatosJugador datosJ1 = estado.getJugador1();
        DatosJugador datosJ2 = estado.getJugador2();

        // Actualizar posiciones y estados de jugadores con interpolación
        if (datosJ1 != null) {
            // Guardar posición anterior si es la primera vez
            if (posicionAnteriorJ1.isZero()) {
                posicionAnteriorJ1.set(datosJ1.x, datosJ1.y);
                anguloAnteriorJ1 = datosJ1.angulo;
            }

            // Interpolación suave de posición (lerp)
            Vector2 posicionObjetivo = new Vector2(datosJ1.x, datosJ1.y);
            posicionAnteriorJ1.lerp(posicionObjetivo, SUAVIZADO_MOVIMIENTO);
            jugador1Local.getPosicion().set(posicionAnteriorJ1);

            // Interpolación suave de ángulo
            float diferenciaAngulo = datosJ1.angulo - anguloAnteriorJ1;
            // Normalizar diferencia de ángulo para tomar el camino más corto
            while (diferenciaAngulo > 180f) diferenciaAngulo -= 360f;
            while (diferenciaAngulo < -180f) diferenciaAngulo += 360f;
            anguloAnteriorJ1 += diferenciaAngulo * SUAVIZADO_MOVIMIENTO;
            jugador1Local.setAnguloRotacion(anguloAnteriorJ1);

            jugador1Local.setObjetoEnMano(datosJ1.objetoEnMano);

//            // Aplicar deslizamiento si está corriendo y luego se detiene TODO hacer que DatosJugador traiga estaCorriendo, velocidadX e Y
//            if (datosJ1.estaCorriendo && datosJ1.velocidadX == 0 && datosJ1.velocidadY == 0) {
//                jugador1Local.iniciarDeslizamiento();
//            }
        }

        if (datosJ2 != null) {
            if (posicionAnteriorJ2.isZero()) {
                posicionAnteriorJ2.set(datosJ2.x, datosJ2.y);
                anguloAnteriorJ2 = datosJ2.angulo;
            }

            Vector2 posicionObjetivo = new Vector2(datosJ2.x, datosJ2.y);
            posicionAnteriorJ2.lerp(posicionObjetivo, SUAVIZADO_MOVIMIENTO);
            jugador2Local.getPosicion().set(posicionAnteriorJ2);

            float diferenciaAngulo = datosJ2.angulo - anguloAnteriorJ2;
            while (diferenciaAngulo > 180f) diferenciaAngulo -= 360f;
            while (diferenciaAngulo < -180f) diferenciaAngulo += 360f;
            anguloAnteriorJ2 += diferenciaAngulo * SUAVIZADO_MOVIMIENTO;
            jugador2Local.setAnguloRotacion(anguloAnteriorJ2);

            jugador2Local.setObjetoEnMano(datosJ2.objetoEnMano);

//            if (datosJ2.estaCorriendo && datosJ2.velocidadX == 0 && datosJ2.velocidadY == 0) {
//                jugador2Local.iniciarDeslizamiento();
//            }
        }

        // Actualizar gestorUI y estaciones
        gestorUI.actualizarPuntaje(estado.getPuntaje());
        gestorUI.actualizarTiempo(estado.getTiempoRestante());
        gestorUI.actualizarInventario(datosJ1.objetoEnMano, datosJ2.objetoEnMano);
    }

    private void actualizarEstacionesDesdeServidor() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        DatosJugador datosLocal = (miIdJugador == 1) ? estado.getJugador1() : estado.getJugador2();

        // Actualizar estados de todas las estaciones
        for (DatosEstacion datosEst : estado.getEstaciones()) {
            if (datosEst.index < 0 || datosEst.index >= estaciones.size()) continue;

            EstacionTrabajo estacion = estaciones.get(datosEst.index);

            // Actualizar estado de procesadoras
            if (estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();
                actualizarEstadoProcesadora(proc, datosEst);
            }

            // Actualizar estado de máquina rota
            //estacion.setFueraDeServicio(datosEst.fueraDeServicio);TODO ESTO LO TENGO QUE MODIFICAR CUANDO ADAPTE QUE EL SERVER LEA LAS MAQ ROTAS
        }

        // Actualizar menú si el jugador está en uno
        if (!datosLocal.estaEnMenu) {
            if (visualizadorMenu.isVisible()) {
                visualizadorMenu.ocultar();
            }
            jugadorEnMenu = false;
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

        jugadorEnMenu = true;
        boolean esJ1 = (miIdJugador == 1);

        switch (datosEst.tipoEstacion) {
            case "Heladera":
                visualizadorMenu.mostrarMenuHeladera(esJ1);
                break;

            case "Mesa":
                // Actualizar menú con los objetos actuales
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
                // Si es otro tipo de estación con menú, ocultar
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

        // Actualizar indicadores si el juego está activo
        if (!gestorOverlays.isJuegoEnPausa() && !gestorOverlays.isCalendarioVisible() && gestorMostrarCalendario.estaMostrando()) {
            gestorIndicadores.actualizar(delta, gestorViewport.getCamaraJuego());
            gestorAudio.reanudarMusica();
        }

        batch.setProjectionMatrix(gestorViewport.getCamaraJuego().combined);
        batch.begin();

        gestorMapa.actualizarEstaciones(delta);
        gestorMapa.dibujarIndicadores(batch);
        gestorIndicadores.dibujar(batch);

        // Dibujar jugadores
        jugador1Local.dibujar(batch);
        jugador2Local.dibujar(batch);

        // Dibujar estados de procesadoras
        for (EstacionTrabajo estacion : estaciones) {
            if (estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();
                proc.dibujarEstado(batch);
            }
        }

        // Dibujar clientes desde el servidor
        PaqueteEstado estado = cliente.getUltimoEstado();
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
        float x = area.x + (area.width / 2f) - (64 / 2f);
        float y = area.y + area.height;

        TextureRegion texturaCliente = GestorTexturas.getInstance().getTexturaCliente();
        if (texturaCliente != null) {
            batch.draw(texturaCliente, x, y, 64, 64);
        }

        // Dibujar barra de tolerancia
        TextureRegion cara = GestorTexturas.getInstance().getCaraPorTolerancia(datos.porcentajeTolerancia);
        if (cara != null) {
            float xCara = x + 32 - 12f;
            float yCara = y + 64 + 4f;
            batch.draw(cara, xCara, yCara, 24f, 24f);
        }
    }

    private void renderizarUI() {
        gestorViewport.getViewportUI().apply();
        gestorViewport.actualizarCamaraUI();

        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado != null) {
            gestorUI.actualizarTiempo(estado.getTiempoRestante());
            gestorUI.actualizarPuntaje(estado.getPuntaje());

            DatosJugador datosJ1 = estado.getJugador1();
            DatosJugador datosJ2 = estado.getJugador2();

            String itemJ1 = datosJ1 != null ? datosJ1.objetoEnMano : "Vacío";
            String itemJ2 = datosJ2 != null ? datosJ2.objetoEnMano : "Vacío";

            gestorUI.actualizarInventario(itemJ1, itemJ2);

            // Actualizar lista de clientes activos SOLO si hay clientes TODO comento el bloque porque no funciona correctamente el método crearClientesVisualesDesdeEstado
//            if (!estado.getClientes().isEmpty()) {
//                ArrayList<Cliente> clientesVisuales = crearClientesVisualesDesdeEstado(estado);
//                if (!clientesVisuales.isEmpty()) {
//                    gestorUI.actualizarPedidosActivos(clientesVisuales);
//                }
//            }
        }

        batch.setProjectionMatrix(gestorViewport.getCamaraUI().combined);
        batch.begin();

        gestorUI.dibujar(batch);

        // Dibujar pedidos SOLO si hay estado y clientes todo el debug no va a funcionar porque el método crearClientesVisualesDesdeEstado funciona mal
//        if (estado != null && !estado.getClientes().isEmpty()) {
//            System.out.println("=== DEBUG CLIENTES ===");
//            System.out.println("Cantidad de clientes del servidor: " + estado.getClientes().size());
//
//            ArrayList<Cliente> clientesVisuales = crearClientesVisualesDesdeEstado(estado);
//            System.out.println("Cantidad de clientes visuales creados: " + clientesVisuales.size());
//
//            for (Cliente c : clientesVisuales) {
//                System.out.println("Cliente - Estado: " + c.getPedido().getEstadoPedido() +
//                    ", Productos: " + c.getPedido().getProductosSolicitados().size() +
//                    ", Tiempo restante: " + c.getTiempoRestante());
//            }
//
//            if (!clientesVisuales.isEmpty()) {
//                gestorUI.dibujarPedidos(batch, clientesVisuales,
//                    gestorViewport.getViewportUI().getWorldWidth(),
//                    gestorViewport.getViewportUI().getWorldHeight());
//            }
//        }

        // Dibujar menús de estaciones si el jugador está en uno
        if (estado != null) {
            DatosJugador datosLocal = (miIdJugador == 1) ? estado.getJugador1() : estado.getJugador2();
            if (datosLocal != null && datosLocal.estaEnMenu && estacionCercanaIndex >= 0) {
                visualizadorMenu.dibujar(batch,
                    gestorViewport.getViewportUI().getWorldWidth(),
                    gestorViewport.getViewportUI().getWorldHeight());
            }
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

        for (DatosCliente dc : estado.getClientes()) {
            // Solo procesar clientes en preparación (los que deben mostrar tarjeta)
            if (!dc.estadoPedido.equals("EN_PREPARACION")) {
                continue;
            }

            // Recuperar los productos visuales reales desde el GestorProductos
            ArrayList<Producto> productos = new ArrayList<>();
            for (String nombreProducto : dc.productosPedido) {
                Producto p = gestorProductos.obtenerProductoPorNombre(nombreProducto);
                if (p != null) {
                    productos.add(p);
                } else {
                    Gdx.app.error("ClientesVisuales", "No se encontró producto: " + nombreProducto);
                }
            }


            // Usar el tiempo restante del servidor directamente
            Cliente clienteVisual = new Cliente(productos, dc.tiempoRestante + 1f, // +1 para evitar que expire inmediatamente
                dc.esVirtual ? TipoCliente.VIRTUAL : TipoCliente.PRESENCIAL);
            clienteVisual.inicializarVisualizador();

            // Asignar estación si es válida
            if (dc.indexEstacion >= 0 && dc.indexEstacion < estaciones.size()) {
                clienteVisual.setEstacionAsignada(estaciones.get(dc.indexEstacion));
            }

            // Forzar el estado del pedido a EN_PREPARACION
            clienteVisual.getPedido().setEstadoPedido(EstadoPedido.EN_PREPARACION);

            clientesVisuales.add(clienteVisual);
        }

        return clientesVisuales;
    }

    private void verificarFinJuego() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado != null && estado.isJuegoTerminado()) {
            terminarJuego(estado.getPuntaje(), estado.getRazonFin());
        }
    }

    private void terminarJuego(int puntaje, String razon) {
        juegoFinalizado = true;
        gestorAudio.detenerMusica();

        cliente.desconectar();

        boolean esDespido = razon != null && !razon.isEmpty();
        if (esDespido) {
            gestorAudio.reproducirSonido(SonidoJuego.DESPIDO);
        } else {
            gestorAudio.reproducirSonido(SonidoJuego.NIVEL_COMPLETADO);
        }

        Pantalla.cambiarPantalla(new PantallaFinal(
            gestorTiempo.getTiempoFormateado(),
            puntaje,
            esDespido,
            razon != null ? razon : ""
        ));
    }

    private void finalizarPorDesconexion() {
        if (juegoFinalizado) return;

        juegoFinalizado = true;

        String razon = "Conexión perdida con el servidor";
        if (cliente.isJugadorDesconectado()) {
            razon = cliente.getRazonDesconexion();
        }

        System.out.println("Finalizando por desconexión: " + razon);

        Gdx.app.postRunnable(() -> {
            cliente.desconectar();
            Pantalla.cambiarPantalla(new PantallaMenu());
        });
    }

    private void detectarEstacionCercana() {
        Vector2 posJugadorLocal = (miIdJugador == 1) ?
            jugador1Local.getPosicion() : jugador2Local.getPosicion(); // Cambiar de posicionJ1/J2 a jugador1Local/jugador2Local

        estacionCercanaIndex = -1;
        float distanciaMinima = DISTANCIA_INTERACCION;

        for (int i = 0; i < gestorMapa.getEstaciones().size(); i++) {
            EstacionTrabajo estacion = gestorMapa.getEstaciones().get(i);

            float centroEstacionX = estacion.area.x + estacion.area.width / 2f;
            float centroEstacionY = estacion.area.y + estacion.area.height / 2f;

            float centroJugadorX = posJugadorLocal.x + 64;
            float centroJugadorY = posJugadorLocal.y + 64;

            float dx = centroJugadorX - centroEstacionX;
            float dy = centroJugadorY - centroEstacionY;
            float distancia = (float) Math.sqrt(dx * dx + dy * dy);

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                estacionCercanaIndex = i;
            }
        }
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
        juegoFinalizado = true;

        if (cliente != null && cliente.isConectado()) {
            cliente.desconectar();
        }

        if (gestorOverlays != null) {
            gestorOverlays.dispose();
        }

        if (gestorUI != null) {
            gestorUI.dispose();
        }

        if (gestorAudio != null) {
            gestorAudio.dispose();
        }

        if (gestorMapa != null) {
            gestorMapa.dispose();
        }

        GestorEventosAleatorios.getInstancia().reset();
    }
}
