package com.hebergames.letmecook.pantallas.juego;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.eventos.entrada.Entrada;
import com.hebergames.letmecook.eventos.entrada.ConfiguracionTeclas;
import com.hebergames.letmecook.estaciones.EstacionTrabajo;

import java.util.ArrayList;
import java.util.List;

public class GestorEntradaJuego {

    private Entrada entrada;
    private final List<Jugador> jugadores;
    private final ArrayList<EstacionTrabajo> estaciones;
    private static GestorEntradaJuego instancia;

    private static final ConfiguracionTeclas CONFIG_JUGADOR_1 = new ConfiguracionTeclas(
        Input.Keys.W,
        Input.Keys.S,
        Input.Keys.A,
        Input.Keys.D,
        Input.Keys.E,
        Input.Keys.SHIFT_LEFT
    );

    private static final ConfiguracionTeclas CONFIG_JUGADOR_2 = new ConfiguracionTeclas(
        Input.Keys.UP,
        Input.Keys.DOWN,
        Input.Keys.LEFT,
        Input.Keys.RIGHT,
        Input.Keys.ENTER,
        Input.Keys.SHIFT_RIGHT
    );

    public static final int[] TECLAS_MENU_JUGADOR_1 = {
        Input.Keys.NUM_1, Input.Keys.NUM_2, Input.Keys.NUM_3, Input.Keys.NUM_4, Input.Keys.NUM_5,
        Input.Keys.NUM_6, Input.Keys.NUM_7, Input.Keys.NUM_8, Input.Keys.NUM_9
    };

    public static final int[] TECLAS_MENU_JUGADOR_2 = {
        Input.Keys.NUMPAD_1, Input.Keys.NUMPAD_2, Input.Keys.NUMPAD_3, Input.Keys.NUMPAD_4, Input.Keys.NUMPAD_5,
        Input.Keys.NUMPAD_6, Input.Keys.NUMPAD_7, Input.Keys.NUMPAD_8, Input.Keys.NUMPAD_9
    };

    public GestorEntradaJuego(List<Jugador> jugadores, ArrayList<EstacionTrabajo> estaciones) {
        this.jugadores = jugadores;
        this.estaciones = estaciones;
        this.entrada = new Entrada();
        instancia = this;
    }

    public static GestorEntradaJuego getInstancia() {
        return instancia;
    }

    public void configurarEntrada(Viewport viewportJuego, Viewport viewportUI) {
        entrada = new Entrada();
        entrada.setViewportJuego(viewportJuego);
        entrada.setViewportUI(viewportUI);
        Gdx.input.setInputProcessor(entrada);

        for (int i = 0; i < jugadores.size(); i++) {
            Jugador jugador = jugadores.get(i);
            ConfiguracionTeclas config = obtenerConfiguracionTeclas(i);
            entrada.registrarJugador(jugador, config);
            if (i == 0) {
                entrada.registrarTeclasMenu(jugador, TECLAS_MENU_JUGADOR_1);
            } else if (i == 1 && jugadores.size() > 1) {
                entrada.registrarTeclasMenu(jugador, TECLAS_MENU_JUGADOR_2);
            }
        }

        entrada.registrarEstacionesTrabajo(estaciones);
    }

    private ConfiguracionTeclas obtenerConfiguracionTeclas(int indiceJugador) {
        if (indiceJugador == 1) {
            return CONFIG_JUGADOR_2;
        }
        return CONFIG_JUGADOR_1;
    }

    public void actualizarEntradas() {
        entrada.actualizarEntradas();
    }

    public Entrada getEntrada() {
        return entrada;
    }

}
