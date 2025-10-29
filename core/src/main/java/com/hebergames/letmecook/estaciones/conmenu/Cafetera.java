package com.hebergames.letmecook.estaciones.conmenu;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.hebergames.letmecook.elementos.Texto;
import com.hebergames.letmecook.entidades.Jugador;
import com.hebergames.letmecook.entregables.productos.bebidas.Cafe;
import com.hebergames.letmecook.entregables.productos.bebidas.EstadoMenuBebida;
import com.hebergames.letmecook.entregables.productos.bebidas.TamanoBebida;
import com.hebergames.letmecook.utiles.Recursos;

public class Cafetera extends EstacionConMenu {

    private EstadoMenuBebida estadoMenu;
    private String tipoSeleccionado;
    private final String[] TIPOS_DISPONIBLES;
    private TamanoBebida tamanoSeleccionado;
    private int seleccionTamano;
    private int seleccionTipo;

    private Cafe cafeEnPreparacion;
    private float tiempoPreparacion;
    private float tiempoTranscurrido;

    private final Texto TEXTO_MENU;
    private final Texto TEXTO_OPCIONES;

    public Cafetera(Rectangle area) {
        super(area);
        this.estadoMenu = EstadoMenuBebida.SELECCION_TAMANO;
        this.seleccionTamano = 0;
        this.seleccionTipo = 0;
        this.TEXTO_MENU = new Texto(Recursos.FUENTE_MENU, 20, Color.WHITE, true);
        this.TEXTO_OPCIONES = new Texto(Recursos.FUENTE_MENU, 16, Color.YELLOW, true);
        this.TIPOS_DISPONIBLES = Cafe.getTiposCafe().keySet().toArray(new String[0]);
    }

    @Override
    public void alLiberar() {
        if (estadoMenu != EstadoMenuBebida.PREPARANDO && estadoMenu != EstadoMenuBebida.LISTO) {
            estadoMenu = EstadoMenuBebida.SELECCION_TAMANO;
            seleccionTamano = 0;
            seleccionTipo = 0;
        }
    }

    @Override
    public void iniciarMenu(Jugador jugador) {
        if (estadoMenu == EstadoMenuBebida.LISTO && cafeEnPreparacion != null) {
            if (!jugador.tieneInventarioLleno()) {
                jugador.guardarEnInventario(cafeEnPreparacion);
                cafeEnPreparacion = null;
                estadoMenu = EstadoMenuBebida.SELECCION_TAMANO;
                seleccionTamano = 0;
                seleccionTipo = 0;
            }
        }
    }

    @Override
    public void manejarSeleccionMenu(Jugador jugador, int numeroSeleccion) {
        if (estadoMenu == EstadoMenuBebida.PREPARANDO || estadoMenu == EstadoMenuBebida.LISTO) {
            return;
        }

        if (estadoMenu == EstadoMenuBebida.SELECCION_TAMANO) {
            if (numeroSeleccion >= 1 && numeroSeleccion <= 3) {
                seleccionTamano = numeroSeleccion - 1;
                tamanoSeleccionado = TamanoBebida.values()[seleccionTamano];
                estadoMenu = EstadoMenuBebida.SELECCION_TIPO;
                seleccionTipo = 0;
            }
        } else if (estadoMenu == EstadoMenuBebida.SELECCION_TIPO) {
            if (numeroSeleccion >= 1 && numeroSeleccion <= TIPOS_DISPONIBLES.length) {
                seleccionTipo = numeroSeleccion - 1;
                tipoSeleccionado = TIPOS_DISPONIBLES[seleccionTipo];
                iniciarPreparacion();
            }
        }
    }

    private void iniciarPreparacion() {
        cafeEnPreparacion = new Cafe(tipoSeleccionado, tamanoSeleccionado);
        tiempoPreparacion = cafeEnPreparacion.getTiempoPreparacion();
        tiempoTranscurrido = 0f;
        estadoMenu = EstadoMenuBebida.PREPARANDO;
    }

    @Override
    public void actualizar(float delta) {
        super.actualizar(delta);

        if (estadoMenu == EstadoMenuBebida.PREPARANDO) {
            tiempoTranscurrido += delta;
            if (tiempoTranscurrido >= tiempoPreparacion) {
                estadoMenu = EstadoMenuBebida.LISTO;
            }
        }
    }

    @Override
    protected void dibujarMenu(SpriteBatch batch, Jugador jugador) {
        float menuX = 100f;
        float menuY = 400f;

        if (estadoMenu == EstadoMenuBebida.SELECCION_TAMANO) {
            TEXTO_MENU.setTexto("Selecciona tamaño:");
            TEXTO_MENU.setPosition(menuX, menuY + 80);
            TEXTO_MENU.dibujarEnUi(batch);

            String opciones = "1. Pequeño\n2. Mediano\n3. Grande";
            TEXTO_OPCIONES.setTexto(opciones);
            TEXTO_OPCIONES.setPosition(menuX, menuY + 40);
            TEXTO_OPCIONES.dibujarEnUi(batch);

        } else if (estadoMenu == EstadoMenuBebida.SELECCION_TIPO) {
            TEXTO_MENU.setTexto("Selecciona tipo de café:");
            TEXTO_MENU.setPosition(menuX, menuY + 100);
            TEXTO_MENU.dibujarEnUi(batch);

            StringBuilder opciones = new StringBuilder();
            for (int i = 0; i < TIPOS_DISPONIBLES.length; i++) {
                opciones.append((i + 1)).append(". ").append(TIPOS_DISPONIBLES[i]);
                if (i < TIPOS_DISPONIBLES.length - 1) opciones.append("\n");
            }
            TEXTO_OPCIONES.setTexto(opciones.toString());
            TEXTO_OPCIONES.setPosition(menuX, menuY + 40);
            TEXTO_OPCIONES.dibujarEnUi(batch);

        } else if (estadoMenu == EstadoMenuBebida.PREPARANDO) {
            float progreso = (tiempoTranscurrido / tiempoPreparacion) * 100f;
            TEXTO_MENU.setTexto(String.format("Preparando... %.0f%%", progreso));
            TEXTO_MENU.setPosition(menuX, menuY + 40);
            TEXTO_MENU.dibujarEnUi(batch);

        } else if (estadoMenu == EstadoMenuBebida.LISTO) {
            TEXTO_MENU.setTexto("¡Café listo! Presiona E");
            TEXTO_MENU.setPosition(menuX, menuY + 40);
            TEXTO_MENU.dibujarEnUi(batch);
        }
    }

    @Override
    public void alInteractuar() {
        if (estadoMenu == EstadoMenuBebida.LISTO && cafeEnPreparacion != null) {
            return;
        }

        if (estadoMenu == null) {
            estadoMenu = EstadoMenuBebida.SELECCION_TAMANO;
        }
    }
}
