package com.hebergames.letmecook.pantallas.conexion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.cliente.ClienteRed;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.eventos.eventosaleatorios.EventoPisoMojado;
import com.hebergames.letmecook.eventos.eventosaleatorios.GestorEventosAleatorios;
import com.hebergames.letmecook.mapa.niveles.*;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.pantallas.juego.GestorUIJuego;
import com.hebergames.letmecook.pantallas.juego.GestorViewport;
import com.hebergames.letmecook.pantallas.superposiciones.GestorPantallasOverlay;
import com.hebergames.letmecook.pantallas.superposiciones.PantallaCalendario;
import com.hebergames.letmecook.pantallas.superposiciones.PantallaPausa;
import com.hebergames.letmecook.red.paquetes.*;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.utiles.*;

import java.util.*;

public class PantallaJuegoOnline extends Pantalla {
    private ClienteRed cliente;
    private SpriteBatch batch;
    private int miIdJugador;

    // Gestores principales
    private GestorViewport gestorViewport;
    private GestorUIJuego gestorUI;
    private GestorTexturas gestorTexturas;
    private GestorAudio gestorAudio;
    private GestorPantallasOverlay gestorOverlays;

    // Gestores especializados
    private GestorJugadoresOnline gestorJugadores;
    private GestorInputOnline gestorInput;
    private GestorEstacionesOnline gestorEstaciones;
    private GestorClientesOnline gestorClientes;
    private GestorNivelOnline gestorNivel;
    private GestorFinalizacionOnline gestorFinalizacion;
    private GestorCambioNivelOnline gestorCambioNivel;

    private float tiempoUltimaActualizacion = 0;
    private static final float INTERVALO_ACTUALIZACION_UI = 0.016f;

    public PantallaJuegoOnline(ClienteRed cliente) {
        this.cliente = cliente;
        this.miIdJugador = cliente.getIdJugador();
    }

    @Override
    public void show() {
        inicializarTexturas();
        inicializarGestoresBasicos();
        esperarYCargarConfiguracion();
        inicializarGestoresEspecializados();
        inicializarNivel();
    }

    private void inicializarTexturas() {
        if (!GestorTexturas.getInstance().estanTexturasListas()) {
            GestorTexturas.getInstance().cargarTexturas();
        }
        gestorTexturas = GestorTexturas.getInstance();
    }

    private void inicializarGestoresBasicos() {
        batch = Render.batch;
        gestorViewport = new GestorViewport();
        gestorUI = new GestorUIJuego();
        gestorAudio = GestorAudio.getInstance();
    }

    private void esperarYCargarConfiguracion() {
        PaqueteInicioPartida config = esperarConfiguracionServidor();

        if (config != null) {
            GestorPartida gestorPartida = GestorPartida.getInstancia();
            gestorPartida.generarNuevaPartida(
                config.getNiveles(),
                config.getNiveles().size(),
                true
            );
        }
    }

    private PaqueteInicioPartida esperarConfiguracionServidor() {
        int intentos = 0;
        int maxIntentos = 50;

        while (intentos < maxIntentos) {
            PaqueteInicioPartida config = cliente.getConfiguracionPartida();
            if (config != null) {
                return config;
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            intentos++;
        }

        return null;
    }

    private void inicializarGestoresEspecializados() {
        gestorJugadores = new GestorJugadoresOnline(miIdJugador);
        gestorInput = new GestorInputOnline(cliente);
        gestorEstaciones = new GestorEstacionesOnline(miIdJugador);
        gestorClientes = new GestorClientesOnline();
        gestorNivel = new GestorNivelOnline();
    }

    private void inicializarNivel() {
        NivelPartida nivel = GestorPartida.getInstancia().getNivelActual();
        gestorNivel.inicializarNivel(nivel);
        gestorEstaciones.inicializarEstaciones(gestorNivel.getGestorMapa());
        gestorClientes.setEstaciones(gestorEstaciones.getEstaciones());

        inicializarAudio(nivel);
    }

    private void inicializarAudio(NivelPartida nivel) {
        gestorAudio.cargarTodasLasMusicasNiveles();
        gestorAudio.cargarTodosLosSonidos();
        gestorAudio.reproducirMusicaNivel(nivel.getCancionNivel());
        gestorAudio.pausarMusica();

        PantallaPausa pantallaPausa = new PantallaPausa(this);
        PantallaCalendario pantallaCalendario = new PantallaCalendario(this);
        gestorOverlays = new GestorPantallasOverlay(pantallaPausa, pantallaCalendario, gestorAudio);
        gestorNivel.getGestorMostrarCalendario().iniciarMostrar();
        gestorOverlays.mostrarCalendarioInicial();

        gestorFinalizacion = new GestorFinalizacionOnline(cliente, gestorAudio, gestorNivel.getGestorTiempo());
        gestorCambioNivel = new GestorCambioNivelOnline(cliente, gestorAudio);
    }

    @Override
    public void render(float delta) {
        if (gestorFinalizacion != null && gestorFinalizacion.isJuegoFinalizado()) return;

        if (cliente.isServidorCerrado() || cliente.isJugadorDesconectado()) {
            gestorFinalizacion.finalizarPorDesconexion();
            return;
        }

        limpiarPantalla();
        manejarInput(delta);
        actualizarCalendario(delta);
        actualizarEstado(delta);

        if (!gestorOverlays.isCalendarioVisible()) {
            renderizarJuego(delta);
            renderizarUI();
        }
        renderizarOverlays(delta);

        gestorFinalizacion.verificarFinJuego();
    }

    private void limpiarPantalla() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void manejarInput(float delta) {
        manejarTeclasEspeciales();

        if (!gestorOverlays.isJuegoEnPausa() && !gestorOverlays.isCalendarioVisible()) {
            gestorInput.actualizar(delta, gestorNivel.getGestorMapa(), gestorJugadores.getPosicionJugadorLocal());
            gestorInput.capturarYEnviarInput();
        }
    }

    private void manejarTeclasEspeciales() {
        if (gestorFinalizacion != null && gestorFinalizacion.isJuegoFinalizado()) {
            return;
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (gestorOverlays.isCalendarioVisible()) {
                gestorOverlays.toggleCalendario();
            } else {
                togglePausa();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            if (gestorNivel.getGestorMostrarCalendario().estaMostrando()) {
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
    }

    private void actualizarCalendario(float delta) {
        gestorNivel.getGestorMostrarCalendario().actualizar(delta);

        if (gestorNivel.getGestorMostrarCalendario().estaMostrando() &&
            gestorOverlays.isCalendarioMostradoAutomaticamente()) {
            gestorOverlays.cerrarCalendarioAutomatico();
        }
    }

    private void actualizarEstado(float delta) {
        tiempoUltimaActualizacion += delta;
        if (tiempoUltimaActualizacion >= INTERVALO_ACTUALIZACION_UI) {
            PaqueteEstado estado = cliente.getUltimoEstado();

            gestorJugadores.actualizarDesdeServidor(estado);
            gestorEstaciones.actualizarDesdeServidor(estado, gestorInput.getEstacionCercanaIndex());
            actualizarUI(estado);

            if (gestorCambioNivel.verificarYProcesarCambioNivel()) {
                reinicializarNivel();
            }

            tiempoUltimaActualizacion = 0;
        }
    }

    private void actualizarUI(PaqueteEstado estado) {
        if (estado == null) return;

        gestorUI.actualizarPuntaje(estado.getPuntaje());
        gestorUI.actualizarTiempo(estado.getTiempoRestante());

        DatosJugador datosJ1 = estado.getJugador1();
        DatosJugador datosJ2 = estado.getJugador2();

        String itemJ1 = datosJ1 != null ? datosJ1.objetoEnMano : "Vacío";
        String itemJ2 = datosJ2 != null ? datosJ2.objetoEnMano : "Vacío";
        gestorUI.actualizarInventario(itemJ1, itemJ2);
    }

    private void renderizarJuego(float delta) {

        if (gestorFinalizacion != null && gestorFinalizacion.isJuegoFinalizado()) {
            return;
        }

        gestorViewport.getViewportJuego().apply();
        gestorViewport.actualizarCamaraDinamica(gestorJugadores.getJugador1(), gestorJugadores.getJugador2());

        gestorNivel.getGestorMapa().renderizar(gestorViewport.getCamaraJuego());
        gestorEstaciones.getGestorIndicadores().actualizar(delta, gestorViewport.getCamaraJuego());

        if (!gestorOverlays.isJuegoEnPausa() && !gestorOverlays.isCalendarioVisible()) {
            gestorJugadores.actualizar(delta, false);
            gestorAudio.reanudarMusica();
        }

        batch.setProjectionMatrix(gestorViewport.getCamaraJuego().combined);
        batch.begin();

        gestorNivel.getGestorMapa().dibujarIndicadores(batch);
        gestorEstaciones.getGestorIndicadores().dibujar(batch);
        gestorJugadores.dibujar(batch);

        PaqueteEstado estado = cliente.getUltimoEstado();
        gestorEstaciones.dibujarOverlaysEstaciones(batch, estado);
        gestorClientes.dibujarClientes(batch, estado, gestorNivel.getGestorMapa(), gestorTexturas);

        dibujarEventosPiso(batch);

        batch.end();
    }

    private void dibujarEventosPiso(SpriteBatch batch) {
        GestorEventosAleatorios gestorEventos = GestorEventosAleatorios.getInstancia();
        EventoPisoMojado eventoPiso = gestorEventos.getEventoPisoMojado();
        if (eventoPiso != null) {
            eventoPiso.dibujar(batch);
        }
    }

    private void renderizarUI() {
        gestorViewport.getViewportUI().apply();
        gestorViewport.actualizarCamaraUI();

        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        actualizarUI(estado);

        ArrayList<Cliente> clientesVisuales = gestorClientes.crearClientesVisualesDesdeEstado(estado);
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

        gestorEstaciones.dibujarMenu(batch, estado, gestorInput.getEstacionCercanaIndex(),
            gestorViewport.getViewportUI().getWorldWidth(),
            gestorViewport.getViewportUI().getWorldHeight());

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

    private void reinicializarNivel() {
        NivelPartida nivel = GestorPartida.getInstancia().getNivelActual();

        gestorNivel.reinicializar(nivel);
        gestorEstaciones.reinicializar();
        gestorEstaciones.inicializarEstaciones(gestorNivel.getGestorMapa());
        gestorClientes.limpiar();
        gestorClientes.setEstaciones(gestorEstaciones.getEstaciones());
        gestorJugadores.dispose();
        gestorJugadores = new GestorJugadoresOnline(miIdJugador);

        gestorAudio.detenerMusica();
        gestorAudio.reproducirMusicaNivel(nivel.getCancionNivel());

        if (gestorOverlays != null) {
            gestorOverlays.dispose();
        }
        PantallaPausa pantallaPausa = new PantallaPausa(this);
        PantallaCalendario pantallaCalendario = new PantallaCalendario(this);
        gestorOverlays = new GestorPantallasOverlay(pantallaPausa, pantallaCalendario, gestorAudio);
        gestorOverlays.mostrarCalendarioInicial();

        GestorEventosAleatorios.getInstancia().reset();
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

        if (gestorFinalizacion != null) {
            gestorFinalizacion.marcarComoFinalizado();
        }

        if (cliente != null && cliente.isConectado()) {
            cliente.desconectar();
        }

        if (gestorJugadores != null) {
            gestorJugadores.dispose();
        }

        if (gestorClientes != null) {
            gestorClientes.dispose();
        }

        if (gestorOverlays != null) {
            gestorOverlays.dispose();
        }

        if (gestorUI != null) {
            gestorUI.dispose();
        }

        if (gestorNivel != null) {
            gestorNivel.limpiarRecursos();
        }

        GestorAudio.getInstance().dispose();
        GestorEventosAleatorios.getInstancia().reset();
    }
}
