package com.hebergames.letmecook.pantallas.conexion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.cliente.ClienteRed;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.pantallas.Pantalla;
import com.hebergames.letmecook.pantallas.PantallaMenu;
import com.hebergames.letmecook.utiles.Render;
import com.hebergames.letmecook.utiles.Recursos;

public class PantallaConexion extends Pantalla {
    private ClienteRed cliente;
    private String ipServidor;
    private Texto textoTitulo;
    private Texto textoEstado;
    private Texto textoInstrucciones;
    private SpriteBatch batch;
    private boolean intentandoConectar;

    private boolean mostrandoError = false;
    private String mensajeError = "";
    private float tiempoError = 0f;
    private static final float TIEMPO_MOSTRAR_ERROR = 5f;

    public PantallaConexion() {
        intentandoConectar = false;
    }

    @Override
    public void show() {
        batch = Render.batch;

        float anchoPantalla = Gdx.graphics.getWidth();
        float altoPantalla = Gdx.graphics.getHeight();

        textoTitulo = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, false);
        textoTitulo.setTexto("MULTIJUGADOR ONLINE");
        textoTitulo.setPosition(
            anchoPantalla / 2f - textoTitulo.getAncho() / 2f,
            altoPantalla * 0.85f
        );

        textoInstrucciones = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, false);
        textoInstrucciones.setTexto("Ingresa la IP del servidor y presiona ENTER");
        textoInstrucciones.setPosition(
            anchoPantalla / 2f - textoInstrucciones.getAncho() / 2f,
            altoPantalla * 0.65f
        );

        textoEstado = new Texto(Recursos.FUENTE_MENU, 28, Color.WHITE, false);
        textoEstado.setTexto("");
        textoEstado.setPosition(
            anchoPantalla / 2f - textoEstado.getAncho() / 2f,
            altoPantalla * 0.35f
        );
    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        manejarInput();

        if (cliente != null && !cliente.isConectado()) {
            if (cliente.isServidorCerrado()) {
                mostrarError("El servidor se cerró inesperadamente");
            } else if (cliente.isJugadorDesconectado()) {
                String razon = cliente.getRazonDesconexion();

                switch (razon) {
                    case "FIN_PARTIDA":
                        mostrarError("Partida finalizada");
                        break;
                    case "JUGADOR_ABANDONO":
                        mostrarError("El otro jugador abandonó la partida");
                        break;
                    case "DESCONEXION_VOLUNTARIA":
                        limpiarError();
                        break;
                    default:
                        mostrarError(razon);
                        break;
                }
            }
        }

        if (mostrandoError) {
            tiempoError += delta;
            if (tiempoError >= TIEMPO_MOSTRAR_ERROR) {
                limpiarError();
            }
        }

        if (cliente != null && cliente.isEsperandoJugadores()) {
            textoEstado.setTexto("Esperando a otro jugador...");
        }

        if (cliente != null && !cliente.isEsperandoJugadores() &&
            cliente.getUltimoEstado() != null) {
            Pantalla.cambiarPantalla(new PantallaJuegoOnline(cliente));
            return;
        }

        batch.begin();
        textoTitulo.dibujar();
        textoInstrucciones.dibujar();
        textoEstado.dibujar();
        batch.end();
    }

    private void mostrarError(String mensaje) {
        mostrandoError = true;
        mensajeError = mensaje;
        tiempoError = 0f;
        textoEstado.setTexto("ERROR: " + mensaje);
        textoEstado.getFuente().setColor(Color.RED);

        if (cliente != null) {
            cliente.desconectar();
            cliente = null;
        }
        intentandoConectar = false;
    }

    private void limpiarError() {
        mostrandoError = false;
        mensajeError = "";
        textoEstado.setTexto("");
        textoEstado.getFuente().setColor(Color.WHITE);
    }

    private void manejarInput() {
        if (intentandoConectar) return;

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            intentarConexion();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            this.dispose();
            Pantalla.cambiarPantalla(new PantallaMenu());
        }
    }

    private void intentarConexion() {
        intentandoConectar = true;
        textoEstado.setTexto("Conectando...");
        ipServidor = "255.255.255.255";

        new Thread(() -> {
            cliente = new ClienteRed();
            boolean exito = cliente.conectar(ipServidor);

            Gdx.app.postRunnable(() -> {
                if (exito) {
                    textoEstado.setTexto("Conectado! Esperando jugadores...");
                } else {
                    textoEstado.setTexto("Error: No se pudo conectar al servidor");
                    intentandoConectar = false;
                    cliente = null;
                }
            });
        }).start();
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (cliente != null) {
            if (cliente.isConectado()) {
                cliente.desconectar();
            }
            cliente = null;
        }
    }
}
