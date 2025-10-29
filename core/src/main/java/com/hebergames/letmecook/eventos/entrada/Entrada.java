package com.hebergames.letmecook.eventos.entrada;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.estaciones.conmenu.EstacionConMenu;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Entrada implements InputProcessor {

    private final ArrayList<BotonInteractuable> ELEMENTOS_INTERACTUABLES = new ArrayList<>();
    private final Map<Jugador, DatosEntrada> ENTRADAS_POR_JUGADOR = new HashMap<>();
    private final Map<Integer, Jugador> MAPA_TECLAS_JUGADOR = new HashMap<>();
    private final Map<Jugador, ConfiguracionTeclas> CONFIG_TECLAS_JUGADOR = new HashMap<>();
    private final ArrayList<EstacionTrabajo> ESTACIONES = new ArrayList<>();
    private Viewport viewportJuego;
    private Viewport viewportUI;

    private final Map<Jugador, int[]> TECLAS_MENU_POR_JUGADOR = new HashMap<>();
    private final Map<Jugador, Integer> NUMERO_SELECCIONADO = new HashMap<>();

    private CallbackClick callbackClick;

    public void registrar(BotonInteractuable i) {
        ELEMENTOS_INTERACTUABLES.add(i);
    }

    public void registrarJugador(Jugador jugador, ConfiguracionTeclas config) {
        DatosEntrada datos = new DatosEntrada();
        ENTRADAS_POR_JUGADOR.put(jugador, datos);
        CONFIG_TECLAS_JUGADOR.put(jugador, config);

        MAPA_TECLAS_JUGADOR.put(config.getArriba(), jugador);
        MAPA_TECLAS_JUGADOR.put(config.getAbajo(), jugador);
        MAPA_TECLAS_JUGADOR.put(config.getIzquierda(), jugador);
        MAPA_TECLAS_JUGADOR.put(config.getDerecha(), jugador);
        MAPA_TECLAS_JUGADOR.put(config.getInteractuar(), jugador);
        MAPA_TECLAS_JUGADOR.put(config.getCorrer(), jugador);
    }

    public void actualizarEntradas() {
        for (Map.Entry<Jugador, DatosEntrada> entry : ENTRADAS_POR_JUGADOR.entrySet()) {
            Jugador jugador = entry.getKey();
            DatosEntrada datos = entry.getValue();
            ConfiguracionTeclas config = CONFIG_TECLAS_JUGADOR.get(jugador);

            if (config != null) {
                datos.arriba = Gdx.input.isKeyPressed(config.getArriba());
                datos.abajo = Gdx.input.isKeyPressed(config.getAbajo());
                datos.izquierda = Gdx.input.isKeyPressed(config.getIzquierda());
                datos.derecha = Gdx.input.isKeyPressed(config.getDerecha());
                datos.correr = Gdx.input.isKeyPressed(config.getCorrer());
            }

            jugador.manejarEntrada(datos);
        }

        for (Jugador jugador : NUMERO_SELECCIONADO.keySet()) {
            int numeroSeleccionado = NUMERO_SELECCIONADO.get(jugador);

            if (jugador.estaEnMenu()) {
                EstacionTrabajo estacion = jugador.getEstacionActual();
                if (estacion instanceof EstacionConMenu) {
                    ((EstacionConMenu) estacion).manejarSeleccionMenu(jugador, numeroSeleccionado);
                }
            }
        }

        NUMERO_SELECCIONADO.clear();

    }

    public void registrarTeclasMenu(Jugador jugador, int[] teclas) {
        TECLAS_MENU_POR_JUGADOR.put(jugador, teclas);
    }

    @Override
    public boolean keyDown(int keycode) {
        Jugador jugador = MAPA_TECLAS_JUGADOR.get(keycode);
        if(jugador != null) {
            ConfiguracionTeclas config = CONFIG_TECLAS_JUGADOR.get(jugador);

            if (config != null && keycode == config.getInteractuar()) {
                interactuarConEstacionCercana(jugador);
            }

            ENTRADAS_POR_JUGADOR.get(jugador).presionar(keycode);
            return true;
        }

        for (Map.Entry<Jugador, int[]> entry : TECLAS_MENU_POR_JUGADOR.entrySet()) {
            jugador = entry.getKey();
            int[] teclas = entry.getValue();

            if (jugador.estaEnMenu()) {
                for (int i = 0; i < teclas.length; i++) {
                    if (keycode == teclas[i]) {
                        NUMERO_SELECCIONADO.put(jugador, i + 1);
                        return true;
                    }
                }
            }
        }

        return false;
    }

    private void interactuarConEstacionCercana(Jugador jugador) {
        Vector2 posJugador = jugador.getPosicion();

        EstacionTrabajo estacionMasCercana = null;
        float distanciaMasCercana = Float.MAX_VALUE;

        for (EstacionTrabajo estacion : ESTACIONES) {

            if (estacion.estaCerca(posJugador.x, posJugador.y)) {
                float distancia = estacion.calcularDistanciaA(posJugador.x, posJugador.y);

                if (distancia < distanciaMasCercana) {
                    distanciaMasCercana = distancia;
                    estacionMasCercana = estacion;
                }
            }
        }

        if (estacionMasCercana != null) {
            estacionMasCercana.interactuarConJugador(jugador);
        }
    }

    @Override
    public boolean keyUp(int keycode) {
        Jugador jugador = MAPA_TECLAS_JUGADOR.get(keycode);
        if(jugador != null) {
            ConfiguracionTeclas config = CONFIG_TECLAS_JUGADOR.get(jugador);

            if (config != null && keycode == config.getCorrer()) {
                jugador.iniciarDeslizamiento();
            }

            ENTRADAS_POR_JUGADOR.get(jugador).soltar(keycode);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 coordenadasUI = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);

        for (BotonInteractuable i : ELEMENTOS_INTERACTUABLES) {
            if(i.fueClickeado(coordenadasUI.x, coordenadasUI.y)) {
                i.alClick();
                return true;
            }
        }

        if (callbackClick != null) {
            callbackClick.onClick(coordenadasUI.x, coordenadasUI.y);
        }

        return false;
    }

    public void registrarEstacionesTrabajo(List<EstacionTrabajo> estaciones) {
        ESTACIONES.clear();
        ESTACIONES.addAll(estaciones);
    }

    public void setCallbackClick(CallbackClick callback) {
        this.callbackClick = callback;
    }

    public DatosEntrada getDatosEntrada(Jugador jugador) {
        return ENTRADAS_POR_JUGADOR.get(jugador);
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    public void setViewportJuego(Viewport viewport) {
        this.viewportJuego = viewport;
    }

    public void setViewportUI(Viewport viewport) {
        this.viewportUI = viewport;
    }

}
