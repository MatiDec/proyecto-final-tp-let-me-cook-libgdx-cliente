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
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.eventos.entrada.DatosEntrada;
import com.hebergames.letmecook.mapa.GestorMapa;
import com.hebergames.letmecook.mapa.niveles.*;
import com.hebergames.letmecook.pantallas.juego.GestorUIJuego;
import com.hebergames.letmecook.pantallas.juego.GestorViewport;
import com.hebergames.letmecook.red.VisualizadorMenuEstacion;
import com.hebergames.letmecook.red.paquetes.*;
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

    // Estado visual de jugadores
    private Vector2 posicionJ1;
    private Vector2 posicionJ2;
    private float anguloJ1, anguloJ2;
    private String objetoJ1, objetoJ2;

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

        GestorPartida gestorPartida = GestorPartida.getInstancia();
        if (gestorPartida.getNivelActual() == null) {
            ArrayList<String> rutasMapas = new ArrayList<>();
            rutasMapas.add(Recursos.RUTA_MAPAS + "Sucursal_1.tmx");
            gestorPartida.generarNuevaPartida(rutasMapas, 1); // Cliente con renderizado
        }

        NivelPartida nivel = gestorPartida.getNivelActual();
        gestorMapa = new GestorMapa();
        gestorMapa.setMapaActual(nivel.getMapa());

        gestorAnimacionJ1 = new GestorAnimacion(Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f);
        gestorAnimacionJ2 = new GestorAnimacion(Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f);

        // Crear jugadores locales solo para visualización
        jugador1Local = new Jugador(0, 0, gestorAnimacionJ1);
        jugador2Local = new Jugador(0, 0, gestorAnimacionJ2);

        // Forzar inicialización del frame
        jugador1Local.actualizar(0);
        jugador2Local.actualizar(0);
    }

    @Override
    public void render(float delta) {
        if (juegoFinalizado) return;

        // Verificar desconexión del servidor
        if (cliente.isServidorCerrado() || cliente.isJugadorDesconectado()) {
            finalizarPorDesconexion();
            return;
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        capturarInput();
        enviarInputAlServidor();

        // Solo actualizar estado desde servidor cada cierto intervalo
        tiempoUltimaActualizacion += delta;
        if (tiempoUltimaActualizacion >= INTERVALO_ACTUALIZACION_UI) {
            actualizarEstadoDesdeServidor();
            actualizarMenusEstaciones();
            tiempoUltimaActualizacion = 0;
        }

        renderizarJuego(delta);
        renderizarIndicadorEstacion();
        renderizarUI();

        verificarFinJuego();
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

        // Actualizar posiciones y estados de jugadores locales
        if (datosJ1 != null) {
            jugador1Local.getPosicion().set(datosJ1.x, datosJ1.y);
            jugador1Local.setAnguloRotacion(datosJ1.angulo);
            jugador1Local.setObjetoEnMano(datosJ1.objetoEnMano);
        }

        if (datosJ2 != null) {
            jugador2Local.getPosicion().set(datosJ2.x, datosJ2.y);
            jugador2Local.setAnguloRotacion(datosJ2.angulo);
            jugador2Local.setObjetoEnMano(datosJ2.objetoEnMano);
        }

        // Actualizar clientes (crear/actualizar visualizadores)
        Map<Integer, DatosCliente> clientesActuales = new HashMap<>();
        for (DatosCliente dc : estado.getClientes()) {
            clientesActuales.put(dc.id, dc);
        }

        // Actualizar gestorUI
        gestorUI.actualizarPuntaje(estado.getPuntaje());
        gestorUI.actualizarTiempo(estado.getTiempoRestante());
        gestorUI.actualizarInventario(datosJ1.objetoEnMano, datosJ2.objetoEnMano);
    }

    private void actualizarMenusEstaciones() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        DatosJugador datosLocal = (miIdJugador == 1) ? estado.getJugador1() : estado.getJugador2();

        if (!datosLocal.estaEnMenu) {
            if (visualizadorMenu.isVisible()) {
                visualizadorMenu.ocultar();
            }
            jugadorEnMenu = false;
            return;
        }

        // El jugador está en un menú, determinar cuál estación
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
        }
    }

    private void renderizarJuego(float delta) {
        gestorViewport.getViewportJuego().apply();

        Vector2 posJugadorLocal = (miIdJugador == 1) ?
            jugador1Local.getPosicion() : jugador2Local.getPosicion();

        gestorViewport.actualizarCamaraDinamica(jugador1Local, jugador2Local);

        gestorMapa.renderizar(gestorViewport.getCamaraJuego());

        batch.setProjectionMatrix(gestorViewport.getCamaraJuego().combined);
        batch.begin();

        jugador1Local.dibujar(batch);
        jugador2Local.dibujar(batch);

        // Dibujar clientes usando VisualizadorCliente normal
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado != null) {
            for (DatosCliente dc : estado.getClientes()) {
                dibujarClienteDesdeServidor(dc);
            }

//            // Dibujar estados de estaciones
//            for (DatosEstacion de : estado.getEstaciones()) {
//                dibujarEstadoEstacion(de);
//            }
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

    private void renderizarIndicadorEstacion() {
        if (estacionCercanaIndex < 0) return;

        EstacionTrabajo estacion = gestorMapa.getEstaciones().get(estacionCercanaIndex);

        batch.setProjectionMatrix(gestorViewport.getCamaraJuego().combined);
        batch.begin();

        // Dibujar indicador "E" sobre la estación
        float x = estacion.area.x + estacion.area.width / 2 - 16;
        float y = estacion.area.y + estacion.area.height + 40;

        // Aquí podrías dibujar un sprite o texto "E"
        // Por ahora, solo un rectángulo visual
        batch.setColor(1f, 1f, 0f, 0.8f); // Amarillo semi-transparente
        batch.draw(Recursos.PIXEL, x, y, 32, 32);
        batch.setColor(1f, 1f, 1f, 1f); // Restaurar color

        batch.end();
    }

    private void dibujarJugador(float x, float y, float angulo, String objeto) {
        Animation<TextureRegion> animacion = gestorAnimacion.getAnimacionPorObjeto(objeto);
        TextureRegion frame = animacion.getKeyFrame(0, true);

        float width = 128;
        float height = 128;
        float originX = width / 2f;
        float originY = height / 2f;

        batch.draw(frame, x, y, originX, originY, width, height, 1f, 1f, angulo);
    }

    private void dibujarObjetosEstaciones() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        for (DatosEstacion datosEst : estado.getEstaciones()) {
            if (datosEst.objetosEnEstacion.isEmpty()) continue;

            EstacionTrabajo estacion = gestorMapa.getEstaciones().get(datosEst.index);

            float x = estacion.area.x + 10;
            float y = estacion.area.y + estacion.area.height - 40;

            // Dibujar cada objeto en la mesa
            for (int i = 0; i < datosEst.objetosEnEstacion.size(); i++) {
                String nombreObjeto = datosEst.objetosEnEstacion.get(i);

                // Aquí deberías obtener la textura del objeto
                // Por ahora, dibujamos un placeholder
                batch.setColor(0.5f, 0.5f, 1f, 1f);
                batch.draw(Recursos.PIXEL, x + (i * 35), y, 30, 30);
                batch.setColor(1f, 1f, 1f, 1f);
            }
        }
    }

    private void dibujarIndicadoresProcesadoras() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        for (DatosEstacion datosEst : estado.getEstaciones()) {
            if (!datosEst.procesando && datosEst.estadoIndicador.equals("INACTIVO")) {
                continue;
            }

            EstacionTrabajo estacion = gestorMapa.getEstaciones().get(datosEst.index);

            float x = estacion.area.x + estacion.area.width / 2 - 20;
            float y = estacion.area.y + estacion.area.height + 10;

            // Color según el estado
            Color color = Color.WHITE;
            switch (datosEst.estadoIndicador) {
                case "PROCESANDO":
                    color = Color.YELLOW;
                    break;
                case "LISTO":
                    color = Color.GREEN;
                    break;
                case "QUEMANDOSE":
                    color = Color.RED;
                    break;
            }

            // Fondo de la barra
            batch.setColor(Color.DARK_GRAY);
            batch.draw(Recursos.PIXEL, x, y, 40, 8);

            // Barra de progreso (siempre llena si está procesando o lista)
            batch.setColor(color);
            float anchoProgreso = 40f * datosEst.progresoProceso;
            batch.draw(Recursos.PIXEL, x, y, anchoProgreso, 8);

            batch.setColor(1f, 1f, 1f, 1f); // Restaurar color
        }
    }

    private void dibujarProgresoBebidasPreparando() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado == null) return;

        for (DatosEstacion datosEst : estado.getEstaciones()) {
            if (!datosEst.estadoMenuBebida.equals("PREPARANDO")) {
                continue;
            }

            EstacionTrabajo estacion = gestorMapa.getEstaciones().get(datosEst.index);

            float x = estacion.area.x + estacion.area.width / 2 - 25;
            float y = estacion.area.y + estacion.area.height + 20;

            // Fondo de la barra
            batch.setColor(Color.DARK_GRAY);
            batch.draw(Recursos.PIXEL, x, y, 50, 10);

            // Barra de progreso
            batch.setColor(Color.CYAN);
            float anchoProgreso = 50f * datosEst.progresoPreparacion;
            batch.draw(Recursos.PIXEL, x, y, anchoProgreso, 10);

            batch.setColor(1f, 1f, 1f, 1f);
        }
    }

    private void renderizarUI() {
        gestorViewport.getViewportUI().apply();
        gestorViewport.actualizarCamaraUI();

        batch.setProjectionMatrix(gestorViewport.getCamaraUI().combined);
        batch.begin();

        gestorUI.actualizarInventario(objetoJ1, objetoJ2);
        gestorUI.dibujar(batch);

        // Dibujar pedidos de clientes
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado != null && !estado.getClientes().isEmpty()) {
            // Convertir DatosCliente a lista temporal para dibujar
            // (esto es una simplificación, idealmente tendríamos objetos Cliente reales)
            float anchoUI = gestorViewport.getViewportUI().getWorldWidth();
            float altoUI = gestorViewport.getViewportUI().getWorldHeight();

            dibujarTarjetasPedidos(batch, estado.getClientes(), anchoUI, altoUI);
        }

        // Dibujar menú de estación si está visible
        visualizadorMenu.dibujar(batch,
            gestorViewport.getViewportUI().getWorldWidth(),
            gestorViewport.getViewportUI().getWorldHeight());

        batch.end();
    }

    private void dibujarTarjetasPedidos(SpriteBatch batch, ArrayList<DatosCliente> clientes, float anchoUI, float altoUI) {
        float yInicial = altoUI / 2f;
        float x = anchoUI - 220f;
        int maxVisibles = Math.min(clientes.size(), 5);

        for (int i = 0; i < maxVisibles; i++) {
            DatosCliente datosCliente = clientes.get(i);
            float y = yInicial - (i * 120f);

            dibujarTarjetaPedido(batch, datosCliente, x, y);
        }
    }

    private void dibujarTarjetaPedido(SpriteBatch batch, DatosCliente datosCliente, float x, float y) {
        final float ANCHO_TARJETA = 200f;
        final float ALTO_TARJETA = 100f;

        // Fondo de la tarjeta (usando ShapeRenderer o un pixel)
        batch.setColor(0.2f, 0.2f, 0.2f, 0.8f);
        batch.draw(Recursos.PIXEL, x, y, ANCHO_TARJETA, ALTO_TARJETA);

        // Barra de tolerancia
        Color colorBarra = datosCliente.porcentajeTolerancia > 0.6f ? Color.GREEN :
            datosCliente.porcentajeTolerancia > 0.3f ? Color.YELLOW : Color.RED;
        batch.setColor(colorBarra);
        batch.draw(Recursos.PIXEL, x, y, ANCHO_TARJETA * datosCliente.porcentajeTolerancia, 5f);

        batch.setColor(1f, 1f, 1f, 1f);

        // Textura del cliente
        TextureRegion texturaCliente = GestorTexturas.getInstance().getTexturaCliente();
        if (texturaCliente != null) {
            batch.draw(texturaCliente, x + 10, y + ALTO_TARJETA - 74, 64, 64);
        }

        // Cara según tolerancia
        TextureRegion cara = GestorTexturas.getInstance().getCaraPorTolerancia(datosCliente.porcentajeTolerancia);
        if (cara != null) {
            batch.draw(cara, x + 10 + 32 - 12, y + ALTO_TARJETA - 10 - 12, 24, 24);
        }

        // Productos solicitados (máximo 3)
        int cantidadAMostrar = Math.min(datosCliente.productosPedido.size(), 3);
        for (int i = 0; i < cantidadAMostrar; i++) {
            String nombreProducto = datosCliente.productosPedido.get(i);
            String claveTextura = nombreProducto.toLowerCase().replace(" ", "");

            TextureRegion texturaProducto = GestorTexturas.getInstance().getTexturaProducto(claveTextura);
            if (texturaProducto != null) {
                float offset = i * 16f;
                float tam = 64f * 0.8f;
                batch.draw(texturaProducto,
                    x + ANCHO_TARJETA - tam - 10 - offset,
                    y + ALTO_TARJETA - tam - 10 - offset,
                    tam, tam);
            }
        }

        // Tiempo restante
        int segundos = (int) datosCliente.tiempoRestante;
        Texto textoTiempo = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
        textoTiempo.setTexto(segundos + "s");
        textoTiempo.setPosition(
            x + (ANCHO_TARJETA / 2f) - (textoTiempo.getAncho() / 2f),
            y + (ALTO_TARJETA / 2f) + (textoTiempo.getAlto() / 2f)
        );
        textoTiempo.dibujarEnUi(batch);
    }

    private void verificarFinJuego() {
        PaqueteEstado estado = cliente.getUltimoEstado();
        if (estado != null && estado.isJuegoTerminado()) {
            cliente.desconectar();
            Pantalla.cambiarPantalla(new PantallaFinal(
                "Tiempo agotado",
                estado.getPuntaje(),
                false,
                estado.getRazonFin()
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
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        juegoFinalizado = true;

        if (cliente != null && cliente.isConectado()) {
            cliente.desconectar();
        }
        if (gestorUI != null) {
            gestorUI.dispose();
        }
        if (gestorMapa != null) {
            gestorMapa.dispose();
        }
    }
}
