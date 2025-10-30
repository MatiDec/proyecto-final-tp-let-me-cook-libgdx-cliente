package com.hebergames.letmecook.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.hebergames.letmecook.cliente.ClienteRed;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.utiles.Render;
import com.hebergames.letmecook.utiles.Recursos;

public class PantallaConexion extends Pantalla {
    private ClienteRed cliente;
    private String ipServidor;
    private Texto textoTitulo;
    private Texto textoIP;
    private Texto textoEstado;
    private Texto textoInstrucciones;
    private SpriteBatch batch;
    private StringBuilder inputIP;
    private boolean intentandoConectar;

    public PantallaConexion() {
        inputIP = new StringBuilder("192.168.0.202");
        intentandoConectar = false;
    }

    @Override
    public void show() {
        batch = Render.batch;

        textoTitulo = new Texto(Recursos.FUENTE_MENU, 48, Color.WHITE, false);
        textoTitulo.setTexto("MULTIJUGADOR ONLINE");
        textoTitulo.setPosition(1280/2f, 900);//esto esta re hardcodeado, dsp hacerlo bien

        textoInstrucciones = new Texto(Recursos.FUENTE_MENU, 24, Color.WHITE, false);
        textoInstrucciones.setTexto("Ingresa la IP del servidor y presiona ENTER");
        textoInstrucciones.setPosition(1280/2f, 700);

        textoIP = new Texto(Recursos.FUENTE_MENU, 32, Color.WHITE, false);
        actualizarTextoIP();
        textoIP.setPosition(1280/2f, 540);

        textoEstado = new Texto(Recursos.FUENTE_MENU, 28, Color.WHITE, false);
        textoEstado.setTexto("");
        textoEstado.setPosition(1280/2f, 400);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        manejarInput();

        // Si hay cliente y está esperando jugadores
        if (cliente != null && cliente.isEsperandoJugadores()) {
            textoEstado.setTexto("Esperando a otro jugador...");
        }

        // Si el juego ya empezó
        if (cliente != null && !cliente.isEsperandoJugadores() &&
            cliente.getUltimoEstado() != null) {
            Pantalla.cambiarPantalla(new PantallaJuegoOnline(cliente));
            return;
        }

        batch.begin();
        textoTitulo.dibujar();
        textoInstrucciones.dibujar();
        textoIP.dibujar();
        textoEstado.dibujar();
        batch.end();
    }

    private void manejarInput() {
        if (intentandoConectar) return;

        // Capturar números y puntos
        for (int i = Input.Keys.NUM_0; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i)) {
                if (inputIP.length() < 15) {
                    inputIP.append(i - Input.Keys.NUM_0);
                    actualizarTextoIP();
                }
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PERIOD) ||
            Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_DOT)) {
            if (inputIP.length() < 15) {
                inputIP.append(".");
                actualizarTextoIP();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
            if (inputIP.length() > 0) {
                inputIP.deleteCharAt(inputIP.length() - 1);
                actualizarTextoIP();
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            intentarConexion();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            // Volver al menú principal
            Pantalla.cambiarPantalla(new PantallaMenu());
        }
    }

    private void intentarConexion() {
        intentandoConectar = true;
        textoEstado.setTexto("Conectando...");
        ipServidor = inputIP.toString();

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

    private void actualizarTextoIP() {
        textoIP.setTexto("IP: " + inputIP.toString() + "_");
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
            cliente.desconectar();
        }
    }
}
