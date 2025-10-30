package com.hebergames.letmecook.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.hebergames.letmecook.cliente.ClienteRed;
import com.hebergames.letmecook.entidades.clientes.VisualizadorClienteRed;
import com.hebergames.letmecook.eventos.entrada.DatosEntrada;
import com.hebergames.letmecook.mapa.GestorMapa;
import com.hebergames.letmecook.mapa.niveles.*;
import com.hebergames.letmecook.pantallas.juego.GestorUIJuego;
import com.hebergames.letmecook.pantallas.juego.GestorViewport;
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

    // Estado visual de jugadores
    private Vector2 posicionJ1;
    private Vector2 posicionJ2;
    private float anguloJ1, anguloJ2;
    private String objetoJ1, objetoJ2;

    // Input local
    private DatosEntrada inputLocal;
    private int miIdJugador;

    // Visualizadores de clientes
    private Map<Integer, VisualizadorClienteRed> visualizadoresClientes;

    public PantallaJuegoOnline(ClienteRed cliente) {
        this.cliente = cliente;
        this.miIdJugador = cliente.getIdJugador();
        this.inputLocal = new DatosEntrada();
        this.visualizadoresClientes = new HashMap<>();

        posicionJ1 = new Vector2();
        posicionJ2 = new Vector2();
        objetoJ1 = "vacio";
        objetoJ2 = "vacio";
    }

    @Override
    public void show() {
        batch = Render.batch;
        gestorViewport = new GestorViewport();
        gestorUI = new GestorUIJuego();

        // Cargar mapa (solo visual)
        GestorPartida gestorPartida = GestorPartida.getInstancia();
        if (gestorPartida.getNivelActual() == null) {
            ArrayList<String> rutasMapas = new ArrayList<>();
            rutasMapas.add(Recursos.RUTA_MAPAS + "Sucursal_1.tmx");
            gestorPartida.generarNuevaPartida(rutasMapas, 1);
        }

        NivelPartida nivel = gestorPartida.getNivelActual();
        gestorMapa = new GestorMapa();
        gestorMapa.setMapaActual(nivel.getMapa());

        // Animaciones para visualización
        gestorAnimacion = new GestorAnimacion(
            Recursos.JUGADOR_SPRITESHEET, 32, 32, 0.2f
        );
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        capturarInput();
        enviarInputAlServidor();
        actualizarEstadoDesdeServidor();

        renderizarJuego(delta);
        renderizarUI();

        verificarFinJuego();
    }

    private void capturarInput() {
        inputLocal.arriba = Gdx.input.isKeyPressed(Input.Keys.W);
        inputLocal.abajo = Gdx.input.isKeyPressed(Input.Keys.S);
        inputLocal.izquierda = Gdx.input.isKeyPressed(Input.Keys.A);
        inputLocal.derecha = Gdx.input.isKeyPressed(Input.Keys.D);
        inputLocal.correr = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);
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

        // Actualizar posiciones y estados de jugadores
        DatosJugador datosJ1 = estado.getJugador1();
        DatosJugador datosJ2 = estado.getJugador2();

        if (datosJ1 != null) {
            posicionJ1.set(datosJ1.x, datosJ1.y);
            anguloJ1 = datosJ1.angulo;
            objetoJ1 = datosJ1.objetoEnMano;
        }

        if (datosJ2 != null) {
            posicionJ2.set(datosJ2.x, datosJ2.y);
            anguloJ2 = datosJ2.angulo;
            objetoJ2 = datosJ2.objetoEnMano;
        }

        // Actualizar clientes visuales
        for (DatosCliente datosCliente : estado.getClientes()) {
            if (!visualizadoresClientes.containsKey(datosCliente.id)) {
                visualizadoresClientes.put(datosCliente.id,
                    new VisualizadorClienteRed(datosCliente));
            } else {
                visualizadoresClientes.get(datosCliente.id).actualizar(datosCliente);
            }
        }

        // Remover clientes que ya no existen
        Set<Integer> idsActuales = new HashSet<>();
        for (DatosCliente dc : estado.getClientes()) {
            idsActuales.add(dc.id);
        }
        visualizadoresClientes.keySet().retainAll(idsActuales);

        // Actualizar UI
        gestorUI.actualizarPuntaje(estado.getPuntaje());
        gestorUI.actualizarTiempo(estado.getTiempoRestante());
    }

    private void renderizarJuego(float delta) {
        gestorViewport.getViewportJuego().apply();

        // Centrar cámara en jugador local
        Vector2 posJugadorLocal = (miIdJugador == 1) ? posicionJ1 : posicionJ2;
        gestorViewport.getCamaraJuego().position.set(
            posJugadorLocal.x + 64, posJugadorLocal.y + 64, 0
        );
        gestorViewport.getCamaraJuego().update();

        gestorMapa.renderizar(gestorViewport.getCamaraJuego());

        batch.setProjectionMatrix(gestorViewport.getCamaraJuego().combined);
        batch.begin();

        // Dibujar jugador 1
        dibujarJugador(posicionJ1.x, posicionJ1.y, anguloJ1, objetoJ1);

        // Dibujar jugador 2
        dibujarJugador(posicionJ2.x, posicionJ2.y, anguloJ2, objetoJ2);

        // Dibujar clientes
        for (VisualizadorClienteRed vis : visualizadoresClientes.values()) {
            vis.dibujar(batch, gestorMapa.getEstaciones());
        }

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

    private void renderizarUI() {
        gestorViewport.getViewportUI().apply();
        gestorViewport.actualizarCamaraUI();

        batch.setProjectionMatrix(gestorViewport.getCamaraUI().combined);
        batch.begin();

        gestorUI.actualizarInventario(objetoJ1, objetoJ2);
        gestorUI.dibujar(batch);

        batch.end();
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
        if (cliente != null) {
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
