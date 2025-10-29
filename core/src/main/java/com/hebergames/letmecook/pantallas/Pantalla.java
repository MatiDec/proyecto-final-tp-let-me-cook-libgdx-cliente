package com.hebergames.letmecook.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;

import java.util.Stack;

public abstract class Pantalla implements Screen {

    private static final Stack<Pantalla> pilaPantallas = new Stack<>();
    private static Pantalla pantallaActual;

    public static Pantalla getPantallaActual() {
        return pantallaActual;
    }

    public static void cambiarPantalla(Pantalla nuevaPantalla) {
        if (pantallaActual != null) {
            pantallaActual.hide();
            pilaPantallas.push(pantallaActual);
        }
        pantallaActual = nuevaPantalla;
        pantallaActual.show();
    }

    public static void volverPantallaAnterior() {
        if (!pilaPantallas.isEmpty()) {
            pantallaActual.hide();
            pantallaActual = pilaPantallas.pop();
            pantallaActual.show();
            Gdx.input.setInputProcessor(null);
        }
    }

    public static void limpiarPila() {
        pilaPantallas.clear();
    }
}
