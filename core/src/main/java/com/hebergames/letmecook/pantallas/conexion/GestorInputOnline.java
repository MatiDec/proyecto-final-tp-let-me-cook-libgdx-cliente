package com.hebergames.letmecook.pantallas.conexion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.hebergames.letmecook.cliente.ClienteRed;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;
import com.hebergames.letmecook.eventos.entrada.DatosEntrada;
import com.hebergames.letmecook.mapa.GestorMapa;
import com.hebergames.letmecook.red.paquetes.PaqueteInteraccion;

public class GestorInputOnline {
    private final ClienteRed CLIENTE_RED;
    private final DatosEntrada ENTRADA_LOCAL;
    private int estacionCercanaIndex = -1;

    private boolean ultimoArriba, ultimoAbajo, ultimoIzquierda, ultimoDerecha, ultimoCorrer;
    private float tiempoDesdeUltimoEnvio = 0;
    private static final float INTERVALO_MINIMO_ENVIO = 0.016f;
    private static final float DISTANCIA_INTERACCION = 150f;

    public GestorInputOnline(ClienteRed CLIENTE_RED) {
        this.CLIENTE_RED = CLIENTE_RED;
        this.ENTRADA_LOCAL = new DatosEntrada();
    }

    public void actualizar(float delta, GestorMapa gestorMapa, Vector2 posJugadorLocal) {
        tiempoDesdeUltimoEnvio += delta;
        detectarEstacionCercana(gestorMapa, posJugadorLocal);
    }

    public void capturarYEnviarInput() {
        capturarInput();
        enviarInputAlServidor();
    }

    private void capturarInput() {
        ENTRADA_LOCAL.arriba = Gdx.input.isKeyPressed(Input.Keys.W);
        ENTRADA_LOCAL.abajo = Gdx.input.isKeyPressed(Input.Keys.S);
        ENTRADA_LOCAL.izquierda = Gdx.input.isKeyPressed(Input.Keys.A);
        ENTRADA_LOCAL.derecha = Gdx.input.isKeyPressed(Input.Keys.D);
        ENTRADA_LOCAL.correr = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT);

        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && estacionCercanaIndex >= 0) {
            CLIENTE_RED.enviarInteraccion(estacionCercanaIndex,
                PaqueteInteraccion.TipoInteraccion.INTERACTUAR_BASICO);
        }

        for (int i = Input.Keys.NUM_1; i <= Input.Keys.NUM_9; i++) {
            if (Gdx.input.isKeyJustPressed(i) && estacionCercanaIndex >= 0) {
                int numero = i - Input.Keys.NUM_0;
                CLIENTE_RED.enviarInteraccion(estacionCercanaIndex,
                    PaqueteInteraccion.TipoInteraccion.SELECCION_MENU, numero - 1);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_0) && estacionCercanaIndex >= 0) {
            CLIENTE_RED.enviarInteraccion(estacionCercanaIndex,
                PaqueteInteraccion.TipoInteraccion.SELECCION_MENU, 9);
        }
    }

    private void enviarInputAlServidor() {
        boolean cambio = ENTRADA_LOCAL.arriba != ultimoArriba ||
            ENTRADA_LOCAL.abajo != ultimoAbajo ||
            ENTRADA_LOCAL.izquierda != ultimoIzquierda ||
            ENTRADA_LOCAL.derecha != ultimoDerecha ||
            ENTRADA_LOCAL.correr != ultimoCorrer;

        if (cambio || tiempoDesdeUltimoEnvio >= INTERVALO_MINIMO_ENVIO) {
            CLIENTE_RED.enviarInput(
                ENTRADA_LOCAL.arriba,
                ENTRADA_LOCAL.abajo,
                ENTRADA_LOCAL.izquierda,
                ENTRADA_LOCAL.derecha,
                ENTRADA_LOCAL.correr
            );

            ultimoArriba = ENTRADA_LOCAL.arriba;
            ultimoAbajo = ENTRADA_LOCAL.abajo;
            ultimoIzquierda = ENTRADA_LOCAL.izquierda;
            ultimoDerecha = ENTRADA_LOCAL.derecha;
            ultimoCorrer = ENTRADA_LOCAL.correr;
            tiempoDesdeUltimoEnvio = 0;
        }
    }

    private void detectarEstacionCercana(GestorMapa gestorMapa, Vector2 posJugadorLocal) {
        estacionCercanaIndex = -1;
        float distanciaMinima = DISTANCIA_INTERACCION;

        for (int i = 0; i < gestorMapa.getEstaciones().size(); i++) {
            float distancia = calcularDistancia(gestorMapa.getEstaciones().get(i), posJugadorLocal);

            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                estacionCercanaIndex = i;
            }
        }
    }

    private float calcularDistancia(EstacionTrabajo estacion, Vector2 posJugadorLocal) {
        float centroEstacionX = estacion.area.x + estacion.area.width / 2f;
        float centroEstacionY = estacion.area.y + estacion.area.height / 2f;

        float centroJugadorX = posJugadorLocal.x + 64;
        float centroJugadorY = posJugadorLocal.y + 64;

        float dx = centroJugadorX - centroEstacionX;
        float dy = centroJugadorY - centroEstacionY;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    public int getEstacionCercanaIndex() {
        return estacionCercanaIndex;
    }
}
