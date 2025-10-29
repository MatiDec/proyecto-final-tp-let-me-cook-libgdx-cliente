package com.hebergames.letmecook.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.entidades.clientes.Cliente;
import com.hebergames.letmecook.entidades.clientes.GestorClientes;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.entregables.productos.Producto;
import com.hebergames.letmecook.entregables.recetas.GestorRecetas;
import com.hebergames.letmecook.entregables.recetas.Receta;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaVirtual;
import com.hebergames.letmecook.estaciones.procesadoras.Procesadora;
import com.hebergames.letmecook.eventos.eventosaleatorios.EventoMaquinaRota;
import com.hebergames.letmecook.eventos.eventosaleatorios.EventoPisoMojado;
import com.hebergames.letmecook.eventos.eventosaleatorios.GestorEventosAleatorios;
import com.hebergames.letmecook.eventos.puntaje.GestorPuntaje;
import com.hebergames.letmecook.mapa.*;
import com.hebergames.letmecook.mapa.indicadores.GestorIndicadores;
import com.hebergames.letmecook.mapa.niveles.GestorPartida;
import com.hebergames.letmecook.mapa.niveles.NivelPartida;
import com.hebergames.letmecook.mapa.niveles.TurnoTrabajo;
import com.hebergames.letmecook.estaciones.interaccionclientes.CajaRegistradora;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.estaciones.interaccionclientes.MesaRetiro;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.pantallas.superposiciones.GestorMostrarCalendario;
import com.hebergames.letmecook.pantallas.superposiciones.GestorPantallasOverlay;
import com.hebergames.letmecook.pantallas.superposiciones.PantallaCalendario;
import com.hebergames.letmecook.pantallas.PantallaFinal;
import com.hebergames.letmecook.pantallas.superposiciones.PantallaPausa;
import com.hebergames.letmecook.pedidos.GestorPedidos;
import com.hebergames.letmecook.sonido.CancionNivel;
import com.hebergames.letmecook.sonido.GestorAudio;
import com.hebergames.letmecook.sonido.SonidoJuego;
import com.hebergames.letmecook.utiles.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PantallaJuego extends Pantalla {

    private final int MIN_CLIENTES_SUCURSAL_CHICA = 10;
    private final int MIN_CLIENTES_SUCURSAL_GRANDE = 20;
    private final int TIEMPO_OBJETIVO = 200;
    private final boolean MODO_MULTIJUGADOR = true;

    private SpriteBatch batch;
    private Jugador jugador1;
    private Jugador jugador2;
    private ArrayList<Jugador> jugadores;
    private GestorMapa gestorMapa;
    private ArrayList<EstacionTrabajo> estaciones;
    private boolean despedido = false;
    private String razonDespido = "";
    private DetectorInactividad detectorInactividad;
    private final float TIEMPO_LIMITE_INACTIVIDAD = 10f;

    private GestorClientes gestorClientes;
    private GestorPedidos gestorPedidos;
    private GestorPuntaje gestorPuntaje;

    private GestorViewport gestorViewport;
    private GestorUIJuego gestorUI;
    private GestorEntradaJuego gestorEntrada;
    private GestorPantallasOverlay gestorOverlays;
    private GestorAudio gestorAudio;
    private GestorAnimacion gestorAnimacionJ1;
    private GestorAnimacion gestorAnimacionJ2;
    private GestorTiempoJuego gestorTiempo;
    private GestorPartida gestorPartida;
    private GestorIndicadores gestorIndicadores;
    private GestorMostrarCalendario gestorMostrarCalendario;
    private NivelPartida nivelActual;

    private GestorTexturas gestorTexturas;
    private Map<String, Animation<TextureRegion>> animacionesConItem = new HashMap<>();
    private Animation<TextureRegion> animacionJugadorNormal;

    @Override
    public void show() {
        gestorTexturas = GestorTexturas.getInstance();
        gestorTexturas.cargarTexturas();
        gestorPartida = GestorPartida.getInstancia();

        if(gestorPartida.getNivelActual() == null) {
            final int CANTIDAD_MAPAS = 4;
            ArrayList<String> rutasMapas = new ArrayList<>();
            for (int i = 1; i <= CANTIDAD_MAPAS; i++) {
                rutasMapas.add(Recursos.RUTA_MAPAS + "Sucursal_" + i + ".tmx");
            }

            gestorPartida.generarNuevaPartida(rutasMapas, rutasMapas.size());
        }

        nivelActual = gestorPartida.getNivelActual();

        inicializarCore();
        inicializarGestores();
        configurarJugadorYMapa();
        GestorJugadores.getInstancia().setJugadores(jugadores);
        configurarEntradaJugadores();
        inicializarAudio();
        inicializarSistemaPedidos();
    }

    private void inicializarCore() {
        batch = Render.batch;
        gestorTiempo = new GestorTiempoJuego(TIEMPO_OBJETIVO);
        gestorIndicadores = new GestorIndicadores();
        jugadores = new ArrayList<>();
        gestorMostrarCalendario = new GestorMostrarCalendario();
    }

    private void inicializarGestores() {
        gestorViewport = new GestorViewport();
        gestorUI = new GestorUIJuego();
        gestorAudio = GestorAudio.getInstance();
        gestorPuntaje = new GestorPuntaje();
    }

    private void configurarJugadorYMapa() {
        configurarTexturasJugadores();

        gestorMapa = new GestorMapa();
        gestorMapa.setMapaActual(nivelActual.getMapa());

        estaciones = gestorMapa.getEstaciones();

        for (EstacionTrabajo estacion : estaciones) {
            if (estacion.getProcesadora() != null && estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();
                if (proc.getIndicador() != null) {
                    gestorIndicadores.registrarIndicador(proc.getIndicador());
                }
            }
        }

        Rectangle spawnJ1 = gestorMapa.getPuntoSpawn("Jugador_1");
        float posXJ1 = (spawnJ1 != null) ? spawnJ1.x + (spawnJ1.width / 2f) - 64 : 1000;
        float posYJ1 = (spawnJ1 != null) ? spawnJ1.y + (spawnJ1.height / 2f) - 64 : 672;

        jugador1 = new Jugador(posXJ1, posYJ1, gestorAnimacionJ1);
        gestorMapa.asignarColisionesYInteracciones(jugador1);
        jugadores.add(jugador1);

        if (MODO_MULTIJUGADOR) {
            Rectangle spawnJ2 = gestorMapa.getPuntoSpawn("Jugador_2");
            float posXJ2 = (spawnJ2 != null) ? spawnJ2.x + (spawnJ2.width / 2f) - 64 : 1000;
            float posYJ2 = (spawnJ2 != null) ? spawnJ2.y + (spawnJ2.height / 2f) - 64 : 872;

            jugador2 = new Jugador(posXJ2, posYJ2, gestorAnimacionJ2);
            gestorMapa.asignarColisionesYInteracciones(jugador2);
            jugadores.add(jugador2);
        }

        for (Jugador j : jugadores) {
            j.setOtrosJugadores(jugadores);
        }

        GestorJugadores.getInstancia().setJugadores(jugadores);
        detectorInactividad = new DetectorInactividad(jugadores, TIEMPO_LIMITE_INACTIVIDAD);
    }

    private void configurarTexturasJugadores() {
        gestorAnimacionJ1 = new GestorAnimacion(
            Recursos.JUGADOR_SPRITESHEET,
            32, 32, 0.2f
        );

        if (MODO_MULTIJUGADOR) {
            gestorAnimacionJ2 = new GestorAnimacion(
                Recursos.JUGADOR_SPRITESHEET,
                32, 32, 0.2f
            );
        }
    }

    private void configurarEntradaJugadores() {
        gestorEntrada = new GestorEntradaJuego(jugadores, estaciones);
        gestorEntrada.configurarEntrada(
            gestorViewport.getViewportJuego(),
            gestorViewport.getViewportUI()
        );
    }

    private void inicializarAudio() {
        gestorAudio.cargarTodasLasMusicasNiveles();
        gestorAudio.cargarTodosLosSonidos();

        TurnoTrabajo turnoActual = nivelActual.getTurno();
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

        GestorRecetas gestorRecetas = GestorRecetas.getInstance();
        ArrayList<Producto> productosDisponibles = new ArrayList<>();

        for (Receta receta : gestorRecetas.getRECETAS()) {
            productosDisponibles.add(receta.preparar());
        }

        TurnoTrabajo turnoActual = nivelActual.getTurno();
        int minClientesRequeridos = calcularMinClientesRequeridos();

        gestorClientes = new GestorClientes(cajas, cajasVirtuales, 15f, turnoActual, minClientesRequeridos);
        gestorPedidos = new GestorPedidos(gestorClientes, mesas);

        gestorClientes.setCallbackPenalizacion((puntos, razon) -> {
            gestorPuntaje.agregarPuntos(puntos);
        });

        for (CajaRegistradora caja : cajas) {
            caja.setGestorPedidos(gestorPedidos);
        }

        for (CajaVirtual cajaVirtual : cajasVirtuales) {
            cajaVirtual.setGestorPedidos(gestorPedidos);
            cajaVirtual.setCallbackPuntaje(gestorPuntaje);
        }

        for (MesaRetiro mesa : mesas) {
            mesa.setGestorPedidos(gestorPedidos);
            mesa.setCallbackPuntaje(gestorPuntaje);
        }



        GestorEventosAleatorios gestorEventos = GestorEventosAleatorios.getInstancia();
        gestorEventos.reset();

        for (EstacionTrabajo estacion : estaciones) {
            if (!(estacion instanceof CajaRegistradora) &&
                !(estacion instanceof MesaRetiro) &&
                !(estacion instanceof CajaVirtual)) {
                gestorEventos.registrarEventoPosible(new EventoMaquinaRota(estacion));
            }
        }

        ArrayList<Rectangle> tilesCaminables = gestorMapa.getTilesCaminables();
        if (!tilesCaminables.isEmpty()) {
            gestorEventos.registrarEventoPosible(new EventoPisoMojado(tilesCaminables));
        }

        gestorEventos.iniciarRonda();
    }

    private int calcularMinClientesRequeridos() {
        int nivelActualIndex = gestorPartida.getNivelActualIndex();
        if (nivelActualIndex == 0 || nivelActualIndex == 2) {
            return MIN_CLIENTES_SUCURSAL_CHICA;
        } else {
            return MIN_CLIENTES_SUCURSAL_GRANDE;
        }
    }

    @Override
    public void render(float delta) {
        limpiarPantalla();
        manejarInput();
        gestorMostrarCalendario.actualizar(delta);

        if (gestorMostrarCalendario.estaMostrando() && gestorOverlays.isCalendarioMostradoAutomaticamente()) {
            gestorOverlays.cerrarCalendarioAutomatico();
        }

        if (!gestorOverlays.isCalendarioVisible()) {
            renderizarJuego(delta);
            renderizarUI();
        }
        renderizarOverlays(delta);
        verificarFinDeJuego();
    }

    private void limpiarPantalla() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    private void manejarInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (gestorOverlays.isCalendarioVisible()) {
                gestorOverlays.toggleCalendario();
            } else {
                togglePausa();
            }
        }

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

                if (!gestorOverlays.isCalendarioVisible()) {
                    gestorEntrada.configurarEntrada(
                        gestorViewport.getViewportJuego(),
                        gestorViewport.getViewportUI()
                    );
                }
            }
        }
    }

    private void renderizarJuego(float delta) {
        gestorViewport.getViewportJuego().apply();

        gestorViewport.actualizarCamaraDinamica(jugador1, jugador2);

        gestorMapa.renderizar(gestorViewport.getCamaraJuego());


        if (!gestorOverlays.isJuegoEnPausa() && !gestorOverlays.isCalendarioVisible() && gestorMostrarCalendario.estaMostrando()) {
            for (Jugador jugador : jugadores) {
                jugador.actualizar(delta);
            }

            if (gestorClientes != null) {
                gestorClientes.actualizar(delta);
            }


            detectorInactividad.actualizar(delta);

            if (!gestorOverlays.isJuegoEnPausa() && !gestorOverlays.isCalendarioVisible() && gestorMostrarCalendario.estaMostrando()) {
                gestorIndicadores.actualizar(delta, gestorViewport.getCamaraJuego());
            }
            for (EstacionTrabajo estacion : estaciones) {
                estacion.verificarDistanciaYLiberar();
            }
            gestorEntrada.actualizarEntradas();
            gestorAudio.reanudarMusica();
        }

        batch.setProjectionMatrix(gestorViewport.getCamaraJuego().combined);
        batch.begin();

        gestorMapa.actualizarEstaciones(delta);
        gestorMapa.dibujarIndicadores(batch);
        gestorIndicadores.dibujar(batch);

        for (Jugador jugador : jugadores) {
            jugador.dibujar(batch);
        }

        for (EstacionTrabajo estacion : estaciones) {
            if (estacion.getProcesadora() instanceof Procesadora) {
                Procesadora proc = (Procesadora) estacion.getProcesadora();
                proc.dibujarEstado(batch);
            }
        }


        if (gestorClientes != null) {
            for (Cliente cliente : gestorClientes.getClientesActivos()) {
                cliente.dibujar(batch);
            }
        }

        batch.end();
    }

    private void renderizarUI() {
        gestorViewport.getViewportUI().apply();
        gestorViewport.actualizarCamaraUI();

        gestorUI.actualizarTiempo(gestorTiempo.getSegundos());

        String itemJ2 = (jugador2 != null) ? jugador2.getNombreItemInventario() : null;
        gestorUI.actualizarInventario(
            jugador1.getNombreItemInventario(),
            itemJ2
        );
        if (gestorClientes != null) {
            int cantidadActual = gestorClientes.getClientesActivos().size();
            if (cantidadActual != gestorClientes.getUltimaCantidadClientes()) {
                gestorUI.actualizarPedidosActivos(gestorClientes.getClientesActivos());
                gestorClientes.actualizarUltimaCantidadClientes();
            }
        }

        gestorUI.actualizarPuntaje(gestorPuntaje.getPuntajeActual());

        batch.setProjectionMatrix(gestorViewport.getCamaraUI().combined);
        batch.begin();
        gestorUI.dibujar(batch);

        if (gestorPedidos != null) {
            gestorUI.dibujarPedidos(batch, gestorPedidos.getPedidosActivos(),
                gestorViewport.getViewportUI().getWorldWidth(),
                gestorViewport.getViewportUI().getWorldHeight());
        }
        for (Jugador jugador : jugadores) {
            if (jugador.estaEnMenu()) {
                EstacionTrabajo estacion = jugador.getEstacionActual();
                if (estacion != null) {
                    estacion.dibujar(batch, jugador);
                }
            }
        }
        batch.end();
    }

    private void renderizarOverlays(float delta) {
        batch.setProjectionMatrix(gestorViewport.getCamaraUI().combined);
        gestorOverlays.renderOverlays(delta, batch);
    }

    private void verificarFinDeJuego() {
        if (detectorInactividad.haySuperadoLimite()) {
            despedido = true;
            razonDespido = "Despedido por inactividad";
            terminarJuego(calcularPuntajeFinal());
            return;
        }
        if (gestorClientes != null && gestorClientes.haAlcanzadoLimiteClientes()) {
            int puntajeFinal = calcularPuntajeFinal();

            if (puntajeFinal < 600) {
                despedido = true;
                razonDespido = "Puntaje insuficiente (menos de 600 puntos)";
                terminarJuego(puntajeFinal);
                return;
            }

            if (gestorClientes.cumpleRequisitoMinimo()) {
                despedido = true;
                razonDespido = "No atendiste a suficientes clientes (" +
                    gestorClientes.getClientesAtendidos() + "/" +
                    gestorClientes.getMinClientesRequeridos() + ")";
                terminarJuego(puntajeFinal);
                return;
            }

            terminarJuego(puntajeFinal);
            return;
        }

        if (gestorTiempo.haTerminadoTiempo()) {
            int puntajeFinal = calcularPuntajeFinal();

            if (puntajeFinal < 600) {
                despedido = true;
                razonDespido = "Puntaje insuficiente (menos de 600 puntos)";
            } else if (gestorClientes != null && gestorClientes.cumpleRequisitoMinimo()) {
                despedido = true;
                razonDespido = "No atendiste a suficientes clientes (" +
                    gestorClientes.getClientesAtendidos() + "/" +
                    gestorClientes.getMinClientesRequeridos() + ")";
            }

            terminarJuego(puntajeFinal);
        }
    }

    private int calcularPuntajeFinal() {
        return gestorPuntaje.getPuntajeActual();
    }

    public void togglePausa() {
        gestorOverlays.togglePausa();

        if (!gestorOverlays.isJuegoEnPausa()) {
            gestorEntrada.configurarEntrada(
                gestorViewport.getViewportJuego(),
                gestorViewport.getViewportUI()
            );
        }
    }

    public void reanudarJuego() {
        gestorOverlays.reanudarJuego();
        gestorEntrada.configurarEntrada(
            gestorViewport.getViewportJuego(),
            gestorViewport.getViewportUI()
        );
    }

    public Animation<TextureRegion> getAnimacionConItem(String nombreItem) {
        Animation<TextureRegion> animacion = animacionesConItem.get(nombreItem);
        if (animacion == null) {
            try {
                String ruta = "core/src/main/java/com/hebergames/letmecook/recursos/imagenes/imagendepruebanomoral" + nombreItem.toLowerCase() + ".png";
                Texture textura = new Texture(Gdx.files.internal(ruta));
                TextureRegion[][] tmp = TextureRegion.split(textura, 32, 32);
                animacion = new Animation<>(0.5f, tmp[0]);
                animacionesConItem.put(nombreItem, animacion);
            } catch (Exception e) {
                Gdx.app.error("Spritesheet", "No se pudo cargar la animacion para: " + nombreItem, e);
                return animacionJugadorNormal;
            }
        }
        return animacion;
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

        gestorOverlays.dispose();

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

    public void terminarJuego(int puntaje) {
        detenerHilos();
        GestorEventosAleatorios.getInstancia().finalizarRonda();
        gestorAudio.detenerMusica();

        if (despedido) {
            gestorAudio.reproducirSonido(SonidoJuego.DESPIDO);
            int puntajeTotal = gestorPartida.getPuntajeTotalPartida() + puntaje;
            Pantalla.cambiarPantalla(new PantallaFinal(gestorTiempo.getTiempoFormateado(), puntajeTotal, true, razonDespido));
        } else {
            gestorAudio.reproducirSonido(SonidoJuego.NIVEL_COMPLETADO);
            boolean hayMasNiveles = gestorPartida.avanzarNivel(puntaje);

            if (hayMasNiveles) {
                Pantalla.cambiarPantalla(new PantallaJuego());
            } else {
                gestorAudio.detenerMusica();
                int puntajeTotal = gestorPartida.getPuntajeTotalPartida();
                Pantalla.cambiarPantalla(new PantallaFinal(gestorTiempo.getTiempoFormateado(), puntajeTotal, false, ""));
            }
        }
    }

    public void detenerHilos() {
        gestorTiempo.detener();
    }
}
